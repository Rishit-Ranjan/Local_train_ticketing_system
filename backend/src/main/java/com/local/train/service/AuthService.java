// AuthService.java
package com.local.train.service;

import com.local.train.dto.*;
import com.local.train.entity.User;
import com.local.train.entity.UserRole;
import com.local.train.entity.Wallet;
import com.local.train.exception.ResourceNotFoundException;
import com.local.train.repository.UserRepository;
import com.local.train.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(UserRole.USER)
                .build();
        
        // Create wallet for user
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        user.setWallet(wallet);
        
        userRepository.save(user);
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        var jwtToken = jwtService.generateToken(userDetails);
        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
        var jwtToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        return TokenResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        final String username = jwtService.extractUsername(request.getToken());
        if (username != null) {
            var user = userRepository.findByEmail(username)
                    .orElseThrow();
            
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build();
            if (jwtService.isTokenValid(request.getToken(), userDetails)) {
                var jwtToken = jwtService.generateToken(userDetails);
                return AuthResponse.builder()
                        .token(jwtToken)
                        .userId(user.getId())
                        .build();
            }
        }
        throw new RuntimeException("Invalid refresh token");
    }
}