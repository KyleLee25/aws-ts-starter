package com.example.services;

import com.example.models.Item;
import com.example.models.Student;
import com.example.models.Course;
import com.example.models.Grade;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import java.net.URI;

public class DynamoDBService {
    private final DynamoDbClient client;
    private final String tableName;
    private final String studentsTable;
    private final String coursesTable;
    private final String gradesTable;
    private final ObjectMapper mapper;

    public DynamoDBService(DynamoDbClient dynamoDbClient) {
        this.client = dynamoDbClient;
        this.tableName = System.getProperty("TABLE_NAME", "aws-java-starter-dev");
        this.studentsTable = System.getProperty("STUDENTS_TABLE", "aws-java-starter-students-dev");
        this.coursesTable = System.getProperty("COURSES_TABLE", "aws-java-starter-courses-dev");
        this.gradesTable = System.getProperty("GRADES_TABLE", "aws-java-starter-grades-dev");
        this.mapper = new ObjectMapper();
    }

    public DynamoDBService() {
        boolean isOffline = Boolean.parseBoolean(System.getProperty("IS_OFFLINE", "false"));
        
        if (isOffline) {
            // Use DynamoDB Local
            this.client = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("dummy", "dummy")))
                .region(Region.US_EAST_1)
                .build();
        } else {
            // Use AWS DynamoDB
            this.client = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        
        this.tableName = System.getProperty("TABLE_NAME", "aws-java-starter-dev");
        this.studentsTable = System.getProperty("STUDENTS_TABLE", "aws-java-starter-students-prod");
        this.coursesTable = System.getProperty("COURSES_TABLE", "aws-java-starter-courses-prod");
        this.gradesTable = System.getProperty("GRADES_TABLE", "aws-java-starter-grades-prod");
        this.mapper = new ObjectMapper();
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

    // Student Methods
    public Student createStudent(Student student) {
        try {
            System.out.println("Creating student with data: " + student);
            System.out.println("Using table name: " + studentsTable);
            
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.builder().s(student.getId()).build());
            item.put("name", AttributeValue.builder().s(student.getName()).build());
            item.put("email", AttributeValue.builder().s(student.getEmail()).build());
            item.put("major", AttributeValue.builder().s(student.getMajor()).build());
            item.put("enrollmentYear", AttributeValue.builder().n(String.valueOf(student.getEnrollmentYear())).build());
            item.put("graduationYear", AttributeValue.builder().n(String.valueOf(student.getGraduationYear())).build());
            item.put("createdAt", AttributeValue.builder().s(student.getCreatedAt()).build());
            item.put("updatedAt", AttributeValue.builder().s(student.getUpdatedAt()).build());

            System.out.println("Prepared DynamoDB item: " + item);

            PutItemRequest request = PutItemRequest.builder()
                .tableName(studentsTable)
                .item(item)
                .build();

            System.out.println("Sending PutItemRequest to DynamoDB");
            PutItemResponse response = client.putItem(request);
            System.out.println("DynamoDB response: " + response);
            
            return student;
        } catch (Exception e) {
            System.err.println("Error creating student in DynamoDB: " + e.getMessage());
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Stack trace:");
            e.printStackTrace();
            return null;
        }
    }

    public Student getStudent(String id) {
        try {
            GetItemRequest request = GetItemRequest.builder()
                .tableName(studentsTable)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build();

            GetItemResponse response = client.getItem(request);
            if (!response.hasItem()) {
                return null;
            }

            Map<String, AttributeValue> item = response.item();
            Student student = new Student();
            student.setId(item.get("id").s());
            student.setName(item.get("name").s());
            student.setEmail(item.get("email").s());
            student.setMajor(item.get("major").s());
            student.setEnrollmentYear(Integer.parseInt(item.get("enrollmentYear").n()));
            student.setGraduationYear(Integer.parseInt(item.get("graduationYear").n()));
            student.setCreatedAt(item.get("createdAt").s());
            student.setUpdatedAt(item.get("updatedAt").s());

            return student;
        } catch (Exception e) {
            System.err.println("Error getting student: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Student> listStudents() {
        try {
            ScanRequest request = ScanRequest.builder()
                .tableName(studentsTable)
                .build();

            ScanResponse response = client.scan(request);
            List<Student> students = new ArrayList<>();

            for (Map<String, AttributeValue> item : response.items()) {
                Student student = new Student();
                student.setId(item.get("id").s());
                student.setName(item.get("name").s());
                student.setEmail(item.get("email").s());
                student.setMajor(item.get("major").s());
                student.setEnrollmentYear(Integer.parseInt(item.get("enrollmentYear").n()));
                student.setGraduationYear(Integer.parseInt(item.get("graduationYear").n()));
                student.setCreatedAt(item.get("createdAt").s());
                student.setUpdatedAt(item.get("updatedAt").s());
                students.add(student);
            }

            return students;
        } catch (Exception e) {
            System.err.println("Error listing students: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Course Methods
    public Course createCourse(Course course) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.builder().s(course.getId()).build());
            item.put("code", AttributeValue.builder().s(course.getCode()).build());
            item.put("name", AttributeValue.builder().s(course.getName()).build());
            item.put("description", AttributeValue.builder().s(course.getDescription()).build());
            item.put("credits", AttributeValue.builder().n(String.valueOf(course.getCredits())).build());
            item.put("instructor", AttributeValue.builder().s(course.getInstructor()).build());
            item.put("semester", AttributeValue.builder().s(course.getSemester()).build());
            item.put("createdAt", AttributeValue.builder().s(course.getCreatedAt()).build());
            item.put("updatedAt", AttributeValue.builder().s(course.getUpdatedAt()).build());

            PutItemRequest request = PutItemRequest.builder()
                .tableName(coursesTable)
                .item(item)
                .build();

            client.putItem(request);
            return course;
        } catch (Exception e) {
            System.err.println("Error creating course: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Course getCourse(String id) {
        try {
            GetItemRequest request = GetItemRequest.builder()
                .tableName(coursesTable)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build();

            GetItemResponse response = client.getItem(request);
            if (!response.hasItem()) {
                return null;
            }

            Map<String, AttributeValue> item = response.item();
            Course course = new Course();
            course.setId(item.get("id").s());
            course.setCode(item.get("code").s());
            course.setName(item.get("name").s());
            course.setDescription(item.get("description").s());
            course.setCredits(Integer.parseInt(item.get("credits").n()));
            course.setInstructor(item.get("instructor").s());
            course.setSemester(item.get("semester").s());
            course.setCreatedAt(item.get("createdAt").s());
            course.setUpdatedAt(item.get("updatedAt").s());

            return course;
        } catch (Exception e) {
            System.err.println("Error getting course: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Course> listCourses() {
        try {
            ScanRequest request = ScanRequest.builder()
                .tableName(coursesTable)
                .build();

            ScanResponse response = client.scan(request);
            List<Course> courses = new ArrayList<>();

            for (Map<String, AttributeValue> item : response.items()) {
                Course course = new Course();
                course.setId(item.get("id").s());
                course.setCode(item.get("code").s());
                course.setName(item.get("name").s());
                course.setDescription(item.get("description").s());
                course.setCredits(Integer.parseInt(item.get("credits").n()));
                course.setInstructor(item.get("instructor").s());
                course.setSemester(item.get("semester").s());
                course.setCreatedAt(item.get("createdAt").s());
                course.setUpdatedAt(item.get("updatedAt").s());
                courses.add(course);
            }

            return courses;
        } catch (Exception e) {
            System.err.println("Error listing courses: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Grade Methods
    public Grade createGrade(Grade grade) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.builder().s(grade.getId()).build());
            item.put("studentId", AttributeValue.builder().s(grade.getStudentId()).build());
            item.put("courseId", AttributeValue.builder().s(grade.getCourseId()).build());
            item.put("grade", AttributeValue.builder().s(grade.getGrade()).build());
            item.put("semester", AttributeValue.builder().s(grade.getSemester()).build());
            item.put("createdAt", AttributeValue.builder().s(grade.getCreatedAt()).build());
            item.put("updatedAt", AttributeValue.builder().s(grade.getUpdatedAt()).build());

            PutItemRequest request = PutItemRequest.builder()
                .tableName(gradesTable)
                .item(item)
                .build();

            client.putItem(request);
            return grade;
        } catch (Exception e) {
            System.err.println("Error creating grade: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Grade> getStudentGrades(String studentId) {
        try {
            Map<String, String> expressionAttributeNames = new HashMap<>();
            expressionAttributeNames.put("#studentId", "studentId");

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":studentId", AttributeValue.builder().s(studentId).build());

            QueryRequest request = QueryRequest.builder()
                .tableName(gradesTable)
                .indexName("studentId-index")
                .keyConditionExpression("#studentId = :studentId")
                .expressionAttributeNames(expressionAttributeNames)
                .expressionAttributeValues(expressionAttributeValues)
                .build();

            QueryResponse response = client.query(request);
            List<Grade> grades = new ArrayList<>();

            for (Map<String, AttributeValue> item : response.items()) {
                Grade grade = new Grade();
                grade.setId(item.get("id").s());
                grade.setStudentId(item.get("studentId").s());
                grade.setCourseId(item.get("courseId").s());
                grade.setGrade(item.get("grade").s());
                grade.setSemester(item.get("semester").s());
                grade.setCreatedAt(item.get("createdAt").s());
                grade.setUpdatedAt(item.get("updatedAt").s());
                grades.add(grade);
            }

            return grades;
        } catch (Exception e) {
            System.err.println("Error getting student grades: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Grade> getCourseGrades(String courseId) {
        try {
            Map<String, String> expressionAttributeNames = new HashMap<>();
            expressionAttributeNames.put("#courseId", "courseId");

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":courseId", AttributeValue.builder().s(courseId).build());

            QueryRequest request = QueryRequest.builder()
                .tableName(gradesTable)
                .indexName("courseId-index")
                .keyConditionExpression("#courseId = :courseId")
                .expressionAttributeNames(expressionAttributeNames)
                .expressionAttributeValues(expressionAttributeValues)
                .build();

            QueryResponse response = client.query(request);
            List<Grade> grades = new ArrayList<>();

            for (Map<String, AttributeValue> item : response.items()) {
                Grade grade = new Grade();
                grade.setId(item.get("id").s());
                grade.setStudentId(item.get("studentId").s());
                grade.setCourseId(item.get("courseId").s());
                grade.setGrade(item.get("grade").s());
                grade.setSemester(item.get("semester").s());
                grade.setCreatedAt(item.get("createdAt").s());
                grade.setUpdatedAt(item.get("updatedAt").s());
                grades.add(grade);
            }

            return grades;
        } catch (Exception e) {
            System.err.println("Error getting course grades: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
} 