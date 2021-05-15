package pl.slawkow.githubstats.users;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class UserStatsWrapperTest {

    @Test
    void shouldCreateWrapperWithOkResponse() {
        //when
        UserStatsWrapper result = UserStatsWrapper.createOkResponse(new UserStats(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                35L
        ));

        //then
        assertNotNull(result);
        assertEquals(new UserStats(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                35L
        ), result.getUserStats());
        assertEquals(UserStatsWrapper.Status.OK, result.getStatus());
        assertNull(result.getError());
    }

    @Test
    void shouldThrowExceptionWhenCreatingOkResponseWithoutUserStats() {
        //when
        assertThrows(IllegalArgumentException.class, () -> UserStatsWrapper.createOkResponse(null));
    }

    @Test
    void shouldThrowExceptionWhenCreatingErrorResponseWithoutError() {
        //when
        assertThrows(IllegalArgumentException.class, () -> UserStatsWrapper.createErrorResponse(null));
    }

    @Test
    void shouldThrowExceptionWhenCreatingErrorResponseWithoutErrorAndWithoutUserStats() {
        //when
        assertThrows(IllegalArgumentException.class, () -> UserStatsWrapper.createErrorResponse(null, null));
    }

    @Test
    void shouldThrowExceptionWhenCreatingErrorResponseWithoutErrorAndWithUserStats() {
        //when
        assertThrows(IllegalArgumentException.class, () -> UserStatsWrapper.createErrorResponse(null, new UserStats(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                35L
        )));
    }

    @Test
    void shouldReturnWrapperWithErrorWhenCreatingErrorResponseWithProperError() {
        //when
        UserStatsWrapper result = UserStatsWrapper.createErrorResponse(UserStatsWrapper.Error.USER_NOT_FOUND);

        //then
        assertNotNull(result);
        assertEquals(UserStatsWrapper.Status.ERROR, result.getStatus());
        assertEquals(UserStatsWrapper.Error.USER_NOT_FOUND, result.getError());
        assertNull(result.getUserStats());
    }

    @Test
    void shouldReturnWrapperWithErrorWhenCreatingErrorResponseWithProperErrorAndWithoutUserStats() {
        //when
        UserStatsWrapper result = UserStatsWrapper.createErrorResponse(UserStatsWrapper.Error.USER_NOT_FOUND, null);

        //then
        assertNotNull(result);
        assertEquals(UserStatsWrapper.Status.ERROR, result.getStatus());
        assertEquals(UserStatsWrapper.Error.USER_NOT_FOUND, result.getError());
        assertNull(result.getUserStats());
    }

    @Test
    void shouldReturnWrapperWithErrorWhenCreatingErrorResponseWithProperErrorAndWithUserStats() {
        //when
        UserStatsWrapper result = UserStatsWrapper.createErrorResponse(UserStatsWrapper.Error.STATS_NOT_PERSISTED, new UserStats(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                35L
        ));

        //then
        assertNotNull(result);
        assertEquals(UserStatsWrapper.Status.ERROR, result.getStatus());
        assertEquals(UserStatsWrapper.Error.STATS_NOT_PERSISTED, result.getError());
        assertEquals(new UserStats(
                123,
                "test",
                "testName",
                "testType",
                "http://x.y/z",
                OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                35L
        ), result.getUserStats());
    }
}