package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.User;
import com.homestay.linen.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        return userService.login(username, password);
    }

    @GetMapping("/list")
    public Result<List<User>> list() {
        return userService.list();
    }

    @GetMapping("/listByRole")
    public Result<List<User>> listByRole(@RequestParam String role) {
        return userService.listByRole(role);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return userService.getById(id);
    }
}
