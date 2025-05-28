package com.renatmirzoev.moviebookingservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renatmirzoev.moviebookingservice.config.JsonConfig;
import com.renatmirzoev.moviebookingservice.exception.JsonParsingException;
import org.springframework.lang.Nullable;


public class JsonUtils {

    private JsonUtils() {
    }

    private static final ObjectMapper MAPPER = new JsonConfig().objectMapper();

    public static <T> String toJson(T object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            throw new JsonParsingException("Error during serialization", exception);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException exception) {
            throw new JsonParsingException("Error during deserialization", exception);
        }
    }

    @Nullable
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException exception) {
            throw new JsonParsingException("Error during deserialization", exception);
        }
    }
}
