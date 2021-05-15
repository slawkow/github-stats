package pl.slawkow.githubstats.users;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDataResponseTest {

    @Test
    void forValidResponseShouldCreateUserData() {
        //given
        GithubRestConnector.UserDataResponse response = new GithubRestConnector.UserDataResponse(
                123L,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                5L,
                15L
        );

        //when
        UserData result = response.toInternal();

        //then
        assertEquals(new UserData(
                        123,
                        "test",
                        "testName",
                        "testType",
                        "http://x.y/z",
                        OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                        5,
                        15,
                        null
                ),
                result);
    }
}
