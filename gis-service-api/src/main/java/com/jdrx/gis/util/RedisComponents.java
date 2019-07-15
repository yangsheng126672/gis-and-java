package com.jdrx.gis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName:RedisComponents
 * @Description:redis组件服务
 * @author: yangfang
 * @date: 2017/9/15 16:09
 */

@Service
public class RedisComponents {

	@Autowired
	private StringRedisTemplate redisTemplate;
	/**
	 * 删除key
	 *
	 * @return
	 * @return: long 下午3:54:53
	 */
	public long delKey(final String key) {
		return (long) redisTemplate.execute(new RedisCallback() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				long result = 0;
				result = connection.del(key.getBytes());
				return result;
			}
		});
	}
	/**
	 * 删除key
	 *
	 * @param keys
	 * @return
	 * @return: long 下午3:54:53
	 */
	public long del(final String... keys) {
		return (long) redisTemplate.execute(new RedisCallback() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				long result = 0;
				for (int i = 0; i < keys.length; i++) {
					result = connection.del(keys[i].getBytes());
				}
				return result;
			}
		});
	}

	/**
	 * 存放key，value
	 *
	 * @param key
	 * @param value
	 * @param liveTime
	 *            存放时间
	 * @return: void 下午3:55:15
	 */
	public void set(final byte[] key, final byte[] value, final long liveTime) {
		redisTemplate.execute(new RedisCallback() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				connection.set(key, value);
				if (liveTime > 0) {
					connection.expire(key, liveTime);
				}
				return 1L;
			}
		});
	}

	/**
	 * @param key
	 * @param value
	 * @param liveTime
	 *            单位秒
	 */
	public void set(String key, String value, long liveTime) {
		this.set(key.getBytes(), value.getBytes(), liveTime);
	}

	/**
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		this.set(key, value, 0L);
	}

	/**
	 * @param key
	 * @param value
	 */
	public void set(byte[] key, byte[] value) {
		this.set(key, value, 0L);
	}

	/**
	 * 获取value
	 *
	 * @param key
	 * @return
	 */
	public Object get(final String key) {
		return redisTemplate.execute(new RedisCallback() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					return new String(connection.get(key.getBytes()), "UTF-8");
				} catch (Exception e) {
					return null;
				}
			}
		});
	}

	/**
	 * @param pattern
	 * @return
	 * @return
	 */
	public Set Setkeys(String pattern) {
		return redisTemplate.keys(pattern);
	}

	/**
	 * 队列里存放(入队,追加队列)
	 *
	 * @param key
	 * @param value
	 * @return: Long 下午2:28:47
	 */
	public Long rpush(String key, String value) {
		return redisTemplate.opsForList().rightPush(key, value);
	}

	/**
	 * 压栈。放入头部
	 *
	 * @param key
	 * @param value
	 * @return: Long 下午2:08:10
	 */
	public Long lpush(String key, String value) {
		return redisTemplate.opsForList().leftPush(key, value);
	}

	/**
	 * 设置过期时间。只对key
	 *
	 * @param key
	 * @param timeout
	 *            时间
	 * @param unit
	 *            单位
	 * @return: Boolean 上午10:04:23
	 */
	public Boolean expire(String key, long timeout, TimeUnit unit) {
		return redisTemplate.expire(key, timeout, unit);
	}

	/**
	 * 对list设置过期时间
	 *
	 * @param key
	 * @param timeout
	 * @param unit
	 * @return: Boolean 下午11:32:28
	 */
	public Boolean expireList(String key, long timeout, TimeUnit unit) {
		return redisTemplate.boundListOps(key).expire(timeout, unit);
	}

	/**
	 * 获取所有key对应的list
	 *
	 * @param key
	 * @return
	 * @return: List 下午3:51:07
	 */
	public List getListByKey(String key) {
		return redisTemplate.opsForList().range(key, 0, -1);
	}

	/**
	 * 获取list长度
	 *
	 * @param key
	 * @return: Long 下午3:11:02
	 */
	public Long getListSizeByKey(final String key) {
		return (Long) redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.lLen(key.getBytes());
			}
		});
	}

	/**
	 * 递增
	 *
	 * @param key
	 * @return: long 下午4:29:54
	 */
	public long incr(final String key) {
		return (long) redisTemplate.execute(new RedisCallback() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.incr(key.getBytes());
			}
		});
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean exists(final String key) {
		return (boolean) redisTemplate.execute(new RedisCallback() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(key.getBytes());
			}
		});
	}

	/**
	 * @return
	 */
	public Object flushDB() {
		return redisTemplate.execute(new RedisCallback() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				connection.flushDb();
				return "ok";
			}
		});
	}

	/**
	 * @return
	 */
	public long dbSize() {
		return (long) redisTemplate.execute(new RedisCallback() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.dbSize();
			}
		});
	}

	/**
	 * @return
	 */
	public Object ping() {
		return redisTemplate.execute(new RedisCallback() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.ping();
			}
		});
	}

	/**
	 * 通过key前缀获取
	 *
	 * @param prefix
	 * @return: Set 上午9:36:52
	 */
	public Set getKeyByPrefix(final String prefix) {
		return (Set) redisTemplate.execute(new RedisCallback() {
			public Set doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.keys(prefix.getBytes());
			}
		});
	}

	private RedisComponents() {
	}

	public StringRedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

}
