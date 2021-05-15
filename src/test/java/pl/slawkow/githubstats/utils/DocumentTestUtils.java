package pl.slawkow.githubstats.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DocumentTestUtils {

    private DocumentTestUtils() {
    }

    public static String loadJsonAsText(String resourceName) throws IOException {
        try (InputStream inputStream = new ClassPathResource(resourceName).getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
