package pl.slawkow.githubstats.users;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class UserDataWrapperTest {

    @Test
    void shouldCreateWrapperWithOkResponse() {
        //when
        UserDataWrapper result = UserDataWrapper.createOkResponse(new UserData(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                5,
                15,
                35L
        ));

        //then
        assertNotNull(result);
        assertEquals(new UserData(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                5,
                15,
                35L
        ), result.getUserData());
        assertEquals(UserDataWrapper.Status.OK, result.getStatus());
        assertNull(result.getError());
    }

    @Test
    void shouldThrowExceptionWhenCreatingOkResponseWithoutUserStats() {
        //when
        assertThrows(IllegalArgumentException.class, () -> UserDataWrapper.createOkResponse(null));
    }

    @Test
    void shouldThrowExceptionWhenCreatingErrorResponseWithoutError() {
        //when
        assertThrows(IllegalArgumentException.class, () -> UserDataWrapper.createErrorResponse(null));
    }

    @Test
    void shouldThrowExceptionWhenCreatingErrorResponseWithoutErrorAndWithoutUserStats() {
        //when
        assertThrows(IllegalArgumentException.class, () -> UserDataWrapper.createErrorResponse(null, null));
    }

    @Test
    void shouldThrowExceptionWhenCreatingErrorResponseWithoutErrorAndWithUserStats() {
        //when
        assertThrows(IllegalArgumentException.class, () -> UserDataWrapper.createErrorResponse(null, new UserData(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                5,
                15,
                35L
        )));
    }

    @Test
    void shouldReturnWrapperWithErrorWhenCreatingErrorResponseWithProperError() {
        //when
        UserDataWrapper result = UserDataWrapper.createErrorResponse(UserDataWrapper.Error.USER_NOT_FOUND);

        //then
        assertNotNull(result);
        assertEquals(UserDataWrapper.Status.ERROR, result.getStatus());
        assertEquals(UserDataWrapper.Error.USER_NOT_FOUND, result.getError());
        assertNull(result.getUserData());
    }

    @Test
    void shouldReturnWrapperWithErrorWhenCreatingErrorResponseWithProperErrorAndWithoutUserStats() {
        //when
        UserDataWrapper result = UserDataWrapper.createErrorResponse(UserDataWrapper.Error.USER_NOT_FOUND, null);

        //then
        assertNotNull(result);
        assertEquals(UserDataWrapper.Status.ERROR, result.getStatus());
        assertEquals(UserDataWrapper.Error.USER_NOT_FOUND, result.getError());
        assertNull(result.getUserData());
    }

    @Test
    void shouldReturnWrapperWithErrorWhenCreatingErrorResponseWithProperErrorAndWithUserStats() {
        //when
        UserDataWrapper result = UserDataWrapper.createErrorResponse(UserDataWrapper.Error.STATS_NOT_PERSISTED, new UserData(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                5,
                15,
                35L
        ));

        //then
        assertNotNull(result);
        assertEquals(UserDataWrapper.Status.ERROR, result.getStatus());
        assertEquals(UserDataWrapper.Error.STATS_NOT_PERSISTED, result.getError());
        assertEquals(new UserData(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                5,
                15,
                35L
        ), result.getUserData());
    }
}