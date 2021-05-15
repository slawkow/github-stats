package pl.slawkow.githubstats.users;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserStatsWrapper {
    private final UserStats userStats;
    private final Status status;
    private final Error error;

    public static UserStatsWrapper createOkResponse(UserStats userStats) {
        if (userStats == null) {
            throw new IllegalArgumentException("User stats cannot be null for OK response");
        }

        return new UserStatsWrapper(
                userStats, Status.OK, null
        );
    }

    public static UserStatsWrapper createErrorResponse(Error error) {
        return createErrorResponse(error, null);
    }

    public static UserStatsWrapper createErrorResponse(Error error, UserStats userStats) {
        if (error == null) {
            throw new IllegalArgumentException("Error cannot be null for ERROR response");
        }

        return new UserStatsWrapper(
                userStats, Status.ERROR, error
        );
    }

    public enum Status {
        OK, ERROR
    }

    public enum Error {
        USER_NOT_FOUND, EXTERNAL_API_ERROR, STATS_NOT_PERSISTED
    }
}
