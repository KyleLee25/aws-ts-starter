// package com.example.handlers;

// import com.amazonaws.services.lambda.runtime.Context;
// import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
// import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
// import com.example.handlers.course.CreateCourseHandler;
// import com.example.handlers.course.GetCourseHandler;
// import com.example.handlers.course.ListCoursesHandler;
// import com.example.handlers.grade.CreateGradeHandler;
// import com.example.handlers.grade.GetCourseGradesHandler;
// import com.example.handlers.grade.GetStudentGradesHandler;
// import com.example.handlers.student.CreateStudentHandler;
// import com.example.handlers.student.GetStudentHandler;
// import com.example.handlers.student.ListStudentsHandler;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.*;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
// import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
// import software.amazon.awssdk.regions.Region;
// import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
// import software.amazon.awssdk.services.dynamodb.model.*;

// import java.net.URI;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Map;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// public class HandlerTest {
//     @Mock
//     private Context context;
    
//     @Mock
//     private DynamoDbClient dynamoDbClient;

//     private final ObjectMapper mapper = new ObjectMapper();
//     private String studentId;
//     private String courseId;
//     private DynamoDBService dbService;

//     @BeforeAll
//     public static void setupAll() {
//         // Set environment variables for local testing
//         System.setProperty("IS_OFFLINE", "true");
//         System.setProperty("TABLE_NAME", "aws-java-starter-dev");
//         System.setProperty("STUDENTS_TABLE", "aws-java-starter-students-dev");
//         System.setProperty("COURSES_TABLE", "aws-java-starter-courses-dev");
//         System.setProperty("GRADES_TABLE", "aws-java-starter-grades-dev");
//     }

//     @BeforeEach
//     public void setup() {
//         MockitoAnnotations.openMocks(this);
//         context = mock(Context.class);
        
//         // Mock successful responses for DynamoDB operations
//         // PutItem
//         when(dynamoDbClient.putItem(any(PutItemRequest.class)))
//             .thenReturn(PutItemResponse.builder().build());

//         // GetItem
//         when(dynamoDbClient.getItem(any(GetItemRequest.class)))
//             .thenAnswer(invocation -> {
//                 GetItemRequest request = invocation.getArgument(0);
//                 String id = request.key().get("id").s();
                
//                 if (id.equals("invalid-id")) {
//                     return GetItemResponse.builder().build(); // Return empty response for invalid ID
//                 }
                
//                 Map<String, AttributeValue> item = new HashMap<>();
//                 item.put("id", AttributeValue.builder().s(id).build());
//                 item.put("name", AttributeValue.builder().s("Test Name").build());
//                 item.put("email", AttributeValue.builder().s("test@example.com").build());
//                 item.put("major", AttributeValue.builder().s("Computer Science").build());
//                 item.put("enrollmentYear", AttributeValue.builder().n("2024").build());
//                 item.put("createdAt", AttributeValue.builder().s("2024-01-01").build());
//                 item.put("updatedAt", AttributeValue.builder().s("2024-01-01").build());
                
//                 return GetItemResponse.builder().item(item).build();
//             });

//         // Scan
//         when(dynamoDbClient.scan(any(ScanRequest.class)))
//             .thenAnswer(invocation -> {
//                 ArrayList<Map<String, AttributeValue>> items = new ArrayList<>();
//                 Map<String, AttributeValue> item = new HashMap<>();
//                 item.put("id", AttributeValue.builder().s("test-id").build());
//                 item.put("name", AttributeValue.builder().s("Test Name").build());
//                 items.add(item);
//                 return ScanResponse.builder().items(items).build();
//             });

//         // Query
//         when(dynamoDbClient.query(any(QueryRequest.class)))
//             .thenAnswer(invocation -> {
//                 ArrayList<Map<String, AttributeValue>> items = new ArrayList<>();
//                 Map<String, AttributeValue> item = new HashMap<>();
//                 item.put("id", AttributeValue.builder().s("test-id").build());
//                 item.put("grade", AttributeValue.builder().s("A").build());
//                 items.add(item);
//                 return QueryResponse.builder().items(items).build();
//             });

//         // Create a DynamoDBService with the mock client
//         dbService = new DynamoDBService(dynamoDbClient);
//     }

//     @Test
//     public void testStudentFlow() throws Exception {
//         // Create student
//         CreateStudentHandler createStudentHandler = new CreateStudentHandler();
//         APIGatewayProxyRequestEvent createStudentRequest = new APIGatewayProxyRequestEvent();
//         String studentJson = "{\"name\":\"Test Student\",\"email\":\"test@example.com\",\"major\":\"Computer Science\",\"enrollmentYear\":2024}";
//         createStudentRequest.setBody(studentJson);
        
//         APIGatewayProxyResponseEvent createStudentResponse = createStudentHandler.handleRequest(createStudentRequest, context);
//         System.out.println("Create Student Response: " + createStudentResponse.getBody());
//         System.out.println("Create Student Status Code: " + createStudentResponse.getStatusCode());
//         assertEquals(200, createStudentResponse.getStatusCode());
//         assertNotNull(createStudentResponse.getBody());
//         studentId = mapper.readTree(createStudentResponse.getBody()).get("id").asText();

