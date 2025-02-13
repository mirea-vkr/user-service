package mirea.vkr.user_service.controller;

import lombok.RequiredArgsConstructor;
import mirea.vkr.user_service.model.dto.request.LoginRequest;
import mirea.vkr.user_service.model.dto.request.RegisterRequest;
import mirea.vkr.user_service.model.dto.request.TokenRefreshRequest;
import mirea.vkr.user_service.model.dto.response.LoginResponse;
import mirea.vkr.user_service.model.dto.response.RefreshTokenResponse;
import mirea.vkr.user_service.service.JwtService;
import mirea.vkr.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        userService.registerUser(request.getEmail(), request.getPassword(), request.getUsername());
        return ResponseEntity.ok(Map.of("message", "Пользователь зарегистрирован"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String accessToken = jwtService.generateAccessToken(request.getEmail());
        String refreshToken = jwtService.generateRefreshToken(request.getEmail());

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));

    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String email = jwtService.extractEmail(refreshToken);

        if (!jwtService.isTokenValid(refreshToken, email)) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh-токен недействителен"));
        }

        String newAccessToken = jwtService.generateAccessToken(email);
        return ResponseEntity.ok().body(new RefreshTokenResponse(newAccessToken));
    }
}



