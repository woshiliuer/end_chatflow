package org.example.chatflow.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author by zzr
 */
@Component
public class RedisUtil {


    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 注入默认的 {@link RedisTemplate} 实例。
     *
     * @param redisTemplate RedisTemplate Bean
     */
    @Autowired
    @Qualifier("redisTemplate")
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 将键设置为在指定秒数后过期。
     *
     * @param key  键
     * @param time 过期时间（秒），大于 0 生效
     * @return 是否设置成功
     */
    public Boolean expire(String key, long time) {
        return this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 判断 Redis 中是否存在该键。
     *
     * @param key 键
     * @return true 表示存在
     */
    public Boolean hasKey(String key) {
        return this.redisTemplate.hasKey(key);
    }

    /**
     * 删除一个或多个键。
     *
     * @param key 需要删除的键，可变参数
     */
    public void del(String... key) {
        this.redisTemplate.delete(Arrays.asList(key));
    }

    /**
     * 根据键获取值。
     *
     * @param key 键
     * @param <T> 返回值类型
     * @return 键对应的值
     */
    public <T> T get(String key) {
        return (T) this.redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置字符串键值。
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置字符串键值并指定过期时间。
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        this.redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置字符串键值并指定过期时间。
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     */
    public void set(String key, Object value, Duration timeout) {
        this.redisTemplate.opsForValue().set(key, value, timeout);
    }

    /**
     * 当键不存在时写入值。
     *
     * @param key   键
     * @param value 值
     * @return true 表示写入成功
     */
    public Boolean setIfAbsent(String key, Object value) {
        return this.redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 当键不存在时写入值并指定过期时间。
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return true 表示写入成功
     */
    public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        return this.redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 当键不存在时写入值并指定过期时间。
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @return true 表示写入成功
     */
    public Boolean setIfAbsent(String key, Object value, Duration timeout) {
        return this.redisTemplate.opsForValue().setIfAbsent(key, value, timeout);
    }

    /**
     * 当键已存在时写入值。
     *
     * @param key   键
     * @param value 值
     * @return true 表示写入成功
     */
    public Boolean setIfPresent(String key, Object value) {
        return this.redisTemplate.opsForValue().setIfPresent(key, value);
    }

    /**
     * 当键已存在时写入值并指定过期时间。
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return true 表示写入成功
     */
    public Boolean setIfPresent(String key, Object value, long timeout, TimeUnit unit) {
        return this.redisTemplate.opsForValue().setIfPresent(key, value, timeout, unit);
    }

    /**
     * 当键已存在时写入值并指定过期时间。
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @return true 表示写入成功
     */
    public Boolean setIfPresent(String key, Object value, Duration timeout) {
        return this.redisTemplate.opsForValue().setIfPresent(key, value, timeout);
    }

    /**
     * 批量写入多个键值。
     *
     * @param map 键值对集合
     */
    public void multiSet(Map<? extends String, ? extends Object> map) {
        this.redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 仅当所有键都不存在时批量写入多个键值。
     *
     * @param map 键值对集合
     * @return true 表示全部写入成功
     */
    public Boolean multiSetIfAbsent(Map<? extends String, ? extends Object> map) {
        return this.redisTemplate.opsForValue().multiSetIfAbsent(map);
    }

    /**
     * 根据键获取值。
     *
     * @param key 键
     * @return 键对应的值
     */
    public Object get(Object key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取键对应的值并删除该键。
     *
     * @param key 键
     * @return 键对应的值
     */
    public Object getAndDelete(String key) {
        return this.redisTemplate.opsForValue().getAndDelete(key);
    }

    /**
     * 获取键对应的值并重新设置过期时间。
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 键对应的值
     */
    public Object getAndExpire(String key, long timeout, TimeUnit unit) {
        return this.redisTemplate.opsForValue().getAndExpire(key, timeout, unit);
    }

    /**
     * 获取键对应的值并重新设置过期时间。
     *
     * @param key     键
     * @param timeout 过期时间
     * @return 键对应的值
     */
    public Object getAndExpire(String key, Duration timeout) {
        return this.redisTemplate.opsForValue().getAndExpire(key, timeout);
    }

    /**
     * 获取键对应的值并移除其过期时间。
     *
     * @param key 键
     * @return 键对应的值
     */
    public Object getAndPersist(String key) {
        return this.redisTemplate.opsForValue().getAndPersist(key);
    }

    /**
     * 获取旧值并写入新值。
     *
     * @param key   键
     * @param value 新值
     * @return 旧值
     */
    public Object getAndSet(String key, Object value) {
        return this.redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 批量获取多个键的值。
     *
     * @param keys 键集合
     * @return 值列表
     */
    public List<Object> multiGet(Collection<String> keys) {
        return this.redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 将键的数值自增 1。
     *
     * @param key 键
     * @return 自增后的值
     */
    public Long increment(String key) {
        return this.redisTemplate.opsForValue().increment(key);
    }

    /**
     * 按指定增量增加键的数值。
     *
     * @param key   键
     * @param delta 增量
     * @return 自增后的值
     */
    public Long increment(String key, long delta) {
        return this.redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 按指定浮点增量增加键的数值。
     *
     * @param key   键
     * @param delta 增量
     * @return 自增后的值
     */
    public Double increment(String key, double delta) {
        return this.redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 将键的数值自减 1。
     *
     * @param key 键
     * @return 自减后的值
     */
    public Long decrement(String key) {
        return this.redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 按指定减量减少键的数值。
     *
     * @param key   键
     * @param delta 减量
     * @return 自减后的值
     */
    public Long decrement(String key, long delta) {
        return this.redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 将键设置为在指定时间后过期。
     *
     * @param key  键
     * @param time 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    public boolean expire(String key, long time, TimeUnit unit) {
        try {
            if (time > 0L) {
                this.redisTemplate.expire(key, time, unit);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取键的剩余过期时间（秒）。
     *
     * @param key 键
     * @return 剩余时间，-1 表示永不过期
     */
    public long getExpire(String key) {
        return this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 批量删除键集合。
     *
     * @param keys 键集合
     * @return true 表示执行成功
     */
    public boolean delByCollection(Collection<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            this.redisTemplate.delete(keys);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置哈希（Hash）结构中的字段值。
     *
     * @param key   哈希键
     * @param item  字段
     * @param value 字段值
     * @return 是否写入成功
     */
    public boolean hset(String key, String item, Object value) {
        try {
            this.redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置哈希结构中的字段值并为整个哈希设置过期时间。
     *
     * @param key   哈希键
     * @param item  字段
     * @param value 字段值
     * @param time  过期时间（秒）
     * @return 是否写入成功
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            this.redisTemplate.opsForHash().put(key, item, value);
            if (time > 0L) {
                this.expire(key, time);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取哈希结构中指定字段的值。
     *
     * @param key  哈希键
     * @param item 字段
     * @return 字段值
     */
    public Object hget(String key, String item) {
        return this.redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取哈希结构中所有字段与值。
     *
     * @param key 哈希键
     * @return 字段与值的 Map
     */
    public Map<Object, Object> hmget(String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }


}
