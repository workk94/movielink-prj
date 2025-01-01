package com.acorn.movielink.comunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/community/auth")
public class ComunityAuthController {
    @Autowired
    private AuthenticationUtil authenticationUtil;

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth() {
        Map<String, Object> response = authenticationUtil.checkAuthentication();
        return ResponseEntity.ok(response);
    }
}
