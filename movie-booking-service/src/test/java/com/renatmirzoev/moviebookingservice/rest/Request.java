package com.renatmirzoev.moviebookingservice.rest;

import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class Request<T> {

    private final String endpoint;
    private final Class<T> clazz;
    private final MockMvc mockMvc;

    @Setter
    private HttpMethod httpMethod = HttpMethod.GET;
    @Setter
    private Map<String, String> pathParams;
    @Setter
    private Map<String, String> queryParams;
    @Setter
    private Object payload;

    @SneakyThrows
    public ResponseEntity<T> andReturn() {
        URI uri = buildUri(endpoint, pathParams, queryParams);
        MockHttpServletRequestBuilder requestBuilder = requestBuilder(httpMethod, uri);

        if (payload != null) {
            requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(payload));
        }

        MvcResult mvcResult;
        try {
            mvcResult = mockMvc.perform(requestBuilder).andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return extractResponseEntity(mvcResult, clazz);
    }

    @SneakyThrows
    protected ResponseEntity<T> extractResponseEntity(MvcResult result, Class<T> clazz) {
        MockHttpServletResponse response = result.getResponse();

        HttpHeaders headers = new HttpHeaders();
        response.getHeaderNames().forEach(header ->
            headers.addAll(header, response.getHeaders(header)));

        ResponseEntity.BodyBuilder builder = ResponseEntity
            .status(response.getStatus())
            .headers(headers);

        String responseBody = response.getContentAsString();
        return StringUtils.hasText(responseBody)
            ? builder.body(JsonUtils.fromJson(responseBody, clazz))
            : builder.build();
    }

    private static MockHttpServletRequestBuilder requestBuilder(HttpMethod method, URI uri) {
        Function<URI, MockHttpServletRequestBuilder> requestBuilderFunction = switch (method.name()) {
            case "GET" -> MockMvcRequestBuilders::get;
            case "POST" -> MockMvcRequestBuilders::post;
            case "PUT" -> MockMvcRequestBuilders::put;
            case "DELETE" -> MockMvcRequestBuilders::delete;
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };

        return requestBuilderFunction.apply(uri);
    }

    private URI buildUri(String endpoint, Map<String, String> pathParams, Map<String, String> queryParams) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(endpoint);

        if (!CollectionUtils.isEmpty(queryParams)) {
            queryParams.forEach(uriBuilder::queryParam);
        }

        UriComponents uriComponents = uriBuilder.build();
        if (!CollectionUtils.isEmpty(pathParams)) {
            uriComponents = uriComponents.expand(pathParams);
        }

        return uriComponents.toUri();
    }
}
