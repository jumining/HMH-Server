package sopt.org.HMH.domain.app.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.HMH.domain.app.domain.App;
import sopt.org.HMH.domain.app.dto.request.AppDeleteRequest;
import sopt.org.HMH.domain.app.dto.request.AppGoalTimeRequest;
import sopt.org.HMH.domain.app.repository.AppRepository;
import sopt.org.HMH.domain.challenge.repository.ChallengeRepository;
import sopt.org.HMH.domain.dailychallenge.domain.DailyChallenge;
import sopt.org.HMH.domain.dailychallenge.repository.DailyChallengeRepository;
import sopt.org.HMH.domain.dailychallenge.service.DailyChallengeService;
import sopt.org.HMH.global.util.IdConverter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppService {

    private final AppRepository appRepository;

    private final DailyChallengeService dailyChallengeService;

    @Transactional
    public void removeApp(Long userId, AppDeleteRequest request, String os) {
        Long todayDailyChallengeId = dailyChallengeService.getTodayDailyChallengeByUserId(userId).getId();
        App app = appRepository.findByDailyChallengeIdAndAppCodeAndOs(todayDailyChallengeId, request.appCode(), os);

        appRepository.deleteById(app.getId());
    }

    @Transactional
    public void addAppsByUserId(Long userId, List<AppGoalTimeRequest> requests, String os) {
        for (AppGoalTimeRequest request : requests) {
            appRepository.save(App.builder()
                    .dailyChallenge(dailyChallengeService.getTodayDailyChallengeByUserId(userId))
                    .appCode(request.appCode())
                    .goalTime(request.goalTime())
                    .os(os).build());
        }
    }
}