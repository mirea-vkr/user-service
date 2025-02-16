package mirea.vkr.user_service.controller;

import lombok.RequiredArgsConstructor;
import mirea.vkr.user_service.model.dto.request.LoginRequest;
import mirea.vkr.user_service.model.dto.request.LogoutRequest;
import mirea.vkr.user_service.model.dto.request.RegisterRequest;
import mirea.vkr.user_service.model.dto.request.TokenRefreshRequest;
import mirea.vkr.user_service.model.dto.response.LoginResponse;
import mirea.vkr.user_service.model.dto.response.RefreshTokenResponse;
import mirea.vkr.user_service.service.JwtService;
import mirea.vkr.user_service.service.RefreshTokenService;
import mirea.vkr.user_service.service.UserService;
import org.springframework.http.HttpStatus;
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
    private final RefreshTokenService refreshTokenService;

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
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String email = jwtService.extractEmail(refreshToken);

        if (!jwtService.isRefreshTokenValid(email, refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }

        String newAccessToken = jwtService.generateAccessToken(email);
        String newRefreshToken = jwtService.generateRefreshToken(email);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken));
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
        String email = jwtService.extractEmail(request.getRefreshToken());
        refreshTokenService.deleteRefreshToken(email);
        return ResponseEntity.ok("Logged out successfully");
    }
}



