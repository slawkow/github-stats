package pl.slawkow.githubstats.users;

import lombok.Value;

import java.time.OffsetDateTime;

@Value
public class UserStats {
    private final long id;
    private final String login;
    private final String name;
    private final String type;
    private final String avatarUrl;
    private final OffsetDateTime createdAt;
    private final Long calculations;
}
