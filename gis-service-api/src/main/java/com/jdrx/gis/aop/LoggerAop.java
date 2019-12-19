
package com.jdrx.gis.aop;

import com.alibaba.fastjson.JSONObject;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.entity.log.GisTransLog;
import com.jdrx.gis.config.JwtConfig;
import com.jdrx.gis.dao.basic.DictDetailPOMapper;
import com.jdrx.gis.dao.log.GisTransLogMapper;
import com.jdrx.gis.util.HttpUtil;
import com.jdrx.gis.util.JsonFormatUtil;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.awt.AWTCharset;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: liaosijun
 * @Time: 2019/11/7 10:22
 */
@Aspect
@Component
public class LoggerAop {
	// 日志
	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(LoggerAop.class);

	/**
	 * 线程池 异步记录日志
	 */
	private static ExecutorService logExecutorService =  Executors.newCachedThreadPool();

	private static final ThreadLocal<GisTransLog> gisTransLogLocal = new ThreadLocal<>();

	@Autowired
	private GisTransLogMapper gisTransLogMapper;

	@Autowired
	private DictDetailPOMapper dictDetailPOMapper;

	@Autowired
	private JwtConfig jwtConfig;

	// 拦截api接口下面的所有类所有方法，所有要求api目录下不要包含其他方法
	@Pointcut("execution(* com.jdrx.gis.api..*(..))")
	public void logcut() {
	}

	/**
	 * 日志记录
	 * @param proceedingJoinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around(value = "logcut()")
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Class<?> targetClass = AopUtils.getTargetClass(proceedingJoinPoint.getTarget());
		Logger.debug("被代理的目标类 ：" + targetClass);
		Long start = System.currentTimeMillis();
		// 交易ID
		String transId = String.valueOf(UUID.randomUUID()).replaceAll("-","");
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		String ip = HttpUtil.getIpAddress(request);
		Object[] argObjs = proceedingJoinPoint.getArgs();


		// 获取接口的描述
		MethodSignature signature = ((MethodSignature) proceedingJoinPoint.getSignature());
		Method targetMethod = signature.getMethod();
		ApiOperation apiOperation = targetMethod.getAnnotation(ApiOperation.class);
		//获取方法上@ApiOperation注解的value值,
		String apiName = "";
		if (Objects.nonNull(apiOperation)) {
			apiName = apiOperation.value();
		}

		// 获取登录用户名称
		Enumeration headerNames = request.getHeaderNames();
		String loginUserName = "";
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			if (GISConstants.TRANSPARENT_TOKEN_FEILD.equalsIgnoreCase(key)) {
				JwtParser parser = Jwts.parser();
				parser.setSigningKey(jwtConfig.getSigningKey());
				DefaultClaims body = (DefaultClaims) parser.parse(value).getBody();
				loginUserName = Objects.nonNull(body.get(GISConstants.REAL_NAME)) ?
						String.valueOf(body.get(GISConstants.REAL_NAME)) : "";
				break;
			}
		}

		// 获取接口请求
		RequestMapping methodRm = targetMethod.getAnnotation(RequestMapping.class);
		RequestMapping classRm = proceedingJoinPoint.getTarget().getClass().getAnnotation(RequestMapping.class);
		String[] methodRms = methodRm.value();
		String[] classRms = classRm.value();
		String seprator = "/";
		StringBuffer api = new StringBuffer().append(seprator);
		if (Objects.nonNull(methodRm) && Objects.nonNull(classRms)) {
			api.append(classRms[0]).append(seprator).append(methodRms[0]); // gis系统都是配置一个请求path
		}

		String transCode = dictDetailPOMapper.getTransCodeByApiPath(String.valueOf(api));

		GisTransLog gisTransLog = new GisTransLog();
		gisTransLog.setTransId(transId);

		gisTransLog.setApiName(apiName);
		gisTransLog.setReqHost(ip);
		gisTransLog.setApi(String.valueOf(api));
		gisTransLog.setCreateAt(new Date());
		gisTransLog.setTransCode(transCode);
		gisTransLog.setOperator(loginUserName);
		gisTransLog.setCost(start.intValue()); // 异常时用
		gisTransLogLocal.set(gisTransLog);
		String reqParams = "";
		if (Objects.nonNull(argObjs) && argObjs.length > 0) {
			try {
				if (isValidJsonObject(argObjs)) {
					reqParams = JSONObject.toJSONString(argObjs);
				}
			} catch (Exception e) {
				Logger.debug("转换请求参数为JSON字符串失败！参数类型 ： " + e.getMessage());
			}
		}
		Logger.debug("Request-----------------" + transId + "-----------------" + transCode + "-----------------" +  apiName + "\n"
				+ JsonFormatUtil.formatJson(reqParams));
		gisTransLog.setReqParams(reqParams);

		ResposeVO resposeVO = (ResposeVO) proceedingJoinPoint.proceed(argObjs);
		Long end = System.currentTimeMillis();
		Long cost = end - start;
		gisTransLog.setCost(cost.intValue());
		String returnCode = resposeVO.getStatus();
		String returnMsg = resposeVO.getMessage();
		gisTransLog.setReturnCode(returnCode);
		gisTransLog.setReturnMsg(returnMsg);


		logExecutorService.execute(() -> {
			synchronized (Logger) {
				Logger.debug("Response-----------------" + transId + "-----------------" + transCode + "\n"
						+ JsonFormatUtil.formatJson(JSONObject.toJSONString(resposeVO)));
			}
			try {
				String req = gisTransLog.getReqParams();
				if (Objects.nonNull(req) && req.length() > 1024) {
					req = "请求数据大于1024，请根据trans_id到日志中查看";
					gisTransLog.setReqParams(req);
				}
				gisTransLogMapper.insertSelective(gisTransLog);
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error("保存GIS日志记录失败！");
			}
		});
		return resposeVO;
	}

	@AfterThrowing(value = "logcut()", throwing = "exception")
	public void afterThrowing(JoinPoint joinPoint, Exception exception) throws Throwable {
		exception.printStackTrace();
		GisTransLog gisTransLog = gisTransLogLocal.get();
		Long start =  gisTransLog.getCost().longValue();
		Long end = System.currentTimeMillis();
		Long cost = end - start;
		if (Objects.nonNull(gisTransLog)) {
			gisTransLog.setCost(cost.intValue());
			gisTransLog.setReturnCode("1");
			String returnMsg = exception.getMessage();
			gisTransLog.setReturnMsg(returnMsg);
			if (Objects.nonNull(returnMsg) && returnMsg.length() > 512) {
				returnMsg = "响应消息大于512，请根据trans_id到日志中查看";
				gisTransLog.setReturnMsg(returnMsg);
			}
		}
		logExecutorService.execute(() -> {
			try {
				String req = gisTransLog.getReqParams();
				if (Objects.nonNull(req) && req.length() > 1024) {
					req = "请求数据大于1024，请根据trans_id到日志中查看";
					gisTransLog.setReqParams(req);
				}
				gisTransLogMapper.insertSelective(gisTransLog);
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error("保存GIS日志记录失败！");
			}
		});
	}

	public boolean isValidJsonObject(Object[] argObjs) {
		try {
			JSONObject.toJSONString(argObjs);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
