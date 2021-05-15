package pl.slawkow.githubstats.users.github;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.slawkow.githubstats.common.rest.ServiceResponseWrapper;
import pl.slawkow.githubstats.users.UserData;

import java.time.OffsetDateTime;

@Component
public class GithubRestConnector {

    private static final String USERS_ENDPOINT = "/users/{login}";

    private final WebClient webClient;

    public GithubRestConnector(@Qualifier("githubWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public ServiceResponseWrapper<UserData> getUserData(String login) {
        ResponseEntity<UserDataResponse> response;
        try {
            response = obtainUserData(login);
        } catch (WebClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ServiceResponseWrapper.createErrorResponse(ServiceResponseWrapper.Error.NOT_FOUND);
            }

            if (exception.getStatusCode().is4xxClientError()) {
                return ServiceResponseWrapper.createErrorResponse(ServiceResponseWrapper.Error.API_CLIENT_ERROR);
            }

            if (exception.getStatusCode().is5xxServerError()) {
                return ServiceResponseWrapper.createErrorResponse(ServiceResponseWrapper.Error.API_SERVER_ERROR);
            }

            return ServiceResponseWrapper.createErrorResponse(ServiceResponseWrapper.Error.UNEXPECTED_RESPONSE);
        } catch (WebClientRequestException exception) {
            return ServiceResponseWrapper.createErrorResponse(ServiceResponseWrapper.Error.API_SERVER_ERROR);
        }

        if (response == null ||
                response.getBody() == null ||
                HttpStatus.OK != response.getStatusCode()) {
            return ServiceResponseWrapper.createErrorResponse(ServiceResponseWrapper.Error.INVALID_RESPONSE);
        }

        return ServiceResponseWrapper.createOkResponse(response.getBody().toInternal());
    }

    private ResponseEntity<UserDataResponse> obtainUserData(String login) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(USERS_ENDPOINT)
                        .build(login))
                .retrieve()
                .toEntity(UserDataResponse.class)
                .block();
    }

    @Value
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class UserDataResponse {
        @JsonProperty("id")
        private final Long id;
        @JsonProperty("login")
        private final String login;
        @JsonProperty("name")
        private final String name;
        @JsonProperty("type")
        private final String type;
        @JsonProperty("avatar_url")
        private final String avatarUrl;
        @JsonProperty("created_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
        private final OffsetDateTime createdAt;
        @JsonProperty("followers")
        private final Long followersCount;
        @JsonProperty("public_repos")
        private final Long publicRepositoriesCount;

        UserData toInternal() {
            return new UserData(
                    id,
                    login,
                    name,
                    type,
                    avatarUrl,
                    createdAt,
                    followersCount,
                    publicRepositoriesCount,
                    null
            );
        }
    }
}
