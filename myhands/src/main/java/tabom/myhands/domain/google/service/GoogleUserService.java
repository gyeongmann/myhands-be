package tabom.myhands.domain.google.service;

import tabom.myhands.domain.user.dto.UserRequest;

public interface GoogleUserService {
    void createUser(UserRequest.GoogleJoin request);
}
