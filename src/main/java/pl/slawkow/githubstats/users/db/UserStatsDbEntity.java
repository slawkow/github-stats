package pl.slawkow.githubstats.users.db;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity(name = "user_stats")
@Table(name = "user_stats")
public class UserStatsDbEntity {
    @Id
    private String login;
    private long requestCount;
}
