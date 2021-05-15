package pl.slawkow.githubstats.users.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.slawkow.githubstats.users.UserData;
import pl.slawkow.githubstats.users.UserDataWrapper;
import pl.slawkow.githubstats.users.UserService;

import java.time.OffsetDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @GetMapping(value = "/{login}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable String login) {
        UserDataWrapper userDataWrapper = userService.getUserStats(login);

        if (userDataWrapper == null) {
            throw new IllegalArgumentException("userStatsWrapper cannot be null");
        }

        if (userDataWrapper.getStatus() == UserDataWrapper.Status.ERROR) {
            switch (userDataWrapper.getError()) {
                case EXTERNAL_API_ERROR:
                    String errorMessage = String.format("External API returned error, while trying to obtain info for user: %s", login);
                    log.error(errorMessage);
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build(); //TODO body
                case USER_NOT_FOUND:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); //TODO body
                case REQUEST_COUNTER_NOT_PERSISTED:
                    log.warn("Data for user {} were obtained correctly, but requests counter was not incremented " +
                            "properly", login);
                    return ResponseEntity.ok(UserStatsResponse.from(userDataWrapper.getUserData()));
                default:
                    log.error("Unsupported error type: {}", userDataWrapper.getError());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); //TODO body
            }
        }

        return ResponseEntity.ok(UserStatsResponse.from(userDataWrapper.getUserData()));
    }

    @Value
    static class UserStatsResponse {
        //disclaimer: personally I would choose another types for id and calculations, but documentation says that they
        //have to be Strings
        private final String id;
        private final String login;
        private final String name;
        private final String type;
        private final String avatarUrl;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
        private final OffsetDateTime createdAt;
        private final String calculations;

        static UserStatsResponse from(UserData userData) {
            if (userData == null) {
                throw new IllegalArgumentException("UserStats cannot be null while trying to prepare response");
            }

            return new UserStatsResponse(
                    String.valueOf(userData.getId()),
                    userData.getLogin(),
                    userData.getName(),
                    userData.getType(),
                    userData.getAvatarUrl(),
                    userData.getCreatedAt(),
                    userData.getCalculations() != null ? userData.getCalculations().toString() : null
            );
        }
    }
}
