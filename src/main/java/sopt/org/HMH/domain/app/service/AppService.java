package sopt.org.HMH.domain.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.HMH.domain.app.domain.App;
import sopt.org.HMH.domain.app.dto.request.AppDeleteRequest;
import sopt.org.HMH.domain.app.dto.request.AppGoalTimeRequest;
import sopt.org.HMH.domain.app.repository.AppRepository;
import sopt.org.HMH.domain.dailychallenge.domain.DailyChallenge;
import sopt.org.HMH.domain.dailychallenge.service.DailyChallengeService;

import java.util.ArrayList;
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
    public void addAppsAndUpdateRemainingDailyChallenge(Long userId, List<AppGoalTimeRequest> requests, String os) {
        List<DailyChallenge> dailyChallenges = dailyChallengeService.getRemainingDailyChallengesByUserId(userId);
        for (DailyChallenge dailyChallenge : dailyChallenges) {
            addApps(dailyChallenge, requests, os);
        }
    }

    @Transactional
    public List<App> addApps(DailyChallenge dailyChallenge, List<AppGoalTimeRequest> requests, String os) {
        List<App> apps = new ArrayList<>();
        for (AppGoalTimeRequest request : requests) {
            apps.add(App.builder()
                    .dailyChallenge(dailyChallenge)
                    .appCode(request.appCode())
                    .goalTime(request.goalTime())
                    .os(os).build());
        }
        appRepository.saveAll(apps);

        return apps;
    }
}