package pl.slawkow.githubstats.users;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class UserData {
    private long id;
    private String login;
    private String name;
    private String type;
    private String avatarUrl;
    private OffsetDateTime createdAt;
    private long followersCount;
    private long publicRepositoriesCount;
    private Long calculations;
}
