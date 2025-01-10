package tabom.myhands.domain.user.service;

import tabom.myhands.domain.user.dto.UserRequest;
import tabom.myhands.domain.user.dto.UserResponse;

public interface UserService {
    void join(UserRequest.Join request);
    void isDuplicate(String id);
    UserResponse.Login login(UserRequest.Login request);
    void logout(Long userId, boolean isAdmin, String accessToken);
    void editPassword(Long userId, UserRequest.Password requestDto);
    void editImage(Long userId, Integer avartaId);
    UserResponse.Info getInfo(Long userId);
}
