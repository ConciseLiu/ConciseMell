package com.leyou.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.client.UserClient;
import com.leyou.user.pojo.User;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    public String authentication(String username, String password) {
        User user = userClient.queryUser(username, password);
        if (user == null) {
            return null;
        }
        try {
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
            String token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
