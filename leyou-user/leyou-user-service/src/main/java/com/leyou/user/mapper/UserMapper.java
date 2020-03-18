package com.leyou.user.mapper;

import com.leyou.pojo.user.User;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@org.apache.ibatis.annotations.Mapper
@Component
public interface UserMapper extends Mapper<User> {

}
