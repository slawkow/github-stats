package pl.slawkow.githubstats.users.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.slawkow.githubstats.users.UserService;
import pl.slawkow.githubstats.users.UserData;
import pl.slawkow.githubstats.users.UserDataWrapper;
import pl.slawkow.githubstats.utils.DocumentTestUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
class UsersControllerIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void controllerShouldReturnValidResponseWhenStatusIsOk() throws Exception {
        //given
        when(userService.getUserStats("test")).thenReturn(UserDataWrapper.createOkResponse(
                new UserData(
                        123,
                        "test",
                        "testName",
                        "testType",
                        "http://x.y/z",
                        OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                        5,
                        15,
                        35.0
                )
        ));

        //when
        mvc.perform(get("/users/test")
                .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(DocumentTestUtils.loadJsonAsText(
                        "json/UsersControllerIT/valid_response.json"
                )));
    }

    @Test
    void controllerShouldReturnValidResponseWhenStatusIsErrorButOnlyNotPersisted() throws Exception {
        //given
        when(userService.getUserStats("test")).thenReturn(UserDataWrapper.createErrorResponse(
                UserDataWrapper.Error.REQUEST_COUNTER_NOT_PERSISTED,
                new UserData(
                        123,
                        "test",
                        "testName",
                        "testType",
                        "http://x.y/z",
                        OffsetDateTime.of(LocalDateTime.of(2021, 5, 15, 10, 10), ZoneOffset.UTC),
                        5,
                        15,
                        35.0
                )
        ));

        //when
        mvc.perform(get("/users/test")
                .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(content().json(DocumentTestUtils.loadJsonAsText(
                        "json/UsersControllerIT/valid_response.json"
                )));
    }

    @Test
    void controllerShouldReturnNotFoundWhenUserWasNotFoundInExternalApi() throws Exception {
        //given
        when(userService.getUserStats("test")).thenReturn(UserDataWrapper.createErrorResponse(
                UserDataWrapper.Error.USER_NOT_FOUND
        ));

        //when
        mvc.perform(get("/users/test")
                .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    void controllerShouldReturnServiceUnavailableWhenExternalApiErrorOccurred() throws Exception {
        //given
        when(userService.getUserStats("test")).thenReturn(UserDataWrapper.createErrorResponse(
                UserDataWrapper.Error.EXTERNAL_API_ERROR
        ));

        //when
        mvc.perform(get("/users/test")
                .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void controllerShouldReturnNotFoundWhenLoginNotProvided() throws Exception {
        //when
        mvc.perform(get("/users/")
                .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isNotFound());
    }
}