package tabom.myhands.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login{
        private String accessToken;
        private String refreshToken;
        private boolean admin;

        public static UserResponse.Login build(String accessToken, String refreshToken, boolean admin) {
            return Login.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .admin(admin)
                    .build();
        }
    }
}
