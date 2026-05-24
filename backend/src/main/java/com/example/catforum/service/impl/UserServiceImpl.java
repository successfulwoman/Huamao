package com.example.catforum.service.impl;

import com.example.catforum.entity.User;
import com.example.catforum.mapper.UserMapper;
import com.example.catforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (user != null && user.getPassword().equals(password) && user.getStatus() == 1) {
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

    @Override
    public User getInfo(Integer id) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public User adminLogin(String username, String password) {
        // 角色为1表示管理员
        User user = userMapper.findByUsernameAndRole(username, 1);
        if (user != null && user.getPassword().equals(password) && user.getStatus() == 1) {
            user.setPassword(null);
            return user;
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userMapper.findAll();
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    public int updateUserStatus(Integer id, Integer status) {
        return userMapper.updateStatus(id, status);
    }

    @Override
    public int updateUserRole(Integer id, Integer role) {
        return userMapper.updateRole(id, role);
    }

    @Override
    public int deleteUser(Integer id) {
        return userMapper.deleteById(id);
    }

    @Override
    public int countUsers() {
        return userMapper.count();
    }
}