package sopt.org.HMH.global.auth.social.kakao.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoUserResponse {

    private Long id;

    private KakaoAccount kakaoAccount;
}
