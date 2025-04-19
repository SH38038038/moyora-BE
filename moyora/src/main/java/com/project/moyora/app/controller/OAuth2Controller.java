package com.project.moyora.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login/oauth2")
public class OAuth2Controller {

    @GetMapping("/success")
    public ResponseEntity<?> loginSuccess(@RequestParam String token) {
        return ResponseEntity.ok("Login Success! Token: " + token);
    }
}
