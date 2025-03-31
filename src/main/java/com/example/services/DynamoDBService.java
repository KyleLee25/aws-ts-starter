package com.example.services;

import com.example.models.Item;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.Instant;

public class DynamoDBService {
    private final DynamoDbClient client;
    private final String tableName;
    private final ObjectMapper mapper;

    public DynamoDBService() {
        boolean isLocal = System.getenv("IS_OFFLINE") != null;
        
        if (isLocal) {
            client = DynamoDbClient.builder()
                .endpointOverride(java.net.URI.create("http://localhost:8000"))
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .build();
        } else {
            client = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        
        tableName = System.getenv("TABLE_NAME");
        mapper = new ObjectMapper();
    }

    public Item createItem(Item item) {
        try {
            Map<String, AttributeValue> itemValues = new HashMap<>();
            itemValues.put("id", AttributeValue.builder().s(item.getId()).build());
            itemValues.put("name", AttributeValue.builder().s(item.getName()).build());
            itemValues.put("price", AttributeValue.builder().n(String.valueOf(item.getPrice())).build());
            itemValues.put("description", AttributeValue.builder().s(item.getDescription()).build());
            itemValues.put("createdAt", AttributeValue.builder().s(Instant.now().toString()).build());
            itemValues.put("updatedAt", AttributeValue.builder().s(Instant.now().toString()).build());

            PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

            client.putItem(request);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Item getItem(String id) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("id", AttributeValue.builder().s(id).build());

            GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

            GetItemResponse response = client.getItem(request);
            
            if (!response.hasItem()) {
                return null;
            }

            Map<String, AttributeValue> itemMap = response.item();
            Item item = new Item();
            item.setId(itemMap.get("id").s());
            item.setName(itemMap.get("name").s());
            item.setPrice(Double.parseDouble(itemMap.get("price").n()));
            item.setDescription(itemMap.get("description").s());
            item.setCreatedAt(itemMap.get("createdAt").s());
            item.setUpdatedAt(itemMap.get("updatedAt").s());

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Item updateItem(String id, Item updateData) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("id", AttributeValue.builder().s(id).build());

            // Build update expression and attribute values
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            Map<String, String> expressionAttributeNames = new HashMap<>();
            StringBuilder updateExpression = new StringBuilder("SET ");
            
            if (updateData.getName() != null) {
                updateExpression.append("#n = :name, ");
                expressionAttributeValues.put(":name", AttributeValue.builder().s(updateData.getName()).build());
                expressionAttributeNames.put("#n", "name");
            }
            
            if (updateData.getPrice() > 0) {
                updateExpression.append("#p = :price, ");
                expressionAttributeValues.put(":price", AttributeValue.builder().n(String.valueOf(updateData.getPrice())).build());
                expressionAttributeNames.put("#p", "price");
            }
            
            if (updateData.getDescription() != null) {
                updateExpression.append("#d = :description, ");
                expressionAttributeValues.put(":description", AttributeValue.builder().s(updateData.getDescription()).build());
                expressionAttributeNames.put("#d", "description");
            }

            // Always update the updatedAt timestamp
            updateExpression.append("#ua = :updatedAt");
            expressionAttributeValues.put(":updatedAt", AttributeValue.builder().s(Instant.now().toString()).build());
            expressionAttributeNames.put("#ua", "updatedAt");

            UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .updateExpression(updateExpression.toString())
                .expressionAttributeValues(expressionAttributeValues)
                .expressionAttributeNames(expressionAttributeNames)
                .returnValues(ReturnValue.ALL_NEW)
                .build();

            UpdateItemResponse response = client.updateItem(request);
            
            // Convert response to Item object
            Map<String, AttributeValue> attributes = response.attributes();
            Item updatedItem = new Item();
            updatedItem.setId(attributes.get("id").s());
            updatedItem.setName(attributes.get("name").s());
            updatedItem.setPrice(Double.parseDouble(attributes.get("price").n()));
            updatedItem.setDescription(attributes.get("description").s());
            updatedItem.setCreatedAt(attributes.get("createdAt").s());
            updatedItem.setUpdatedAt(attributes.get("updatedAt").s());

            return updatedItem;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteItem(String id) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("id", AttributeValue.builder().s(id).build());

            DeleteItemRequest request = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

            DeleteItemResponse response = client.deleteItem(request);
            return response.sdkHttpResponse().isSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Item> listItems() {
        try {
            ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

            ScanResponse response = client.scan(scanRequest);
            List<Item> items = new ArrayList<>();

            for (Map<String, AttributeValue> itemMap : response.items()) {
                Item item = new Item();
                item.setId(itemMap.get("id").s());
                item.setName(itemMap.get("name").s());
                item.setPrice(Double.parseDouble(itemMap.get("price").n()));
                item.setDescription(itemMap.get("description").s());
                item.setCreatedAt(itemMap.get("createdAt").s());
                item.setUpdatedAt(itemMap.get("updatedAt").s());
                items.add(item);
            }

            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 