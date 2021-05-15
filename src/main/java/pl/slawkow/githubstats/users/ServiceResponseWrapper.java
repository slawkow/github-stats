package pl.slawkow.githubstats.users;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceResponseWrapper<T> {
    private final T responseContent;
    private final Status status;
    private final Error error;

    public static <T> ServiceResponseWrapper<T> createOkResponse(T responseContent) {
        return new ServiceResponseWrapper<>(
                responseContent,
                Status.OK,
                null
        );
    }

    public static <T> ServiceResponseWrapper<T> createErrorResponse(Error error) {
        return new ServiceResponseWrapper<>(
                null,
                Status.ERROR,
                error
        );
    }

    public enum Status {
        OK, ERROR
    }

    public enum Error {
        NOT_FOUND, API_CLIENT_ERROR, API_SERVER_ERROR, INVALID_RESPONSE, UNEXPECTED_RESPONSE
    }
}
