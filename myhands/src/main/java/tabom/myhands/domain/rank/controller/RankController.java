package tabom.myhands.domain.rank.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.DtoResponse;
import tabom.myhands.domain.rank.dto.RankResponse;
import tabom.myhands.domain.rank.service.RankService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exp")
public class RankController {
    private final ResponseProperties responseProperties;
    private final RankService rankService;

    @GetMapping("/rank")
    public ResponseEntity<DtoResponse<RankResponse.RankList>> list(HttpServletRequest request) {
        RankResponse.RankList response = rankService.getRankList((Long) request.getAttribute("userId"));

        if(response == null) {
            ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getFail(),null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getSuccess(),response));
    }

}
