package mirea.vkr.user_service.model.dto.response;

import lombok.AllArgsConstructor;



@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
}
