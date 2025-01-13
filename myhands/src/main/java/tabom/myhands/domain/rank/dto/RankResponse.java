package tabom.myhands.domain.rank.dto;

import lombok.*;

import java.util.List;

public class RankResponse {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateRank {
        private int departmentId;
        private int expAvg;
        private int rank;

        public static RankResponse.CreateRank build(int departmentId, int expAvg){
            return CreateRank.builder()
                    .departmentId(departmentId)
                    .expAvg(expAvg)
                    .build();
        }

        public void changeRank(int rank) {
            this.rank = rank;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RankList {
        private int myIndex;
        private int needExp;
        private List<RankResponse.CreateRank> rankList;

        public static RankResponse.RankList build(int myIndex, int needExp, List<RankResponse.CreateRank> rankList){
            return RankList.builder()
                    .myIndex(myIndex)
                    .needExp(needExp)
                    .rankList(rankList)
                    .build();
        }
    }

}
