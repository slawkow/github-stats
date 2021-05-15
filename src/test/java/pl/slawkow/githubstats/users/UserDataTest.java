package pl.slawkow.githubstats.users;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class UserDataTest {

    @Test
    void constructorShouldNotAllowToCreateObjectWithNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> new UserData(
                -123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                5,
                15,
                20.4
        ));
    }

    @Test
    void constructorShouldNotAllowToCreateObjectWithNegativeFollowersCount() {
        assertThrows(IllegalArgumentException.class, () -> new UserData(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                -5,
                15,
                20.4
        ));
    }

    @Test
    void constructorShouldNotAllowToCreateObjectWithNegativePublicRepositories() {
        assertThrows(IllegalArgumentException.class, () -> new UserData(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                5,
                -15,
                20.4
        ));
    }

    @Test
    void shouldDoNotCalculateStatsWhenTryingToCalculateStatsFor0FollowersCount() {
        //given
        UserData userData = new UserData(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                0,
                15,
                null
        );

        //when
        UserData result = userData.calculateStats();

        //then
        assertEquals(new UserData(
                        123,
                        "test",
                        "testName",
                        "testType",
                        "http://x.y/z",
                        OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                        0,
                        15,
                        null
                ),
                result);
    }

    @Test
    void shouldCalculateStatsWhenCalculateStatsForValidInput() {
        //given
        UserData userData = new UserData(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                15,
                15,
                null
        );

        //when
        UserData result = userData.calculateStats();

        //then
        assertEquals(new UserData(
                        123,
                        "test",
                        "testName",
                        "testType",
                        "http://x.y/z",
                        OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                        15,
                        15,
                        6.8
                ),
                result);
    }

    @Test
    void shouldCalculateStatsWhenCalculateStatsFor0PublicRepositories() {
        //given
        UserData userData = new UserData(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                15,
                0,
                null
        );

        //when
        UserData result = userData.calculateStats();

        //then
        assertEquals(new UserData(
                        123,
                        "test",
                        "testName",
                        "testType",
                        "http://x.y/z",
                        OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                        15,
                        0,
                        0.8
                ),
                result);
    }
}