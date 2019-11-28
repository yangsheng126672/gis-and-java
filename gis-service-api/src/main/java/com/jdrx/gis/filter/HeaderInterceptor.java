package com.jdrx.gis.filter;

import com.jdrx.gis.filter.assist.OcpService;
import com.jdrx.gis.util.ComUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Objects;
import java.util.Properties;


/**
 * 拦截header的deptPath,做数据权限控制
 */
@Intercepts({
		@Signature(type = StatementHandler.class,
				method = "prepare",
				args = {Connection.class, Integer.class})
})
@Component
public class HeaderInterceptor implements Interceptor {

	private static Logger logger = LoggerFactory.getLogger(HeaderInterceptor.class);

	private static final String DEPT_PATH = "deptPath";
	private static final String T_DEV = "share_dev";
	private static final String T_EXT = "gis_dev_ext";
	private String matchs;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		HttpServletRequest request = null;
		String deptPath = null;
		if (RequestContextHolder.getRequestAttributes() != null) {
			request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		} else {
			return invocation.proceed();
		}
		if (Objects.nonNull(request)) {
			deptPath = request.getHeader(DEPT_PATH);
		} else {
			throw new Exception(" http header " + DEPT_PATH + " not found! ");
		}
		if (invocation.getTarget() instanceof RoutingStatementHandler) {
			if (StringUtils.isNotBlank(deptPath)) {
				RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
				StatementHandler delegate = (StatementHandler) getFieldValue(statementHandler, "delegate");
				MappedStatement mappedStatement = (MappedStatement)getFieldValue(delegate, "mappedStatement");
				SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
				String commandName = sqlCommandType.name();
				BoundSql boundSql = delegate.getBoundSql();
				String sql = boundSql.getSql();
				if ("SELECT".equalsIgnoreCase(commandName)) {
					sql = handleSql(sql, deptPath);
				}
				setFieldValue(boundSql, "sql", sql);
			} else {
				throw new Exception(" http header " + DEPT_PATH + " not found! ");
			}
		}
		return invocation.proceed();
	}

	/**
	 * 处理sql, 先替换gis_ext_dev,再替换share_dev
	 * @param sql
	 * @param deptPath
	 * @return
	 * @throws Exception
	 */
	private static String handleSql(String sql, String deptPath) throws Exception {
		StringBuffer sb1 = new StringBuffer().append("(SELECT a.* FROM ")
				.append(T_DEV)
				.append(" a INNER JOIN (SELECT * FROM ")
				.append(T_EXT)
				.append(" WHERE belong_to = ~) b ON a.id = b.dev_id)");
		StringBuffer sb2 = new StringBuffer().append("(SELECT * FROM ")
				.append(T_EXT)
				.append(" WHERE belong_to = ~)");

		if (Objects.nonNull(deptPath) && Objects.nonNull(sql)) {
			Long deptId = new OcpService().setDeptPath(deptPath).getUserWaterworksDeptId();
			if (Objects.nonNull(deptId)) {
				sql = ComUtil.replaceTargetIngorCase(sql, T_EXT, String.valueOf(sb2)).replaceAll("\\~", String.valueOf(deptId));
				sql = ComUtil.replaceTargetIngorCase(sql, T_DEV, String.valueOf(sb1)).replaceAll("\\~", String.valueOf(deptId));
			}
		}
		return sql;
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	public static Object getFieldValue(Object obj, String fieldName) {

		if (obj == null) {
			return null;
		}

		Field targetField = getTargetField(obj.getClass(), fieldName);

		try {
			return FieldUtils.readField(targetField, obj, true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Field getTargetField(Class<?> targetClass, String fieldName) {
		Field field = null;

		try {
			if (targetClass == null) {
				return field;
			}

			if (Object.class.equals(targetClass)) {
				return field;
			}

			field = FieldUtils.getDeclaredField(targetClass, fieldName, true);
			if (field == null) {
				field = getTargetField(targetClass.getSuperclass(), fieldName);
			}
		} catch (Exception e) {
		}

		return field;
	}

	public static void setFieldValue(Object obj, String fieldName, Object value) {
		if (null == obj) {
			return;
		}
		Field targetField = getTargetField(obj.getClass(), fieldName);
		try {
			FieldUtils.writeField(targetField, obj, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void setProperties(Properties properties) {
		this.matchs = properties.getProperty("matchs", null);
	}

}