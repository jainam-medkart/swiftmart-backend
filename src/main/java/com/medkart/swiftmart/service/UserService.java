package com.medkart.swiftmart.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.medkart.swiftmart.dto.LoginRequest;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.dto.UserDto;
import com.medkart.swiftmart.entity.User;
import com.medkart.swiftmart.enums.UserRole;
import com.medkart.swiftmart.mapper.EntityDtoMapper;
import com.medkart.swiftmart.repository.UserRepo;
import com.medkart.swiftmart.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final EntityDtoMapper entityDtoMapper;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public Response registerUser(UserDto regReq) {
        UserRole role = UserRole.USER;

        if(regReq.getRole() != null && regReq.getRole().equalsIgnoreCase("admin")){
            role = UserRole.ADMIN;
        }

        User user = User.builder()
                .name(regReq.getName())
                .email(regReq.getEmail().toLowerCase())
                .password(passwordEncoder.encode(regReq.getPassword()))
                .phoneNumber(regReq.getPhoneNumber())
                .role(role)
                .build();

        User savedUser = userRepo.save(user);
        String token = jwtUtils.generateToken(savedUser);

        UserDto userDto = entityDtoMapper.mapUserToDtoBasic(savedUser);
        return Response.builder()
                .status(200)
                .message("User Successfully Created")
                .user(userDto)
                .build();
    }

    public Response registerAdmin(UserDto regReq) {
        UserRole role = UserRole.ADMIN;

        User user = User.builder()
                .name(regReq.getName())
                .email(regReq.getEmail().toLowerCase())
                .password(passwordEncoder.encode(regReq.getPassword()))
                .phoneNumber(regReq.getPhoneNumber())
                .role(role)
                .build();

        User savedUser = userRepo.save(user);
        String token = jwtUtils.generateToken(savedUser);

        UserDto userDto = entityDtoMapper.mapUserToDtoBasic(savedUser);
        return Response.builder()
                .status(200)
                .message("User Successfully Created")
                .user(userDto)
                .build();
    }


    public Response loginUser(LoginRequest loginReq) throws InvalidCredentialsException {
        User user = userRepo.findByEmail(loginReq.getEmail())
                .orElseThrow(() -> new NotFoundException("User doesn't exist with email: " + loginReq.getEmail()));

        if(!passwordEncoder.matches(loginReq.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Password doesn't match");
        }

        String token = jwtUtils.generateToken(user);
        UserDto userDto = entityDtoMapper.mapUserToDtoBasic(user);
        return Response.builder()
                .status(200)
                .message("Successfully logged in")
                .expirationTime("24 hours")
                .role(user.getRole().name())
                .token(token)
                .build();
    }


    public Response getAllUsers() {
        List<User> users = userRepo.findAll();
        List<UserDto> userDtos = users.stream().map(user -> entityDtoMapper.mapUserToDtoBasic(user)).toList();

        return Response.builder()
                .status(200)
                .message("Fetched Successfully")
                .userList(userDtos)
                .build();
    }


    public User getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }


    public Response getUserInfoAndOrderHistory() {
        User user = getLoginUser();
        UserDto userDto = entityDtoMapper.mapUserToDtoPlusAddressAndOrderHistory(user);

        return Response.builder()
                .status(200)
                .message("Fetched Successfully")
                .user(userDto)
                .build();
    }

}
