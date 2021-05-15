package pl.slawkow.githubstats.common.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceResponseWrapperTest {

    @Test
    void shouldCreateOkResponseWithContent() {
        //when
        ServiceResponseWrapper<String> result = ServiceResponseWrapper.createOkResponse("some-content");

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.OK, result.getStatus());
        assertNull(result.getError());
        assertEquals("some-content", result.getResponseContent());
    }

    @Test
    void shouldCreateOkResponseWithNullContent() {
        //when
        ServiceResponseWrapper<String> result = ServiceResponseWrapper.createOkResponse(null);

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.OK, result.getStatus());
        assertNull(result.getError());
        assertNull(result.getResponseContent());
    }

    @Test
    void shouldThrowExceptionForErrorResponseWithNullError() {
        //when
        assertThrows(IllegalArgumentException.class, () -> ServiceResponseWrapper.createErrorResponse(null));
    }

    @Test
    void shouldPrepareWrapperForErrorResponseWithValidError() {
        //when
        ServiceResponseWrapper<String> result = ServiceResponseWrapper.
                createErrorResponse(ServiceResponseWrapper.Error.API_SERVER_ERROR);

        //then
        assertNotNull(result);
        assertEquals(ServiceResponseWrapper.Status.ERROR, result.getStatus());
        assertEquals(ServiceResponseWrapper.Error.API_SERVER_ERROR, result.getError());
        assertNull(result.getResponseContent());
    }
}