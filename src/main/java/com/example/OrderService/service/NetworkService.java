package com.example.OrderService.service;

import com.example.OrderService.controller.dto.cart.MenuItemInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NetworkService {

    @Value("${services.restaurant-service.url}")
    private String restaurantServiceUrl;
    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    private final RestTemplate restTemplate;

    public NetworkService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public MenuItemInfoResponse getInfo(long itemId) {
        String getItemUrl = restaurantServiceUrl + "/menu/" + itemId;
        ResponseEntity<MenuItemInfoResponse> checkResponse = restTemplate.getForEntity(getItemUrl, MenuItemInfoResponse.class);
        if (checkResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("failed to get item with id " + itemId);
        }
        return checkResponse.getBody();
    }

    public long getIdFromToken(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);

        String parseIdUrl = authServiceUrl + "/auth/parse_id";
        ResponseEntity<Long> parseIdResponse = restTemplate.postForEntity(parseIdUrl, entity, Long.class);
        if (parseIdResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Check jwt failed");
        }
        return parseIdResponse.getBody();
    }

    public String getUserRoleByToken(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);

        String parseIdUrl = authServiceUrl + "/auth/parse_role";
        ResponseEntity<String> parseIdResponse = restTemplate.postForEntity(parseIdUrl, entity, String.class);
        if (parseIdResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Check jwt failed");
        }

        return parseIdResponse.getBody();
    }

    public String getUserRoleById(long userId) {
        String parseIdUrl = authServiceUrl + "/auth/parse_role/" + userId;
        ResponseEntity<String> parseIdResponse = restTemplate.postForEntity(parseIdUrl, null, String.class);
        if (parseIdResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Check jwt failed");
        }
        return parseIdResponse.getBody();
    }
}
