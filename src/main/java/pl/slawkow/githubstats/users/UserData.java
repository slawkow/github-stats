package pl.slawkow.githubstats.users;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
@ToString
@Getter
@EqualsAndHashCode
public class UserData {
    private final long id;
    private final String login;
    private final String name;
    private final String type;
    private final String avatarUrl;
    private final OffsetDateTime createdAt;
    private final long followersCount;
    private final long publicRepositoriesCount;
    private Long calculations;

    public UserData(long id,
                    String login,
                    String name,
                    String type,
                    String avatarUrl,
                    OffsetDateTime createdAt,
                    long followersCount,
                    long publicRepositoriesCount,
                    Long calculations) {
        if (id < 0) {
            throw new IllegalArgumentException("id cannot be lower than 0");
        }
        if (followersCount < 0) {
            throw new IllegalArgumentException("followersCount cannot be lower than 0");
        }
        if (publicRepositoriesCount < 0) {
            throw new IllegalArgumentException("publicRepositoriesCount cannot be lower than 0");
        }
        this.id = id;
        this.login = login;
        this.name = name;
        this.type = type;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.followersCount = followersCount;
        this.publicRepositoriesCount = publicRepositoriesCount;
        this.calculations = calculations;
    }

    public UserData calculateStats() {
        if (followersCount == 0) {
            log.warn("Cannot calculate stats for 0 followers");
            calculations = null;
            return this;
        }

        calculations = 6 / followersCount * (2 + publicRepositoriesCount);
        log.info("Calculated stats for user {} with params: followers: {}, public repos: {}. Result is: {}",
                login, followersCount, publicRepositoriesCount, calculations);
        return this;
    }
}
