package sopt.org.HMH.domain.user;

import jakarta.persistence.*;
import sopt.org.HMH.domain.challenge.domain.Challenge;
import sopt.org.HMH.global.common.domain.BaseTimeEntity;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Integer Id;

    @OneToMany(mappedBy = "user")
    private List<Challenge> challenges;
}