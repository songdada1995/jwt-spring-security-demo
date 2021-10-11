package org.zerhusen.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerhusen.dao.primary.UserMapper;
import org.zerhusen.model.primary.User;
import org.zerhusen.security.SecurityUtils;

@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        return userMapper.getUserWithAuthorities(SecurityUtils.getCurrentUsername().get());
    }

}
