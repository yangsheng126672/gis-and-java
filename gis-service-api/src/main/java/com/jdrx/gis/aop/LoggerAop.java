package com.jdrx.gis.aop;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.jdrx.gis.beans.entry.log.GisTransLog;
import com.jdrx.gis.dao.log.GisTransLogMapper;
import com.jdrx.gis.util.HttpUtil;
import com.jdrx.gis.util.JsonFormatUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: liaosijun
 * @Time: 2019/11/7 10:22
 */
//@Aspect
//@Component
public class LoggerAop {
	// 日志
	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(LoggerAop.class);

	/**
	 * 线程池 异步记录日志
	 */
	private static ExecutorService logExecutorService =  Executors.newCachedThreadPool();

	@Autowired
	private GisTransLogMapper gisTransLogMapper;

	// 拦截api接口下面的所有类所有方法
	@Pointcut("execution(* com.jdrx.gis.api..*(..))")
	public void logcut() {
	}

	String ipp = "";
	/**
	 * 拦截来自api的请求，目的把belongToIds（权限分配的ID数组）和gis系统中的权限ID做映射
	 *
	 * @param proceedingJoinPoint
	 * @return
	 * @throws Throwable
	 */
	@Before(value = "logcut()")
	public void before(JoinPoint joinPoint) throws Throwable {
//		ipp = HttpUtil.getIpAddress();
		System.out.println("aop before 前置 ：" + Thread.currentThread().getId() + " = " + Thread.currentThread().getName());
	}
	@AfterReturning(value = "logcut()")
	public void afterReturning(JoinPoint joinPoint) throws Throwable {
		System.out.println("aop afterReturning 返回 ：" + Thread.currentThread().getId() + " = " + Thread.currentThread().getName());
	}

