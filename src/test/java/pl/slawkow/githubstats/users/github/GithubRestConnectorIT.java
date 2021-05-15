package pl.slawkow.githubstats.users.github;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.slawkow.githubstats.common.rest.ServiceResponseWrapper;
import pl.slawkow.githubstats.users.UserData;
import pl.slawkow.githubstats.users.db.UserStatsStorage;
import pl.slawkow.githubstats.users.github.GithubRestConnector;
import pl.slawkow.githubstats.utils.DocumentTestUtils;
import pl.slawkow.githubstats.utils.WireMockExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
class GithubRestConnectorIT {

    @RegisterExtension
    public static WireMockExtension githubWireMockRule = new WireMockExtension(38081);

    @Autowired
    private GithubRestConnector githubRestConnector;

    @MockBean
    private UserStatsStorage userStatsStorage;

    @Test
    void getUserDataShouldReturnValidDataForOkResponse() throws IOException {
        //given
        githubWireMockRule.stubFor(get(urlEqualTo("/users/slawkow"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                        .withBody(DocumentTestUtils.loadJsonAsText("json/GithubRestConnectorIT/valid_response.json"))
                ));

        //when
        ServiceResponseWrapper<UserData> result = githubRestConnector.getUserData("slawkow");

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.OK, result.getStatus());
        assertEquals(new UserData(
                        11233089,
                        "slawkow",
                        "test",
                        "User",
                        "https://avatars.githubusercontent.com/u/11233089?v=4",
                        OffsetDateTime.of(LocalDateTime.of(2015, 2, 27, 17, 9, 22), ZoneOffset.UTC),
                        1,
                        3,
                        null
                ),
                result.getResponseContent());
    }

    @Test
    void getUserDataShouldReturnNotFoundErrorForNotFoundFromApi() {
        //given
        githubWireMockRule.stubFor(get(urlEqualTo("/users/slawkow"))
                .willReturn(aResponse()
                        .withStatus(404)
                ));

        //when
        ServiceResponseWrapper<UserData> result = githubRestConnector.getUserData("slawkow");

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.ERROR, result.getStatus());
        assertEquals(ServiceResponseWrapper.Error.NOT_FOUND, result.getError());
        assertNull(result.getResponseContent());
    }

    @Test
    void getUserDataShouldReturnApiClientErrorForClientError() {
        //given
        githubWireMockRule.stubFor(get(urlEqualTo("/users/slawkow"))
                .willReturn(aResponse()
                        .withStatus(422)
                ));

        //when
        ServiceResponseWrapper<UserData> result = githubRestConnector.getUserData("slawkow");

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.ERROR, result.getStatus());
        assertEquals(ServiceResponseWrapper.Error.API_CLIENT_ERROR, result.getError());
        assertNull(result.getResponseContent());
    }

    @Test
    void getUserDataShouldReturnApiServerErrorForServerError() {
        //given
        githubWireMockRule.stubFor(get(urlEqualTo("/users/slawkow"))
                .willReturn(aResponse()
                        .withStatus(503)
                ));

        //when
        ServiceResponseWrapper<UserData> result = githubRestConnector.getUserData("slawkow");

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.ERROR, result.getStatus());
        assertEquals(ServiceResponseWrapper.Error.API_SERVER_ERROR, result.getError());
        assertNull(result.getResponseContent());
    }

    @Test
    void getUserDataShouldReturnApiServerErrorForTimeoutResponse() throws IOException {
        //given
        githubWireMockRule.stubFor(get(urlEqualTo("/users/slawkow"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                        .withFixedDelay(1500)
                        .withBody(DocumentTestUtils.loadJsonAsText("json/GithubRestConnectorIT/valid_response.json"))
                ));

        //when
        ServiceResponseWrapper<UserData> result = githubRestConnector.getUserData("slawkow");

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.ERROR, result.getStatus());
        assertEquals(ServiceResponseWrapper.Error.API_SERVER_ERROR, result.getError());
        assertNull(result.getResponseContent());
    }

    @Test
    void getUserDataShouldReturnUnexpectedResponseForNoContentResponse() {
        //given
        githubWireMockRule.stubFor(get(urlEqualTo("/users/slawkow"))
                .willReturn(aResponse()
                        .withStatus(204)
                        .withHeader("Content-type", "application/json")
                ));

        //when
        ServiceResponseWrapper<UserData> result = githubRestConnector.getUserData("slawkow");

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.ERROR, result.getStatus());
        assertEquals(ServiceResponseWrapper.Error.INVALID_RESPONSE, result.getError());
        assertNull(result.getResponseContent());
    }

    //actually HTTP.202 are valid responses from github, but there is no enough description to understand this case
    @Test
    void getUserDataShouldReturnUnexpectedResponseForAcceptedResponse() {
        //given
        githubWireMockRule.stubFor(get(urlEqualTo("/users/slawkow"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-type", "application/json")
                        .withBody("{\"status\": \"pending\"}")
                ));

        //when
        ServiceResponseWrapper<UserData> result = githubRestConnector.getUserData("slawkow");

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.ERROR, result.getStatus());
        assertEquals(ServiceResponseWrapper.Error.INVALID_RESPONSE, result.getError());
        assertNull(result.getResponseContent());
    }

    @Test
    void getUserDataShouldReturnInvalidForNoResponseWithoutBody() {
        //given
        githubWireMockRule.stubFor(get(urlEqualTo("/users/slawkow"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                ));

        //when
        ServiceResponseWrapper<UserData> result = githubRestConnector.getUserData("slawkow");

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.ERROR, result.getStatus());
        assertEquals(ServiceResponseWrapper.Error.INVALID_RESPONSE, result.getError());
        assertNull(result.getResponseContent());
    }
}