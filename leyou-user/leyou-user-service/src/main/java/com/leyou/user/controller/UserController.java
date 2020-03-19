package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data, @PathVariable("type") Integer type) {
        Boolean checkStatus = userService.checkData(data, type);
        if (checkStatus == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(checkStatus);
    }

    @PostMapping("code")
    public ResponseEntity<Boolean> sendVerifyCode(String phone) {
        Boolean sendStatus = userService.sendVerifyCode(phone);
        if (sendStatus == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(sendStatus);
    }

    @PostMapping("register")
    public ResponseEntity<Boolean> register(@Valid User user, @RequestParam("code")String code) {
        Boolean regStatus = this.userService.register(user, code);
        if (regStatus == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(regStatus);
    }

    @GetMapping("query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        User user = this.userService.queryUser(username, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }
}
