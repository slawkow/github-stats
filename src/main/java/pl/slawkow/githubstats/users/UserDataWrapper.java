package pl.slawkow.githubstats.users;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDataWrapper {
    private final UserData userData;
    private final Status status;
    private final Error error;

    public static UserDataWrapper createOkResponse(UserData userData) {
        if (userData == null) {
            throw new IllegalArgumentException("User stats cannot be null for OK response");
        }

        return new UserDataWrapper(
                userData, Status.OK, null
        );
    }

    public static UserDataWrapper createErrorResponse(Error error) {
        return createErrorResponse(error, null);
    }

    public static UserDataWrapper createErrorResponse(Error error, UserData userData) {
        if (error == null) {
            throw new IllegalArgumentException("Error cannot be null for ERROR response");
        }

        return new UserDataWrapper(
                userData, Status.ERROR, error
        );
    }

    public enum Status {
        OK, ERROR
    }

    public enum Error {
        USER_NOT_FOUND, EXTERNAL_API_ERROR, STATS_NOT_PERSISTED
    }
}
