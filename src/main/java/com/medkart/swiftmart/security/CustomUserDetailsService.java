package com.medkart.swiftmart.security;

import com.amazonaws.services.kms.model.NotFoundException;
import com.medkart.swiftmart.entity.User;
import com.medkart.swiftmart.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));

        return AuthUser.builder().user(user).build();
    }
}