	@AfterThrowing(value = "logcut()", throwing = "throwable")
	public void afterThrowing(JoinPoint joinPoint , Throwable throwable) throws Throwable {
		System.out.println("aop afterThrowing 异常 ：" + Thread.currentThread().getId() + " = " + Thread.currentThread().getName());
	}
	@After(value = "logcut()")
	public void after(JoinPoint joinPoint) throws Throwable {
		System.out.println("aop after 最后 ：" + Thread.currentThread().getId() + " = " + Thread.currentThread().getName());
	}
	@Around(value = "logcut()")
	public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		String ip = HttpUtil.getIpAddress(request);
		Object[] argObjs = proceedingJoinPoint.getArgs();
		String reqParams = "";
		Logger.debug("==========================" + JsonFormatUtil.formatJson(JSONObject.toJSONString(argObjs)));
		Object[] paramObjs = proceedingJoinPoint.getArgs();
//		request
		MethodSignature signature = ((MethodSignature) proceedingJoinPoint.getSignature());
		GisTransLog gisTransLog = new GisTransLog();
		String transId = String.valueOf(UUID.randomUUID()).replaceAll("-","");
		gisTransLog.setApi(transId);
//		proceedingJoinPoint.
		System.out.println("aop around 环绕 ：" + Thread.currentThread().getId() + " = " + Thread.currentThread().getName());
		String xsldfjs = "蔚来绝壁";
		System.out.println("------------1-----------");
		logExecutorService.execute(() -> {
			try {
				System.out.println("--------------2------------------");
				System.out.println("aop around 线程池 ：" + Thread.currentThread().getId() + " = " + Thread.currentThread().getName());

				Logger.debug("ip=" + ipp + "xs" +  xsldfjs);
				System.out.println("-------------------3--------------");

//				String transId = String.valueOf(UUID.randomUUID()).replaceAll("-","");
//
//				MethodSignature signature = ((MethodSignature) proceedingJoinPoint.getSignature());
//				String[] argNames = signature.getParameterNames();
//				Object[] argObjs = proceedingJoinPoint.getArgs();

			} catch (Exception e) {
				e.printStackTrace();
			}

		});
		System.out.println("--------------------4------------");
//		if (Objects.isNull(argObjs) | Objects.isNull(argNames)) {
//			return proceedingJoinPoint.proceed(argObjs);
//		}
//		int containKey = containKey(argObjs, argNames);
//		if (containKey == 0) {
//			return proceedingJoinPoint.proceed(argObjs);
//		}
//		List<DictDetailPO> dictDetailPOList = dictDetailService.findDetailsByTypeVal(dictConfig.getAuthId());
//		Map map = Maps.newHashMap();
//		if (Objects.nonNull(dictDetailPOList) && dictDetailPOList.size() > 0) {
//			dictDetailPOList.stream().forEach(dictDetailPO -> {
//				String val = dictDetailPO.getVal();
//				if (Objects.nonNull(val)) {
//					String[] authIds = val.split("=");
//					if (Objects.nonNull(authIds) && authIds.length == 2) {
//						map.put(authIds[0], authIds[1]);
//					}
//				}
//			});
//			Logger.debug(String.format("数据权限ID映射列表%s-%s",map.keySet(), map.values()));
//		}
//
//		for (Object argObj : argObjs) {
//			if (1 == containKey) {
//				// 参数如果是belongToIds
//				ArrayList req_authIds = (ArrayList) argObj;
//				if (Objects.nonNull(req_authIds) && req_authIds.size() > 0) {
//					for (int i = 0; i < req_authIds.size(); i++) {
//						Object authId = req_authIds.get(i);
//						req_authIds.set(i, Long.parseLong(String.valueOf(map.get(String.valueOf(authId)))));
//					}
////					signature.getMethod().invoke(proceedingJoinPoint.getTarget(), argObjs); // 可以不用方法添加接收字段，可直接调用
//					return proceedingJoinPoint.proceed(argObjs);
//				}
//			} else if (2 == containKey) {
//				// 如果是GIS系统自定义的DTO参数
//				JSONObject paramObj = JSONObject.parseObject(JSONObject.toJSONString(argObj));
//				Object authIds = paramObj.get(GISConstants.BELONG_TO_IDS);
//				if (authIds instanceof List) {
//					JSONArray jsonArray_auIds = (JSONArray) authIds;
//					if (Objects.nonNull(jsonArray_auIds) && jsonArray_auIds.size() > 0) {
//						for (int i = 0; i < jsonArray_auIds.size(); i++) {
//							Object authId = jsonArray_auIds.get(i);
//							jsonArray_auIds.set(i, Long.parseLong(String.valueOf(map.get(String.valueOf(authId)))));
//						}
//					}
//				}
//				Method setBelongToIds = argObj.getClass().getMethod("setBelongToIds", List.class);
//				setBelongToIds.invoke(argObj, authIds);
//				return proceedingJoinPoint.proceed(argObjs);
//			}
//		}
//		return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
	}
//
//
//	/**
//	 * 请求参数中是否包含belongToIds，如果有的话，区分是放在dto还是直接放在最外层
//	 *
//	 * @param argObjs
//	 * @param argNames
//	 * @return
//	 */
//	private int containKey(Object[] argObjs, String[] argNames) {
//		for (Object argObj : argObjs) {
//			for (String argName : argNames) {
//				// 参数如果是belongToIds
//				if (GISConstants.BELONG_TO_IDS.equals(argName) && argObj instanceof List) {
//					return 1;
//				}
//				String pacakgeName = argObj.getClass().getName();
//				// 如果是GIS系统自定义的DTO参数
//				if (pacakgeName.startsWith("com.jdrx.gis.beans.dto")) {
//					JSONObject paramObj = JSONObject.parseObject(JSONObject.toJSONString(argObj));
//					if (Objects.nonNull(paramObj) && paramObj.containsKey(GISConstants.BELONG_TO_IDS)) {
//						return 2;
//					}
//				}
//			}
//		}
//		return 0;
//	}
}
