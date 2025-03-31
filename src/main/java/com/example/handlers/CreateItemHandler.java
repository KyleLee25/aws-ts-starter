package com.example.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.models.Item;
import com.example.services.DynamoDBService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.Map;
import com.example.utils.CorsHeaders;

public class CreateItemHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DynamoDBService dbService;
    private final ObjectMapper mapper;

    public CreateItemHandler() {
        this.dbService = new DynamoDBService();
        this.mapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            if (input.getBody() == null) {
                return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withHeaders(CorsHeaders.getCorsHeaders())
                    .withBody("{\"message\": \"Invalid request body\"}");
            }

            Item item = mapper.readValue(input.getBody(), Item.class);
            item.setId(UUID.randomUUID().toString());

            Item createdItem = dbService.createItem(item);
            if (createdItem == null) {
                return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(CorsHeaders.getCorsHeaders())
                    .withBody("{\"message\": \"Failed to create item\"}");
            }

            return new APIGatewayProxyResponseEvent()
                .withStatusCode(201)
                .withHeaders(CorsHeaders.getCorsHeaders())
                .withBody(mapper.writeValueAsString(createdItem));

        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(500)
                .withHeaders(CorsHeaders.getCorsHeaders())
                .withBody("{\"message\": \"Internal server error\"}");
        }
    }
} 