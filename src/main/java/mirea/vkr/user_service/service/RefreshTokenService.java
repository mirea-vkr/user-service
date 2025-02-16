package mirea.vkr.user_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenService {
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private final RedisTemplate<String, String> redisTemplate;
    private final long refreshTokenExpiration;

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate,
                               @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.redisTemplate = redisTemplate;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public void saveRefreshToken(String email, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(refreshTokenExpiration));
    }

    public boolean isRefreshTokenValid(String email, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + email;
        String storedToken = redisTemplate.opsForValue().get(key);
        return storedToken.equals(refreshToken);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);
    }
}

