package sopt.org.hmh.domain.challenge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.hmh.domain.app.domain.ChallengeApp;
import sopt.org.hmh.domain.app.dto.request.ChallengeAppRequest;
import sopt.org.hmh.domain.app.dto.response.ChallengeAppResponse;
import sopt.org.hmh.domain.app.service.ChallengeAppService;
import sopt.org.hmh.domain.challenge.domain.Challenge;
import sopt.org.hmh.domain.challenge.domain.exception.ChallengeError;
import sopt.org.hmh.domain.challenge.domain.exception.ChallengeException;
import sopt.org.hmh.domain.challenge.dto.request.ChallengeRequest;
import sopt.org.hmh.domain.challenge.dto.response.ChallengeResponse;
import sopt.org.hmh.domain.challenge.dto.response.DailyChallengeResponse;
import sopt.org.hmh.domain.challenge.repository.ChallengeRepository;
import sopt.org.hmh.domain.dailychallenge.domain.DailyChallenge;
import sopt.org.hmh.domain.dailychallenge.service.DailyChallengeService;
import sopt.org.hmh.domain.user.domain.User;
import sopt.org.hmh.domain.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final DailyChallengeService dailyChallengeService;
    private final UserService userService;
    private final ChallengeAppService challengeAppService;

    @Transactional
    public Challenge addChallenge(Long userId, ChallengeRequest challengeRequest, String os) {
        Challenge challenge = challengeRepository.save(challengeRequest.toEntity(userId));
        LocalDate startDate = challenge.getCreatedAt().toLocalDate();
        User user = userService.findByIdOrThrowException(userId);
        this.addAppsIfPreviousChallengeExist(os, user.getCurrentChallengeId(), challenge);
        user.changeCurrentChallengeId(challenge.getId());
        dailyChallengeService.addDailyChallenge(userId, startDate, challenge);

        return challenge;
    }

    private void addAppsIfPreviousChallengeExist(String os, Long previousChallengeId, Challenge challenge) {
        if (previousChallengeId != null) {
            Challenge previousChallenge = this.findByIdOrElseThrow(previousChallengeId);
            List<ChallengeAppRequest> previousApps = previousChallenge.getApps().stream()
                    .map(app -> new ChallengeAppRequest(app.getAppCode(), app.getGoalTime()))
                    .toList();
            challengeAppService.addApps(challenge, previousApps, os);
        }
    }

    public ChallengeResponse getChallenge(Long userId) {
        Challenge challenge = findCurrentChallengeByUserId(userId);
        Integer todayIndex = calculateTodayIndex(challenge.getCreatedAt(), challenge.getPeriod());

        return ChallengeResponse.builder()
                .period(challenge.getPeriod())
                .statuses(challenge.getHistoryDailyChallenges()
                        .stream()
                        .map(DailyChallenge::getStatus)
                        .toList())
                .todayIndex(todayIndex)
                .startDate(challenge.getCreatedAt().toLocalDate().toString())
                .goalTime(challenge.getGoalTime())
                .apps(challenge.getApps().stream()
                        .map(app -> new ChallengeAppResponse(app.getAppCode(), app.getGoalTime())).toList())
                .build();
    }

    public DailyChallengeResponse getDailyChallenge(Long userId) {
        Challenge challenge = findCurrentChallengeByUserId(userId);

        return DailyChallengeResponse.builder()
                .goalTime(challenge.getGoalTime())
                .apps(challenge.getApps().stream()
                        .map(app -> new ChallengeAppResponse(app.getAppCode(), app.getGoalTime())).toList())
                .build();
    }

    @Transactional
    public void deleteChallengeRelatedByUserId(List<Long> expiredUserIdList) {
        challengeRepository.deleteByUserIdIn(expiredUserIdList);
    }

    private Integer calculateTodayIndex(LocalDateTime challengeCreateAt, int period) {
        int daysBetween = (int) ChronoUnit.DAYS.between(challengeCreateAt.toLocalDate(), LocalDate.now());
        return (daysBetween >= period) ? -1 : daysBetween;
    }

    public Challenge findByIdOrElseThrow(Long challengeId) {
        return challengeRepository.findById(challengeId).orElseThrow(
                () -> new ChallengeException(ChallengeError.CHALLENGE_NOT_FOUND));
    }

    public Challenge findCurrentChallengeByUserId(Long userId) {
        User user = userService.findByIdOrThrowException(userId);
        return findByIdOrElseThrow(user.getCurrentChallengeId());
    }

    public List<ChallengeApp> getCurrentChallengeAppByChallengeId(Long challengeId) {
        return this.findByIdOrElseThrow(challengeId).getApps();
    }
}