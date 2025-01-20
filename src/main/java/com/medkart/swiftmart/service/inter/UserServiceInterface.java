package com.medkart.swiftmart.service.inter;

import com.medkart.swiftmart.dto.LoginRequest;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.dto.UserDto;
import com.medkart.swiftmart.entity.User;

public interface UserServiceInterface {

    public Response registerUser(UserDto user);

    public Response registerAdmin(UserDto user);

    public Response loginUser(LoginRequest loginRequest);

    public Response getAllUsers();

    public User getLoginUser();

    public Response getUserInfoAndOrderHistory();

}
