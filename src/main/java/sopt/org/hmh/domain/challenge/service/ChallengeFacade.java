package sopt.org.hmh.domain.challenge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.hmh.domain.app.dto.request.ChallengeAppRequest;
import sopt.org.hmh.domain.app.dto.response.ChallengeAppResponse;
import sopt.org.hmh.domain.app.service.ChallengeAppService;
import sopt.org.hmh.domain.challenge.domain.Challenge;
import sopt.org.hmh.domain.challenge.dto.NewChallengeOrder;
import sopt.org.hmh.domain.challenge.dto.response.ChallengeResponse;
import sopt.org.hmh.domain.challenge.dto.response.DailyChallengeResponse;
import sopt.org.hmh.domain.dailychallenge.service.DailyChallengeService;
import sopt.org.hmh.domain.user.domain.User;
import sopt.org.hmh.domain.user.service.UserService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeFacade {

    private final ChallengeService challengeService;
    private final DailyChallengeService dailyChallengeService;
    private final UserService userService;
    private final ChallengeAppService challengeAppService;

    @Transactional
    public void startNewChallenge(NewChallengeOrder newChallengeOrder) {
        Challenge newChallenge = challengeService.addChallenge(newChallengeOrder.toChallengeEntity());
        userService.changeCurrentChallengeIdByUserId(newChallengeOrder.getUserId(), newChallenge.getId());

        dailyChallengeService.addDailyChallenge(newChallenge);

        this.addAppsByNewChallengeOrder(newChallengeOrder, newChallenge);
    }

    private void addAppsByNewChallengeOrder(NewChallengeOrder newChallengeOrder, Challenge newChallenge) {
        if (newChallengeOrder.isFirstChallenge()) {
            challengeAppService.addApps(newChallengeOrder.toChallengeAppEntities(newChallenge));
            return;
        }
        Long previousChallengeId = userService.getCurrentChallengeIdByUserId(newChallengeOrder.getUserId());
        challengeAppService.addAppsByPreviousChallengeApp(newChallengeOrder.getOs(), previousChallengeId, newChallenge);
        // TODO: userId를 이용해 previousChallengeId를 찾아서 그 ChallengeApp을 가져오는 로직을 join을 이용해 한 번에 처리하도록 변경
        // 그 후, challengeAppService로 메서드 책임 이전
    }

    @Transactional(readOnly = true)
    public ChallengeResponse getCurrentChallengeInfo(Long userId) {
        return ChallengeResponse.of(this.findCurrentChallengeByUserId(userId));
    }

    @Transactional(readOnly = true)
    public DailyChallengeResponse getDailyChallengeInfo(Long userId) {
        Challenge challenge = this.findCurrentChallengeByUserId(userId);

        return DailyChallengeResponse.builder()
                .goalTime(challenge.getGoalTime())
                .apps(challenge.getApps().stream()
                        .map(app -> new ChallengeAppResponse(app.getAppCode(), app.getGoalTime())).toList())
                .build();
    }

    public Challenge findCurrentChallengeByUserId(Long userId) {
        User user = userService.findByIdOrThrowException(userId);
        return challengeService.findByIdOrElseThrow(user.getCurrentChallengeId());
    }

    @Transactional
    public void addAppsToCurrentChallenge(Long userId, List<ChallengeAppRequest> requests, String os) {
        Challenge challenge = this.findCurrentChallengeByUserId(userId);
        challengeAppService.addApps(
                requests.stream()
                        .map(request -> request.toEntity(challenge, os))
                        .toList()
        );
    }

    @Transactional
    public void removeAppFromCurrentChallenge(Long userId, String appCode, String os) {
        Challenge challenge = this.findCurrentChallengeByUserId(userId);
        challengeAppService.removeApp(challenge, appCode, os);
    }
}