package sopt.org.hmh.domain.app.dto.request;

import jakarta.validation.constraints.NotNull;
import sopt.org.hmh.domain.app.domain.AppConstants;
import sopt.org.hmh.domain.app.domain.exception.AppError;
import sopt.org.hmh.domain.app.domain.exception.AppException;

public record ChallengeAppRequest(
        @NotNull(message = "앱 코드는 null일 수 없습니다.")
        String appCode,
        @NotNull(message = "앱 시간은 null일 수 없습니다.")
        Long goalTime
) {
        public ChallengeAppRequest {
                if (goalTime > AppConstants.MAXIMUM_APP_TIME || goalTime < AppConstants.MINIMUM_APP_TIME) {
                        throw new AppException(AppError.INVALID_GOAL_TIME);
                }
        }
}
