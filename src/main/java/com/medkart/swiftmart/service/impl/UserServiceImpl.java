package com.medkart.swiftmart.service.impl;

import com.medkart.swiftmart.dto.LoginRequest;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.dto.UserDto;
import com.medkart.swiftmart.entity.User;
import com.medkart.swiftmart.service.inter.UserServiceInterface;

public class UserServiceImpl implements UserServiceInterface {
    @Override
    public Response registerUser(UserDto user) {
        return null;
    }

    @Override
    public Response registerAdmin(UserDto user) {
        return null;
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public Response getAllUsers() {
        return null;
    }

    @Override
    public User getLoginUser() {
        return null;
    }

    @Override
    public Response getUserInfoAndOrderHistory() {
        return null;
    }
}
