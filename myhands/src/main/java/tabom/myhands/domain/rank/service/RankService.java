package tabom.myhands.domain.rank.service;

import tabom.myhands.domain.rank.dto.RankResponse;

public interface RankService {
    RankResponse.RankList getRankList(Long userId);
}
