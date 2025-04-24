package com.example.handlers.student;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.example.models.Student;
import com.example.services.DynamoDBService;
import com.example.utils.CorsHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;

public class CreateStudentHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DynamoDBService dbService;
    private final ObjectMapper mapper;

    public CreateStudentHandler() {
        this.dbService = new DynamoDBService();
        this.mapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Received request to create student");
        
        try {
            if (input.getBody() == null) {
                logger.log("WARN: Received null request body");
                return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withHeaders(CorsHeaders.getCorsHeaders())
                    .withBody("{\"message\": \"Invalid request body\"}");
            }

            logger.log("Request body: " + input.getBody());
            logger.log("Attempting to parse request body");
            
            Student student = mapper.readValue(input.getBody(), Student.class);
            logger.log("Parsed student object: " + student);
            
            String studentId = UUID.randomUUID().toString();
            student.setId(studentId);
            
            // Set timestamps
            String currentTime = Instant.now().toString();
            student.setCreatedAt(currentTime);
            student.setUpdatedAt(currentTime);
            
            logger.log("Creating student with ID: " + studentId);
            logger.log("Final student object: " + student);

            Student createdStudent = dbService.createStudent(student);
            if (createdStudent == null) {
                logger.log("ERROR: Failed to create student in database");
                return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(CorsHeaders.getCorsHeaders())
                    .withBody("{\"message\": \"Failed to create student\"}");
            }

            logger.log("Successfully created student: " + createdStudent.getId());
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(201)
                .withHeaders(CorsHeaders.getCorsHeaders())
                .withBody(mapper.writeValueAsString(createdStudent));

        } catch (Exception e) {
            logger.log("ERROR: Error creating student: " + e.getMessage());
            logger.log("Error type: " + e.getClass().getName());
            logger.log("Stack trace:");
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(500)
                .withHeaders(CorsHeaders.getCorsHeaders())
                .withBody("{\"message\": \"Internal server error\"}");
        }
    }
} 