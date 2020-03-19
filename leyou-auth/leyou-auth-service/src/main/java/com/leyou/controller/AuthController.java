package com.leyou.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.CookieUtils;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties prop;

    @PostMapping("accredit")
    public ResponseEntity<String> authentication(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String token = this.authService.authentication(username, password);
        if (StringUtils.isBlank(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // 将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
        CookieUtils.setCookie(request, response, prop.getCookieName(),
                token, prop.getCookieMaxAge(), null, true);
        return ResponseEntity.ok(token);
    }

    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue("LY_TOKEN") String token,
            HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest
    ) {
        try {
            UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            if (infoFromToken != null) {
                String newToken = JwtUtils.generateToken(infoFromToken, prop.getPrivateKey(), prop.getExpire());
                CookieUtils.setCookie(httpServletRequest, httpServletResponse, prop.getCookieName(), newToken, prop.getCookieMaxAge());
                return ResponseEntity.ok(infoFromToken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