//         // Verify PutItem was called
//         verify(dynamoDbClient, times(1)).putItem(any(PutItemRequest.class));

//         // Get student
//         GetStudentHandler getStudentHandler = new GetStudentHandler();
//         APIGatewayProxyRequestEvent getStudentRequest = new APIGatewayProxyRequestEvent();
//         Map<String, String> studentPathParams = new HashMap<>();
//         studentPathParams.put("id", studentId);
//         getStudentRequest.setPathParameters(studentPathParams);

//         APIGatewayProxyResponseEvent getStudentResponse = getStudentHandler.handleRequest(getStudentRequest, context);
//         System.out.println("Get Student Response: " + getStudentResponse.getBody());
//         System.out.println("Get Student Status Code: " + getStudentResponse.getStatusCode());
//         assertEquals(200, getStudentResponse.getStatusCode());
//         assertNotNull(getStudentResponse.getBody());

//         // Test invalid student ID
//         studentPathParams.put("id", "invalid-id");
//         APIGatewayProxyResponseEvent invalidStudentResponse = getStudentHandler.handleRequest(getStudentRequest, context);
//         System.out.println("Invalid Student Response: " + invalidStudentResponse.getBody());
//         System.out.println("Invalid Student Status Code: " + invalidStudentResponse.getStatusCode());
//         assertEquals(404, invalidStudentResponse.getStatusCode());

//         // List students
//         ListStudentsHandler listStudentsHandler = new ListStudentsHandler();
//         APIGatewayProxyResponseEvent listStudentsResponse = listStudentsHandler.handleRequest(new APIGatewayProxyRequestEvent(), context);
//         System.out.println("List Students Response: " + listStudentsResponse.getBody());
//         System.out.println("List Students Status Code: " + listStudentsResponse.getStatusCode());
//         assertEquals(200, listStudentsResponse.getStatusCode());
//         assertNotNull(listStudentsResponse.getBody());
//     }

//     @Test
//     public void testCourseFlow() throws Exception {
//         // Create course
//         CreateCourseHandler createCourseHandler = new CreateCourseHandler();
//         APIGatewayProxyRequestEvent createCourseRequest = new APIGatewayProxyRequestEvent();
//         String courseJson = "{\"code\":\"CS101\",\"name\":\"Test Course\",\"description\":\"Test Description\",\"credits\":3,\"instructor\":\"Test Instructor\",\"semester\":\"Spring 2024\"}";
//         createCourseRequest.setBody(courseJson);
        
//         APIGatewayProxyResponseEvent createCourseResponse = createCourseHandler.handleRequest(createCourseRequest, context);
//         System.out.println("Create Course Response: " + createCourseResponse.getBody());
//         System.out.println("Create Course Status Code: " + createCourseResponse.getStatusCode());
//         assertEquals(200, createCourseResponse.getStatusCode());
//         assertNotNull(createCourseResponse.getBody());
//         courseId = mapper.readTree(createCourseResponse.getBody()).get("id").asText();

//         // Get course
//         GetCourseHandler getCourseHandler = new GetCourseHandler();
//         APIGatewayProxyRequestEvent getCourseRequest = new APIGatewayProxyRequestEvent();
//         Map<String, String> coursePathParams = new HashMap<>();
//         coursePathParams.put("id", courseId);
//         getCourseRequest.setPathParameters(coursePathParams);

//         APIGatewayProxyResponseEvent getCourseResponse = getCourseHandler.handleRequest(getCourseRequest, context);
//         System.out.println("Get Course Response: " + getCourseResponse.getBody());
//         System.out.println("Get Course Status Code: " + getCourseResponse.getStatusCode());
//         assertEquals(200, getCourseResponse.getStatusCode());
//         assertNotNull(getCourseResponse.getBody());

//         // Test invalid course ID
//         coursePathParams.put("id", "invalid-id");
//         APIGatewayProxyResponseEvent invalidCourseResponse = getCourseHandler.handleRequest(getCourseRequest, context);
//         System.out.println("Invalid Course Response: " + invalidCourseResponse.getBody());
//         System.out.println("Invalid Course Status Code: " + invalidCourseResponse.getStatusCode());
//         assertEquals(404, invalidCourseResponse.getStatusCode());

//         // List courses
//         ListCoursesHandler listCoursesHandler = new ListCoursesHandler();
//         APIGatewayProxyResponseEvent listCoursesResponse = listCoursesHandler.handleRequest(new APIGatewayProxyRequestEvent(), context);
//         System.out.println("List Courses Response: " + listCoursesResponse.getBody());
//         System.out.println("List Courses Status Code: " + listCoursesResponse.getStatusCode());
//         assertEquals(200, listCoursesResponse.getStatusCode());
//         assertNotNull(listCoursesResponse.getBody());
//     }

