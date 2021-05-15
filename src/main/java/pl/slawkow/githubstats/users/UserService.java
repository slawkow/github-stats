package pl.slawkow.githubstats.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final GithubRestConnector githubRestConnector;
    private final UsersDatabaseRepository usersDatabaseRepository;

    public UserDataWrapper getUserStats(String login) {
        if (login == null) {
            throw new IllegalArgumentException("login cannot be null");
        }

        //TODO handle errors
        ServiceResponseWrapper<UserData> userDataResponse = githubRestConnector.getUserData(login);

        calculateStats(userDataResponse.getResponseContent());

        usersDatabaseRepository.incrementRequestCounter(login);

        return UserDataWrapper.createOkResponse(userDataResponse.getResponseContent());
    }

    private void calculateStats(UserData userData) {
        //TODO implement "calculations"
    }
}
