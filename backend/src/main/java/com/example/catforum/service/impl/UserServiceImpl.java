package com.example.catforum.service.impl;

import com.example.catforum.entity.User;
import com.example.catforum.mapper.UserMapper;
import com.example.catforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            user.setPassword(null);
            return user;
        }
        return null;
    }

    @Override
    public int updateAvatar(Integer id, String avatar) {
        return userMapper.updateAvatar(id, avatar);
    }

    @Override
    public int register(User user) {
        User existing = userMapper.findByUsername(user.getUsername());
        if (existing != null) {
            return -1;
        }
        return userMapper.insert(user);
    }
}