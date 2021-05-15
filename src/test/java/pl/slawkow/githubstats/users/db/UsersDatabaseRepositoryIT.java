package pl.slawkow.githubstats.users.db;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@DataJpaTest
@Import(UsersDatabaseRepositoryIT.UsersDatabaseRepositoryITConfig.class)
@ContextConfiguration(initializers = {UsersDatabaseRepositoryIT.Initializer.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersDatabaseRepositoryIT {

    @Autowired
    private UsersDatabaseRepository usersDatabaseRepository;

    @Autowired
    private UserStatsStorage userStatsStorage;

    private static final PostgreSQLContainer<?> DB_CONTAINER;
    private static final int DEFAULT_POSTGRES_PORT = 5432;

    static {
        DB_CONTAINER = new PostgreSQLContainer<>("postgres:13.3")
                .withDatabaseName("github-stats")
                .withUsername("test")
                .withPassword("test");

        DB_CONTAINER.start();
    }

    @Test
    void repoShouldReturnFreshlySavedLoginWithOneRequestCount() {
        //when
        usersDatabaseRepository.incrementRequestCounter("test");

        //then
        assertThat(getAllUserStats()).extracting("login", "requestCount")
                .containsExactlyInAnyOrder(tuple("test", 1L));
    }

    @Test
    void repoShouldReturnSaveLoginWithCounterEqualThreeWhenThreeTimesInvokedIncrementForTheSameLogin() {
        //when
        usersDatabaseRepository.incrementRequestCounter("test");
        usersDatabaseRepository.incrementRequestCounter("test");
        usersDatabaseRepository.incrementRequestCounter("test");

        //then
        assertThat(getAllUserStats()).extracting("login", "requestCount")
                .containsExactlyInAnyOrder(tuple("test", 3L));
    }

    @Test
    void repoShouldReturnSaveLoginWithSeparateCountersForEachLogin() {
        //when
        usersDatabaseRepository.incrementRequestCounter("test");
        usersDatabaseRepository.incrementRequestCounter("test");
        usersDatabaseRepository.incrementRequestCounter("test-another");
        usersDatabaseRepository.incrementRequestCounter("test-another");
        usersDatabaseRepository.incrementRequestCounter("test-another");
        usersDatabaseRepository.incrementRequestCounter("fresh");

        //then
        assertThat(getAllUserStats()).extracting("login", "requestCount")
                .containsExactlyInAnyOrder(
                        tuple("test", 2L),
                        tuple("test-another", 3L),
                        tuple("fresh", 1L)
                );
    }

    private List<UserStatsDbEntity> getAllUserStats() {
        return StreamSupport.stream(userStatsStorage.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public static class UsersDatabaseRepositoryITConfig {
        @Bean
        public UsersDatabaseRepository usersDatabaseRepository(UserStatsStorage userStatsStorage) {
            return new UsersDatabaseRepository(userStatsStorage);
        }
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues propertyValues = TestPropertyValues.empty();
            DB_CONTAINER.start();
            propertyValues = propertyValues.and(
                    String.format("spring.datasource.url=jdbc:postgresql://%s:%s/github-stats", DB_CONTAINER.getContainerIpAddress(), DB_CONTAINER.getMappedPort(DEFAULT_POSTGRES_PORT)),
                    "spring.datasource.username=test",
                    "spring.datasource.password=test",
                    "spring.datasource.driverClassName=org.postgresql.Driver"
            );

            propertyValues.applyTo(configurableApplicationContext);
        }
    }
}