package com.mizuho.external.mph.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceNotFoundExceptionTest {

    @Test
    void storesMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource was not found.");

        assertEquals("Resource was not found.", exception.getMessage());
    }
}
