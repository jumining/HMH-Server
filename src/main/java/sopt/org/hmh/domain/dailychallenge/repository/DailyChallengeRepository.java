package sopt.org.hmh.domain.dailychallenge.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import sopt.org.hmh.domain.dailychallenge.domain.DailyChallenge;

public interface DailyChallengeRepository {

    Optional<DailyChallenge> findByChallengeDateAndUserId(LocalDate challengeDate, Long userId);

    List<DailyChallenge> findAllByChallengeId(Long challengeId);
}