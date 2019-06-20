package com.fun.crawl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    /**
     * 当前redis数据库的前缀
     */
    private static String preStr = "know-";


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 批量删除对应的value
     *
     * @param keys String[]
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 模糊匹配查询
     *
     * @param pattern()
     * @return Set<String>（keys集合）
     * 该命令所支持的匹配模式如下：
     * （1）?：用于匹配单个字符。例如，h?llo可以匹配hello、hallo和hxllo等；
     * （2）*：用于匹配零个或者多个字符。例如，h*llo可以匹配hllo和heeeello等；
     * （3）[]：可以用来指定模式的选择区间。例如h[ae]llo可以匹配hello和hallo，但是不能匹配hillo。
     * 同时，可以使用“/”符号来转义特殊的字符
     */
    public Set<String> getKeysByPattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(preStr + pattern);
        if (keys.size() > 0) {
            return keys;
        } else {
            return null;
        }
    }

    /**
     * 模糊匹配批量删除
     * 该命令所支持的匹配模式如下：
     * （1）?：用于匹配单个字符。例如，h?llo可以匹配hello、hallo和hxllo等；
     * （2）*：用于匹配零个或者多个字符。例如，h*llo可以匹配hllo和heeeello等；
     * （3）[]：可以用来指定模式的选择区间。例如h[ae]llo可以匹配hello和hallo，但是不能匹配hillo。
     * 同时，可以使用“/”符号来转义特殊的字符
     *
     * @param pattern()
     */
    public boolean removePattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(preStr + pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
            return true;
        }
        return false;
    }

    public boolean removeAllPattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
            return true;
        }
        return false;
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public boolean remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(preStr + key);
            return true;
        }
        return false;
    }

    /**
     * 清除飞卡系统中规则为（基础数据）的所有对象缓存
     */
    public boolean refreshAll(String par) {
        try {
            return this.removePattern(par);
        } catch (Exception e) {
            log.error("refreshAll异常：", e);
        }
        return false;
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(preStr + key);
    }

    /**
     * 读取（不加前缀）
     *
     * @param key
     * @return
     */
    public Object getNoPre(final String key) {
        Object result = null;
        try {
            ValueOperations<String, Object> operations = redisTemplate
                    .opsForValue();
            result = operations.get(key);
        } catch (Exception e) {
            log.error("------RedisUtils-----getNoPre----key:" + key + ",异常");
        }
        return result;
    }

    /**
     * 写入单个对象（不加前缀）
     *
     * @param key
     * @param value
     * @param expireTime 失效时间，单位秒,null默认30分钟失效（-1表示永久有效）
     * @return
     */
    public boolean setNoPre(String key, Object value, Integer expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            expire(key, expireTime);
            result = true;
        } catch (Exception e) {
            log.error("------RedisUtils-----setNoPre----key:" + key + ",异常：", e);
        }
        return result;
    }


    /**
     * 删除（不加前缀）
     *
     * @param key
     */
    public boolean delNoPre(final String key) {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            log.error("------RedisUtils-----delNoPre----key:" + key + ",异常：", e);
        }
        return false;
    }


    /**
     * 读取
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        try {
            ValueOperations<String, Object> operations = redisTemplate
                    .opsForValue();
            result = operations.get(preStr + key);
        } catch (Exception e) {
            log.error("------RedisUtils-----get,异常");
        }
        return result;
    }

    /**
     * 写入单个对象
     *
     * @param key
     * @param value
     * @param expireTime 失效时间，单位秒,null默认30分钟失效（-1表示永久有效）
     * @return
     */
    public boolean set(String key, Object value, Integer expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(preStr + key, value);
            expire(key, expireTime);
            result = true;
        } catch (Exception e) {
            log.error("Redis操作异常：", e);
        }
        return result;
    }

    /**
     * 保存List集合
     *
     * @param key
     * @param value   List集合
     * @param timeout 失效时间，单位秒,null默认30分钟失效（-1表示永久有效）
     */
    public boolean setList(String key, List<?> value, Integer timeout) {
        boolean result = false;
        try {
            // ListOperations可以理解为List<Object>
            ListOperations listOperations = redisTemplate.opsForList();
            listOperations.leftPush(preStr + key, value);
            // .leftPushAll(value);
            expire(key, timeout);
            result = true;
        } catch (Exception e) {
            log.error("Redis操作异常：", e);
        }
        return result;
    }

    /**
     * 获取List<Object>集合对象
     *
     * @param key
     * @return
     */
    public Object getList(String key) {
        // ListOperations可以理解为List<Object>
        return redisTemplate.opsForList().leftPop(preStr + key);
    }

    /**
     * 新增Set集合对象
     *
     * @param key
     * @param value   Set集合
     * @param timeout 失效时间，单位秒,null默认30分钟失效（-1表示永久有效）
     */
    public boolean setSet(String key, Set<?> value, Integer timeout) {
        boolean result = false;
        try {
            SetOperations setOperations = redisTemplate.opsForSet();
            setOperations.add(preStr + key, value);
            expire(key, timeout);
        } catch (Exception e) {
            log.error("Redis操作异常：", e);
        }
        return result;
    }


    /**
     * 获取Set<Object>集合对象
     *
     * @param key
     * @return
     */
    public Object getSet(String key) {
        return redisTemplate.opsForSet().members(preStr + key);
    }

    /**
     * 新增Map集合对象
     *
     * @param key
     * @param value   Map集合
     * @param timeout 失效时间，单位秒,null默认30分钟失效（-1表示永久有效）
     */
    public boolean setHash(String key, Map<String, ?> value, Integer timeout) {
        boolean result = false;
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(preStr + key, value);
            expire(key, timeout);
            result = true;
        } catch (Exception e) {
            log.error("Redis操作异常：", e);
        }
        return result;
    }

    /**
     * 获取Map<Object,Object>集合对象
     *
     * @param key
     * @return
     */
    public Object getHash(String key) {
        return redisTemplate.opsForHash().entries(preStr + key);
    }

    /**
     * 哈希表key中的域field的值设为value
     * 例如：
     * hset("key1", "field1", "field1-value");
     * hset("key1", "field2", "field2-value");
     *
     * @param key     哈希表key
     * @param hashKey 域
     * @param value   值
     * @param timeout 失效时间，单位秒,null默认30分钟失效（-1表示永久有效）
     */
    public boolean hset(String key, String hashKey, Object value, Integer timeout) {
        boolean result = false;
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.put(preStr + key, (preStr + hashKey), value);
            expire(key, timeout);
            result = true;
        } catch (Exception e) {
            log.error("Redis操作异常：", e);
        }
        return result;
    }

    /**
     * 获取哈希表指定域的值
     *
     * @param key     哈希表key
     * @param hashKey 域
     * @return
     */
    public Object hget(String key, String hashKey) {
        return redisTemplate.opsForHash().get(preStr + key, preStr + hashKey);
    }

    /**
     * 删除指定哈希表指定域的值
     *
     * @param key     哈希表key
     * @param hashKey 域
     * @return
     */
    public void hdel(String key, String hashKey) {
        redisTemplate.opsForHash().delete(preStr + key, preStr + hashKey);
    }

    /**
     * 设置失效时间
     *
     * @param key
     * @param timeout 失效时间，秒为单位，null默认30分钟失效（-1表示永久有效）
     */
    public void expire(String key, Integer timeout) {
        if (timeout == null) {
            timeout = 60 * 30;// 30分钟
        }
        if (timeout == -1) {
            //（-1表示永久有效）不设置失效时间
        } else {
            redisTemplate.expire(preStr + key, timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * 读取
     * stringRedisTemplate 操作
     */
    public String getByStringRedis(String key) {
        String rt = null;
        try {
            ValueOperations<String, String> valueops = stringRedisTemplate
                    .opsForValue();
            rt = valueops.get(key);
        } catch (Exception E) {
            E.printStackTrace();
        }
        return rt;
    }

    /**
     * 通过stringRedisTemplate 操作 存储文件
     *
     * @param key
     * @param object
     * @param timeout 失效时间，单位秒,
     */
    public void saveByStringRedis(String key, String object, int timeout) {
        if (key == null) return;
        try {
            ValueOperations operations = stringRedisTemplate
                    .opsForValue();
            operations.set(key, object, timeout, TimeUnit.SECONDS);
            stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 删除（不加前缀）
     *
     * @param key
     */
    public boolean delNoPreString(final String key) {
        try {
            stringRedisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            log.error("------RedisUtils-----delNoPre----key:" + key + ",异常：", e);
        }
        return false;
    }



}
