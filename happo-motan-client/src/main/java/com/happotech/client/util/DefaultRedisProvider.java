package com.happotech.client.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: redis操作实现类
 * @author: rb
 * @version: 2019年1月2日 下午2:50:48
 */
@Component
public class DefaultRedisProvider implements RedisProvider, InitializingBean {
	private static final Logger logger = LogManager.getLogger(DefaultRedisProvider.class);

	@Value("${redis.host}")
	private String host;

	@Value("${redis.port}")
	private int port;

	@Value("${redis.password}")
	private String password;

	@Value("${redis.database}")
	private int database;

	@Value("${redis.timeout}")
	private int timeout;

	@Value("${redis.maxIdle}")
	private int maxIdle;

	@Value("${redis.maxTotal}")
	private int maxTotal;
	@Value("${redis.index}")
	private int index;

	private static  JedisPool pool = null;


	@Override
	public void afterPropertiesSet() throws Exception {

		JedisPoolConfig config= new JedisPoolConfig();
		//最大空闲连接数, 应用自己评估，不要超过ApsaraDB for Redis每个实例最大的连接数
	    config.setMaxIdle(maxIdle);
	    //最大连接数, 应用自己评估，不要超过ApsaraDB for Redis每个实例最大的连接数
	    config.setMaxTotal(maxTotal);
	    config.setTestOnBorrow(false);
	    config.setTestOnReturn(false);

	    JedisPool mypool;

		if(null!=password && !"".equals(password.trim()))
			mypool=new JedisPool(config, host, port, timeout, password,database);
		else
			mypool=new JedisPool(config, host, port, timeout,null,database);
	    
	    DefaultRedisProvider.setPool(mypool);
		
	}
	
	
	private Jedis getResource(){
		Jedis jedis = pool.getResource();
		jedis.select(index);
		return jedis;
	}
	
	

