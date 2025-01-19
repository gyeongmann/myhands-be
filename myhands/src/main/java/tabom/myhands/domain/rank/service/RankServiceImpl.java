package tabom.myhands.domain.rank.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tabom.myhands.common.infra.redis.RedisService;
import tabom.myhands.domain.rank.dto.RankResponse;
import tabom.myhands.domain.user.entity.User;
import tabom.myhands.domain.user.repository.DepartmentRepository;
import tabom.myhands.domain.user.repository.UserRepository;
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.UserApiException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankServiceImpl implements RankService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RedisService redisService;

    @Override
    @Transactional
    public RankResponse.RankList getRankList(Long userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserApiException(UserErrorCode.USER_ID_NOT_FOUND));
        int userDepartmentId = user.getDepartment().getDepartmentId();
        int myTotalExp = 0;

        List<Object[]> results = departmentRepository.countUsersByDepartment();
        List<RankResponse.CreateRank> rankList = new ArrayList<>();
        int[][] recordUserAndExp = new int[results.size() + 1][results.size() + 1];

        for (Object[] result : results) {
            int departmentId = (Integer) result[0];
            int userCount = ((Long) result[1]).intValue();
            recordUserAndExp[departmentId][0] = userCount;
            String getExp = redisService.getStringValue("department:" + departmentId + ":exp");

            int totalExp = getExp == null ? 0 : Integer.parseInt(getExp);
            int expAvg = userCount == 0 ? 0 : totalExp / userCount;
            recordUserAndExp[departmentId][1] = totalExp;

            if(departmentId == userDepartmentId) {
                myTotalExp = totalExp;
            }

            rankList.add(RankResponse.CreateRank.build(departmentId, expAvg));
        }

        rankList.sort((r1, r2) -> {
            if(r1.getExpAvg() == r2.getExpAvg()) {
                if(r2.getDepartmentId() == userDepartmentId) {
                    return 1;
                }

                return -1;
            }

            return r2.getExpAvg() - r1.getExpAvg();
        });


        int rank = 0;
        int myIndex = -1;
        int preExpAvg = -1;
        for (int i = 0; i < rankList.size(); i++) {
            RankResponse.CreateRank nowRank = rankList.get(i);

            if(nowRank.getExpAvg() != preExpAvg) {
                rank++;
                preExpAvg = nowRank.getExpAvg();
            }
            nowRank.changeRank(rank);

            if (nowRank.getDepartmentId() == userDepartmentId) {
                myIndex = i;
            }
        }

        int needExp = 0;
        if(myIndex > 0) {
            int preDepartmentId = rankList.get(myIndex - 1).getDepartmentId();
            needExp = recordUserAndExp[preDepartmentId][1] * recordUserAndExp[userDepartmentId][0] / recordUserAndExp[preDepartmentId][0] - recordUserAndExp[userDepartmentId][1];
        }

        return RankResponse.RankList.build(myIndex, needExp, rankList);
    }

}
