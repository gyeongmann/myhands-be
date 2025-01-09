package tabom.myhands.domain.user.service;

import tabom.myhands.domain.user.dto.UserRequest;
import tabom.myhands.domain.user.dto.UserResponse;

public interface UserService {
    void join(UserRequest.Join request);
    void isDuplicate(String id);
    UserResponse.Login login(UserRequest.Login request);
}
