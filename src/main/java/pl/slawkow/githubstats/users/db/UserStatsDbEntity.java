package pl.slawkow.githubstats.users.db;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "user_stats")
@Table(name = "user_stats")
public class UserStatsDbEntity {
    @Id
    private String login;
    private long requestCount;
}
