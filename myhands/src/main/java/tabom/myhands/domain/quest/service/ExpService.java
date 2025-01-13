package tabom.myhands.domain.quest.service;

import tabom.myhands.domain.user.entity.User;

public interface ExpService {

    Integer getCurrentExp(User user);

    Integer getThisYearExp(User user);

    Integer getLastYearExp(User user);

    Integer getYearExp(User user, Integer year);
}