//     @Test
//     public void testGradeFlow() throws Exception {
//         // First create student and course
//         testStudentFlow();
//         testCourseFlow();

//         // Create grade
//         CreateGradeHandler createGradeHandler = new CreateGradeHandler();
//         APIGatewayProxyRequestEvent createGradeRequest = new APIGatewayProxyRequestEvent();
//         String gradeJson = String.format("{\"studentId\":\"%s\",\"courseId\":\"%s\",\"grade\":\"A\",\"semester\":\"Spring 2024\"}", 
//             studentId, courseId);
//         createGradeRequest.setBody(gradeJson);
        
//         APIGatewayProxyResponseEvent createGradeResponse = createGradeHandler.handleRequest(createGradeRequest, context);
//         System.out.println("Create Grade Response: " + createGradeResponse.getBody());
//         System.out.println("Create Grade Status Code: " + createGradeResponse.getStatusCode());
//         assertEquals(200, createGradeResponse.getStatusCode());
//         assertNotNull(createGradeResponse.getBody());

//         // Get student grades
//         GetStudentGradesHandler getStudentGradesHandler = new GetStudentGradesHandler();
//         APIGatewayProxyRequestEvent getStudentGradesRequest = new APIGatewayProxyRequestEvent();
//         Map<String, String> studentGradesPathParams = new HashMap<>();
//         studentGradesPathParams.put("studentId", studentId);
//         getStudentGradesRequest.setPathParameters(studentGradesPathParams);

//         APIGatewayProxyResponseEvent getStudentGradesResponse = getStudentGradesHandler.handleRequest(getStudentGradesRequest, context);
//         System.out.println("Get Student Grades Response: " + getStudentGradesResponse.getBody());
//         System.out.println("Get Student Grades Status Code: " + getStudentGradesResponse.getStatusCode());
//         assertEquals(200, getStudentGradesResponse.getStatusCode());
//         assertNotNull(getStudentGradesResponse.getBody());

//         // Test invalid student ID for grades
//         studentGradesPathParams.put("studentId", "invalid-id");
//         APIGatewayProxyResponseEvent invalidStudentGradesResponse = getStudentGradesHandler.handleRequest(getStudentGradesRequest, context);
//         System.out.println("Invalid Student Grades Response: " + invalidStudentGradesResponse.getBody());
//         System.out.println("Invalid Student Grades Status Code: " + invalidStudentGradesResponse.getStatusCode());
//         assertEquals(200, invalidStudentGradesResponse.getStatusCode()); // Should return empty list

//         // Get course grades
//         GetCourseGradesHandler getCourseGradesHandler = new GetCourseGradesHandler();
//         APIGatewayProxyRequestEvent getCourseGradesRequest = new APIGatewayProxyRequestEvent();
//         Map<String, String> courseGradesPathParams = new HashMap<>();
//         courseGradesPathParams.put("courseId", courseId);
//         getCourseGradesRequest.setPathParameters(courseGradesPathParams);

//         APIGatewayProxyResponseEvent getCourseGradesResponse = getCourseGradesHandler.handleRequest(getCourseGradesRequest, context);
//         System.out.println("Get Course Grades Response: " + getCourseGradesResponse.getBody());
//         System.out.println("Get Course Grades Status Code: " + getCourseGradesResponse.getStatusCode());
//         assertEquals(200, getCourseGradesResponse.getStatusCode());
//         assertNotNull(getCourseGradesResponse.getBody());

//         // Test invalid course ID for grades
//         courseGradesPathParams.put("courseId", "invalid-id");
//         APIGatewayProxyResponseEvent invalidCourseGradesResponse = getCourseGradesHandler.handleRequest(getCourseGradesRequest, context);
//         System.out.println("Invalid Course Grades Response: " + invalidCourseGradesResponse.getBody());
//         System.out.println("Invalid Course Grades Status Code: " + invalidCourseGradesResponse.getStatusCode());
//         assertEquals(200, invalidCourseGradesResponse.getStatusCode()); // Should return empty list
//     }

//     @Test
//     public void whenCreateItemWithValidData_thenReturns201() {
//         // Given
//         CreateItemHandler handler = new CreateItemHandler(dbService);
//         APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
//         String itemJson = "{\"name\":\"Test Item\",\"price\":10.99,\"description\":\"Test Description\"}";
//         request.setBody(itemJson);

//         // When
//         APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

//         // Then
//         assertEquals(201, response.getStatusCode());
//         assertNotNull(response.getBody());
//         verify(dynamoDbClient, times(1)).putItem(any(PutItemRequest.class));
//     }

//     @Test
//     public void whenCreateItemWithInvalidData_thenReturns400() {
//         // Given
//         CreateItemHandler handler = new CreateItemHandler(dbService);
//         APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
//         request.setBody(null);

//         // When
//         APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

//         // Then
//         assertEquals(400, response.getStatusCode());
//         verify(dynamoDbClient, never()).putItem(any(PutItemRequest.class));
//     }
// }
