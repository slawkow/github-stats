package pl.slawkow.githubstats.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.slawkow.githubstats.common.rest.ServiceResponseWrapper;
import pl.slawkow.githubstats.users.db.UsersDatabaseRepository;
import pl.slawkow.githubstats.users.github.GithubRestConnector;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private GithubRestConnector githubRestConnector;

    @Mock
    private UsersDatabaseRepository usersDatabaseRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(githubRestConnector, usersDatabaseRepository);
    }

    @Test
    void forNullLoginServiceShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUserStats(null));
    }

    @Test
    void forNullResponseFromGithubRestConnectorServiceShouldThrowException() {
        //given
        when(githubRestConnector.getUserData("test")).thenReturn(null);

        //when
        assertThrows(IllegalStateException.class, () -> userService.getUserStats("test"));
    }

    @Test
    void forOkStatusFromGithubConnectorAndPersistedCounterServiceShouldReturnOkResponseWithCalculatedStats() {
        //given
        when(githubRestConnector.getUserData("test")).thenReturn(ServiceResponseWrapper.createOkResponse(
                new UserData(
                        123,
                        "test",
                        "testName",
                        "testType",
                        "http://x.y/z",
                        OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                        5,
                        15,
                        null
                )
        ));

        //when
        UserDataWrapper result = userService.getUserStats("test");

        //then
        assertNotNull(result);
        assertEquals(UserDataWrapper.Status.OK, result.getStatus());
        assertNull(result.getError());
        assertEquals(new UserData(
                        123,
                        "test",
                        "testName",
                        "testType",
                        "http://x.y/z",
                        OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                        5,
                        15,
                        20.4
                ),
                result.getUserData());
    }

    @Test
    void forOkStatusFromGithubConnectorAndNotPersistedCounterServiceShouldReturnErrorResponseWithCalculatedStats() {
        //given
        when(githubRestConnector.getUserData("test")).thenReturn(ServiceResponseWrapper.createOkResponse(
                new UserData(
                        123,
                        "test",
                        "testName",
                        "testType",
                        "http://x.y/z",
                        OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                        5,
                        15,
                        null
                )
        ));

        doThrow(RuntimeException.class).when(usersDatabaseRepository).incrementRequestCounter("test");

        //when
        UserDataWrapper result = userService.getUserStats("test");

        //then
        assertNotNull(result);
        assertEquals(UserDataWrapper.Status.ERROR, result.getStatus());
        assertEquals(UserDataWrapper.Error.REQUEST_COUNTER_NOT_PERSISTED, result.getError());
        assertEquals(new UserData(
                        123,
                        "test",
                        "testName",
                        "testType",
                        "http://x.y/z",
                        OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                        5,
                        15,
                        20.4
                ),
                result.getUserData());
    }

    @Test
    void forErrorStatusFromGithubConnectorBecauseOfNotFoundUserServiceShouldReturnErrorResponseWithNotFoundError() {
        //given
        when(githubRestConnector.getUserData("test")).thenReturn(ServiceResponseWrapper.createErrorResponse(
                ServiceResponseWrapper.Error.NOT_FOUND
        ));

        //when
        UserDataWrapper result = userService.getUserStats("test");

        //then
        assertNotNull(result);
        assertEquals(UserDataWrapper.Status.ERROR, result.getStatus());
        assertEquals(UserDataWrapper.Error.USER_NOT_FOUND, result.getError());
        assertNull(result.getUserData());
    }

    @ParameterizedTest
    @EnumSource(value = ServiceResponseWrapper.Error.class,
            names = {"API_CLIENT_ERROR", "API_SERVER_ERROR", "INVALID_RESPONSE", "UNEXPECTED_RESPONSE" })
    void forErrorStatusFromGithubConnectorCausedOfErrorResponseUserServiceShouldReturnErrorResponseWithExternalApiError(
            ServiceResponseWrapper.Error error
    ) {
        //given
        when(githubRestConnector.getUserData("test")).thenReturn(ServiceResponseWrapper.createErrorResponse(error));

        //when
        UserDataWrapper result = userService.getUserStats("test");

        //then
        assertNotNull(result);
        assertEquals(UserDataWrapper.Status.ERROR, result.getStatus());
        assertEquals(UserDataWrapper.Error.EXTERNAL_API_ERROR, result.getError());
        assertNull(result.getUserData());
    }

    @Test
    void forErrorStatusFromGithubConnectorBecauseOfApiErrorAndNotPersistedCounterUserServiceShouldReturnErrorResponseWithApiError() {
        //given
        when(githubRestConnector.getUserData("test")).thenReturn(ServiceResponseWrapper.createErrorResponse(
                ServiceResponseWrapper.Error.API_SERVER_ERROR
        ));

        doThrow(RuntimeException.class).when(usersDatabaseRepository).incrementRequestCounter("test");

        //when
        UserDataWrapper result = userService.getUserStats("test");

        //then
        assertNotNull(result);
        assertEquals(UserDataWrapper.Status.ERROR, result.getStatus());
        assertEquals(UserDataWrapper.Error.EXTERNAL_API_ERROR, result.getError());
        assertNull(result.getUserData());
    }
}