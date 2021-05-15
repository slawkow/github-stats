package pl.slawkow.githubstats.users.db;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserStatsStorage extends CrudRepository<UserStatsDbEntity, String> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO user_stats (login, request_count) VALUES (:login, 1) " +
            "ON CONFLICT (login) DO UPDATE SET request_count = user_stats.request_count + 1",
            nativeQuery = true)
    void incrementRequestCount(String login);
}
