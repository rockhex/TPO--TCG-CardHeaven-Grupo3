package com.tcgtrader.service.impl;

import com.tcgtrader.dto.UserRequest;
import com.tcgtrader.dto.UserResponse;
import com.tcgtrader.entity.Cart;
import com.tcgtrader.entity.User;
import com.tcgtrader.exception.BusinessException;
import com.tcgtrader.exception.ResourceNotFoundException;
import com.tcgtrader.repository.UserRepository;
import com.tcgtrader.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered: " + request.email());
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(hashPassword(request.password()))
                .build();

        // Create an empty cart for the new user
        Cart cart = Cart.builder().user(user).build();
        user.setCart(cart);

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    // Placeholder — replace with BCrypt in production
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }
}
