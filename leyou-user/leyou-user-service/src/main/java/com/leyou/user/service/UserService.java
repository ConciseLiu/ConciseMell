package com.leyou.user.service;

import com.leyou.pojo.user.User;
import com.leyou.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    public UserMapper userMapper;

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
}
