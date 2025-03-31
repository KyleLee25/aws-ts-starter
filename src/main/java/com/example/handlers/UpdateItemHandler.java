package com.example.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.models.Item;
import com.example.services.DynamoDBService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.utils.CorsHeaders;

public class UpdateItemHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DynamoDBService dbService;
    private final ObjectMapper mapper;

    public UpdateItemHandler() {
        this.dbService = new DynamoDBService();
        this.mapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            if (input.getPathParameters() == null || input.getBody() == null) {
                return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withHeaders(CorsHeaders.getCorsHeaders())
                    .withBody("{\"message\": \"Invalid request parameters or body\"}");
            }

            String id = input.getPathParameters().get("id");
            Item updateItemInput = mapper.readValue(input.getBody(), Item.class);
            Item updatedItem = dbService.updateItem(id, updateItemInput);

            if (updatedItem == null) {
                return new APIGatewayProxyResponseEvent()
                    .withStatusCode(404)
                    .withHeaders(CorsHeaders.getCorsHeaders())
                    .withBody("{\"message\": \"Item not found\"}");
            }

            return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withHeaders(CorsHeaders.getCorsHeaders())
                .withBody(mapper.writeValueAsString(updatedItem));

        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(500)
                .withHeaders(CorsHeaders.getCorsHeaders())
                .withBody("{\"message\": \"Internal server error\"}");
        }
    }
} 