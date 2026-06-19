package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.User;
import com.homestay.linen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Result<User> login(String username, String password) {
        try {
            User user = userRepository.findByUsernameAndPassword(username, password).orElse(null);
            if (user == null) {
                return Result.error("用户名或密码错误");
            }
            if (!user.getEnabled()) {
                return Result.error("账号已被禁用");
            }
            return Result.success("登录成功", user);
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    public Result<List<User>> list() {
        try {
            List<User> users = userRepository.findAll();
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("查询用户列表失败: " + e.getMessage());
        }
    }

    public Result<List<User>> listByRole(String role) {
        try {
            List<User> users = userRepository.findByRole(role);
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("按角色查询用户失败: " + e.getMessage());
        }
    }

    public Result<User> getById(Long id) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("查询用户失败: " + e.getMessage());
        }
    }
}
