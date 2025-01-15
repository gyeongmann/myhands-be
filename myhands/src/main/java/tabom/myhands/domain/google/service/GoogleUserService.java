package tabom.myhands.domain.google.service;

import tabom.myhands.domain.user.dto.UserRequest;
import tabom.myhands.domain.user.entity.User;

public interface GoogleUserService {
    void createUser(UserRequest.GoogleJoin request);
    void createUserToSheet(User user);
}
