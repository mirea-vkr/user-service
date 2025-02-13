package mirea.vkr.user_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshRequest {
    @NotBlank(message = "Refresh-токен обязателен")
    private String refreshToken;
}

