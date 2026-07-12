package com.wallet.user.service;

import com.wallet.user.dto.AuthResponse;
import com.wallet.user.dto.LoginRequest;
import com.wallet.user.dto.RegisterRequest;

public interface UserService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
