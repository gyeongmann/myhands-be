package tabom.myhands.domain.user.service;

import tabom.myhands.domain.user.dto.UserRequest;
import tabom.myhands.domain.user.dto.UserResponse;

import java.util.List;

public interface UserService {
    void join(boolean isAdmin,UserRequest.Join request);
    void isDuplicate(boolean isAdmin, String id);
    UserResponse.Login login(UserRequest.Login request);
    void logout(Long userId, boolean isAdmin, String accessToken);
    void editPassword(Long userId, UserRequest.Password requestDto);
    void editImage(Long userId, Integer avartaId);
    UserResponse.Info getInfo(Long userId);
    List<UserResponse.UserList> getList(boolean isAdmin);
    UserResponse.Detail getDetail(boolean isAdmin, Long userId);
}