	public boolean set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.set(key, value);
		} catch (Exception ex) {
			logger.error("redis的set操作异常", ex);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return true;
	}

	
	public boolean hset(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.hset(key, field, value);
		} catch (Exception ex) {
			logger.error("redis的hset操作异常", ex);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return true;
	}
	
	/**
	 * @Description: 设置key过期时间
	 * @author: rb
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 * @version: 2018年8月20日 下午9:58:49
	 */
	public boolean setex(String key, int seconds, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.setex(key, seconds, value);
		} catch (Exception ex) {
			logger.error("redis的setex操作异常", ex);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return true;
	}


	public boolean lpush(String key, String... args) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.lpush(key, args);
		
		} catch (Exception ex) {
			logger.error("redis的lpush操作异常", ex);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return true;
	}


	public String rpop(String key) {
		 String value=null;
			Jedis jedis = null;
			try {
				jedis = getResource();
				value=jedis.rpop(key);
			} catch (Exception ex) {
				logger.error("redis的rpop操作异常", ex);
				return null;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		 return value;
	}


	
	

	public boolean del(String key) {
		
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.del(key);
		} catch (Exception ex) {
			logger.error("redis的del操作异常", ex);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return true;
	}

	
	public boolean hdel(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.hdel(key, field);
		} catch (Exception ex) {
			logger.error("redis的hdel操作异常", ex);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return true;
	}

	
	public String get(String key) {
	    String value=null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			value=jedis.get(key);
		} catch (Exception ex) {
			logger.error("redis的get操作异常", ex);
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return value;
	}

	
	public String hget(String key, String field) {
	
		 String value=null;
			Jedis jedis = null;
			try {
				jedis = getResource();
				value=jedis.hget(key, field);
			} catch (Exception ex) {
				logger.error("redis的hget操作异常", ex);
				return null;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		 return value;
	}
	
	public  Map<String, String> hgetAll(String key) {
		
		 Map<String, String> value=null;
			Jedis jedis = null;
			try {
				jedis = getResource();
				value=jedis.hgetAll(key);
			} catch (Exception ex) {
				logger.error("redis的hgetAll操作异常", ex);
				return null;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		 return value;
	}

	public  long hlen(String key) {
		long hlen=0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			hlen=jedis.hlen(key);
		} catch (Exception ex) {
			logger.error("redis的hlen操作异常", ex);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return hlen;
	}

	public  List<String> lrange(String key,long start,long end) {
		
		List<String>  value=null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			value=jedis.lrange(key, start, end);
		} catch (Exception ex) {
			logger.error("redis的lrange操作异常", ex);
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	   return value;
	}



	public long llen(String key) {
		long llen=0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			llen=jedis.llen(key);
		} catch (Exception ex) {
			logger.error("redis的llen操作异常", ex);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return llen;
	}
	

	
	
	public boolean ltrim(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.ltrim(key, start, end);
		} catch (Exception ex) {
			logger.error("redis的ltrim操作异常", ex);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return true;
	}

	
	@Override
	public Set<String> hkeys(String key) {
		Set<String>  value=null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			value=jedis.hkeys(key);
		} catch (Exception ex) {
			logger.error("redis的hkeys操作异常", ex);
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	   return value;
	}
	
	@Override
	public boolean hexists(String key, String field) {
		boolean flag=false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			flag=jedis.hexists(key, field);
		} catch (Exception ex) {
			logger.error("redis的hexists操作异常", ex);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return flag;
	}
	
	@Override
	public boolean exists(String key) {
		boolean flag=false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			flag=jedis.exists(key);
		} catch (Exception ex) {
			logger.error("redis的hexists操作异常", ex);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return flag;
	}
	
	@Override
	public long hincrBy(String key, String field, long value) {
		long fsize=0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			fsize=jedis.hincrBy(key, field, value);
		} catch (Exception ex) {
			logger.error("redis的hset操作异常", ex);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return fsize;
	}
	
	/**
	 * @Description: 设置 seconds 秒后，key 过期
	 * @author: rb
	 * @param key
	 * @param seconds
	 * @return
	 * @version: 2018年10月18日 下午4:51:30
	 */
	public long expire(String key, int seconds) {
		long fsize=0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			fsize = jedis.expire(key, seconds);
		} catch (Exception ex) {
			logger.error("redis的expire操作异常", ex);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return fsize;
	}
	
	/**
	 * @Description: 设置 key 在某个 时间戳过期  单位是毫秒
	 * @author: rb
	 * @param key
	 * @param seconds
	 * @return
	 * @version: 2018年10月18日 下午4:52:48
	 */
	public long pexpireAt(String key, long unixTime) {
		long fsize=0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			fsize = jedis.pexpireAt(key, unixTime);
		} catch (Exception ex) {
			logger.error("redis的expireAt操作异常", ex);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return fsize;
	}
	
	@Override
	public long incr(String key) {
		long fsize=0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			fsize=jedis.incr(key);
		} catch (Exception ex) {
			logger.error("redis的incr操作异常", ex);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return fsize;
	}

	@Override
	public boolean lsort(String code, SortingParams sortingParams) {
		boolean flag=false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.sort(code, sortingParams);
			flag = true;
		}catch (Exception e){
			logger.error("redis的lsort操作异常", e);
		}finally {
			if (jedis != null){
				jedis.close();
			}
		}
		return flag;
	}

	@Override
	public boolean lrem(String key, int index, String value) {
		boolean flag=false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.lrem(key, index, value);
			flag = true;
		}catch (Exception e){
			logger.error("redis的lsort操作异常", e);
		}finally {
			if (jedis != null){
				jedis.close();
			}
		}
		return flag;
	}

	@Override
	public boolean sadd(String key, String... field) {
		boolean flag=false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.sadd(key, field);
			flag = true;
		}catch (Exception e){
			logger.error("redis的lsort操作异常", e);
		}finally {
			if (jedis != null){
				jedis.close();
			}
		}
		return flag;
	}

	@Override
	public boolean sismember(String key, String field) {
		boolean flag=false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.sismember(key, field);
		}catch (Exception e){
			logger.error("redis的lsort操作异常", e);
		}finally {
			if (jedis != null){
				jedis.close();
			}
		}
		return flag;
	}

	@Override
	public boolean srem(String key, String field) {
		boolean flag=false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.srem(key, field);
			return true;
		}catch (Exception e){
			logger.error("redis的lsort操作异常", e);
		}finally {
			if (jedis != null){
				jedis.close();
			}
		}
		return flag;
	}

    @Override
    public long scard(String key) {
		long llen=0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			llen=jedis.scard(key);
		} catch (Exception ex) {
			logger.error("redis的scard操作异常", ex);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return llen;
    }

	@Override
	public String spop(String key) {
		String value=null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			value=jedis.spop(key);
		} catch (Exception ex) {
			logger.error("redis的spop操作异常", ex);
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return value;
	}

	@Override
	public Set<String> keys(String key) {
		Set<String> value=null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			value=jedis.keys(key);
		} catch (Exception ex) {
			logger.error("redis的keys操作异常", ex);
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return value;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public static JedisPool getPool() {
		return pool;
	}

	public static void setPool(JedisPool pool) {
		DefaultRedisProvider.pool = pool;
	}



}
