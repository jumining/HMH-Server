package sopt.org.hmh.domain.dailychallenge.domain;

import static jakarta.persistence.GenerationType.*;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import sopt.org.hmh.domain.app.domain.HistoryApp;
import sopt.org.hmh.global.common.domain.BaseTimeEntity;
import sopt.org.hmh.domain.challenge.domain.Challenge;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyChallenge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @OneToMany(mappedBy = "dailyChallenge", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<HistoryApp> apps;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long goalTime;

    @Column(nullable = false)
    private LocalDate challengeDate;

    @Builder
    DailyChallenge(Challenge challenge, Long userId, Long goalTime, LocalDate challengeDate) {
        Assert.notNull(challenge, "Challenge must not be null");
        Assert.notNull(userId, "UserId must not be null");
        Assert.notNull(goalTime, "GoalTime must not be null");
        Assert.notNull(challengeDate, "ChallengeDate must not be null");

        this.challenge = challenge;
        this.userId = userId;
        this.goalTime = goalTime;
        this.challengeDate = challengeDate;
        this.status = Status.NONE;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }
}