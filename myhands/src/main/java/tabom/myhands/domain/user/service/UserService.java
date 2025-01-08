package tabom.myhands.domain.user.service;

import tabom.myhands.domain.user.dto.UserRequest;

public interface UserService {
    void join(UserRequest.Join request);
}
