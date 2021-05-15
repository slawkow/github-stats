package pl.slawkow.githubstats.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final GithubRestConnector githubRestConnector;
    private final UsersDatabaseRepository usersDatabaseRepository;

    public UserDataWrapper getUserStats(String login) {
        if (login == null) {
            throw new IllegalArgumentException("login cannot be null");
        }

        ServiceResponseWrapper<UserData> userDataResponse = githubRestConnector.getUserData(login);

        if (userDataResponse == null) {
            throw new IllegalStateException("userDataResponse cannot be null");
        }

        boolean requestCounterPersisted = incrementRequestCounter(login);

        UserData calculatedUserData;
        switch (userDataResponse.getStatus()) {
            case OK:
                calculatedUserData = userDataResponse.getResponseContent().calculateStats();
                break;
            case ERROR:
                switch (userDataResponse.getError()) {
                    case NOT_FOUND:
                        return UserDataWrapper.createErrorResponse(UserDataWrapper.Error.USER_NOT_FOUND);
                    case API_CLIENT_ERROR:
                    case API_SERVER_ERROR:
                    case INVALID_RESPONSE:
                    case UNEXPECTED_RESPONSE:
                        return UserDataWrapper.createErrorResponse(UserDataWrapper.Error.EXTERNAL_API_ERROR);
                    default:
                        throw new IllegalStateException("Unexpected error value: " + userDataResponse.getError());
                }
            default:
                throw new IllegalStateException("Unexpected status value: " + userDataResponse.getStatus());
        }

        if (!requestCounterPersisted) {
            return UserDataWrapper.createErrorResponse(UserDataWrapper.Error.REQUEST_COUNTER_NOT_PERSISTED, calculatedUserData);
        }

        return UserDataWrapper.createOkResponse(calculatedUserData);
    }

    private boolean incrementRequestCounter(String login) {
        try {
            usersDatabaseRepository.incrementRequestCounter(login);
            return true;
        } catch (Exception exception) {
            log.error("Exception occurred while trying to increment request counter", exception);
            return false;
        }
    }
}
