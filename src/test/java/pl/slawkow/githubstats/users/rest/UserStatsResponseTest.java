package pl.slawkow.githubstats.users.rest;

import org.junit.jupiter.api.Test;
import pl.slawkow.githubstats.users.UserData;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class UserStatsResponseTest {

    @Test
    void shouldThrowExceptionWhenTryToBuildFromNullData() {
        assertThrows(IllegalArgumentException.class, () -> UsersController.UserStatsResponse.from(null));
    }

    @Test
    void shouldCreateValidResponseFromValidData() {
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
                15.0
        );

        //when
        UsersController.UserStatsResponse result = UsersController.UserStatsResponse.from(userData);

        //then
        assertNotNull(result);
        assertEquals(new UsersController.UserStatsResponse(
                "123",
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                "15.0"
        ),
                result);
    }
}
