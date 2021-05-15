package pl.slawkow.githubstats.users.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersDatabaseRepository {
    private final UserStatsStorage userStatsStorage;

    public void incrementRequestCounter(String login) {
        if (login == null) {
            throw new IllegalArgumentException("login cannot be null");
        }

        userStatsStorage.incrementRequestCount(login);
    }
}
