package com.happotech.client.util;

import redis.clients.jedis.SortingParams;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: redis操作类
 * @author: rb
 * @version: 2019年1月2日 下午2:49:34
 */
public interface RedisProvider {

	public boolean set(String key,String value);

	public boolean hset(String key,String field,String value);
	
	public boolean setex(String key, int seconds, String value);
	
	public boolean lpush(String key,String... args);
	
	public String rpop(String key);
	
	public boolean del(String key);
	
	public boolean hdel(String key,String field);

	public String get(String key);
	
	public String hget(String key, String field);

	public Map<String, String> hgetAll(String key);
	
	public  long hlen(String key);
	
	public List<String> lrange(String key,long start,long end);
	
	public long llen(String key);
	
	public boolean ltrim(String key,long start,long end);
	
	public Set<String> hkeys(String key);
	
	public boolean hexists(String key, String field);
	
	public boolean exists(String key);
	
	public long hincrBy(String key, String field, long value);
	
	public long expire(String key, int seconds);
	
	public long pexpireAt(String key, long unixTime);
	
	public long incr(String key);

	public boolean lsort(String code, SortingParams sortingParams);

    public boolean lrem(String key, int index, String value);

    public boolean sadd(String key, String... field);

    public boolean sismember(String key, String field);

    public boolean srem(String key, String field);

    public long scard(String key);

	public String spop(String key);

	public Set<String> keys(String key);
}
