package com.example.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.models.Item;
import com.example.services.DynamoDBService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import com.example.utils.CorsHeaders;
import java.util.ArrayList;

public class ListItemsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DynamoDBService dbService;
    private final ObjectMapper mapper;

    public ListItemsHandler() {
        this.dbService = new DynamoDBService();
        this.mapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            List<Item> items = dbService.listItems();

            if (items == null) {
                items = new ArrayList<>();
            }

            return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withHeaders(CorsHeaders.getCorsHeaders())
                .withBody(mapper.writeValueAsString(items));

        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(500)
                .withHeaders(CorsHeaders.getCorsHeaders())
                .withBody("{\"message\": \"Internal server error\"}");
        }
    }
} 