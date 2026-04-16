package com.tcgtrader.service.impl;

import com.tcgtrader.dto.AuthResponse;
import com.tcgtrader.dto.LoginRequest;
import com.tcgtrader.dto.UserRequest;
import com.tcgtrader.entity.Cart;
import com.tcgtrader.entity.Role;
import com.tcgtrader.entity.User;
import com.tcgtrader.exception.BusinessException;
import com.tcgtrader.exception.ResourceNotFoundException;
import com.tcgtrader.repository.RoleRepository;
import com.tcgtrader.repository.UserRepository;
import com.tcgtrader.security.JwtService;
import com.tcgtrader.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final UUID DEFAULT_CUSTOMER_ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        user.setLastLogin(Instant.now());
        userRepository.save(user);

        return toAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse register(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered: " + request.email());
        }

        UUID roleId = request.roleId() != null ? request.roleId() : DEFAULT_CUSTOMER_ROLE_ID;
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleId));

        User user = User.builder()
                .role(role)
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        Cart cart = Cart.builder().user(user).build();
        user.setCart(cart);

        return toAuthResponse(userRepository.save(user));
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(
                jwtService.generateToken(user),
                user.getId(),
                user.getRole().getName(),
                user.getEmail()
        );
    }
}
