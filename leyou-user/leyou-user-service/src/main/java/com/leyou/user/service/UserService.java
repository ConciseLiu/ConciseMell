package com.leyou.user.service;

import com.leyou.common.utils.CodecUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.pojo.User;
import com.leyou.user.mapper.UserMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    public UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public static final String  VERIFY_CODE_REDIS_PRE   =   "verify:code:phone:";
    public static final Integer VERIFY_CODE_LEN         =   4;
    public static final Integer VERIFY_CODE_EXPIRE_TIME =   5;

    public static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPassword(data);
                break;
            default:
        }
        return userMapper.selectCount(user) != 0;
    }

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    public Boolean sendVerifyCode(String phone) {
        String code = NumberUtils.generateCode(VERIFY_CODE_LEN);

        try {
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            String redisKey = VERIFY_CODE_REDIS_PRE + phone;
            amqpTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code", msg);
            redisTemplate.opsForValue().set(redisKey, code, VERIFY_CODE_EXPIRE_TIME, TimeUnit.MINUTES);
            return true;
        } catch (AmqpException e) {
            LOGGER.error("短信发送失败，手机号:{}, 验证码:{}", phone, code);
            return false;
        }
    }

    public Boolean register(User user, String code) {
        String verifyRedisKey = VERIFY_CODE_REDIS_PRE + user.getPhone();
        // 1. 校验验证码
        String cacheCode = this.redisTemplate.opsForValue().get(verifyRedisKey);
        if (!StringUtils.equals(code, cacheCode)) {
            return false;
        }
        user.setId(null);
        // 2. 加密密码存储
        String salt = CodecUtils.generateSalt();
        String encodePassword = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setPassword(encodePassword);
        user.setSalt(salt);
        user.setCreated(new Date());
        int insertRows = this.userMapper.insertSelective(user);
        if (insertRows == 1) {
            this.redisTemplate.delete(verifyRedisKey);
            return true;
        }
        return false;
    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        if (user == null) {
            return null;
        }
        String dbPassword = user.getPassword();
        String encodePassword = CodecUtils.md5Hex(password, user.getSalt());
        if (!StringUtils.equals(dbPassword, encodePassword)) {
            return null;
        }
        return user;
    }
}
