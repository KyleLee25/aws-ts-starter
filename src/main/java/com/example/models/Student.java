package com.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class Student {
    private String id;
    private String name;
    private String email;
    private String major;
    private int enrollmentYear;
    private int graduationYear;
    private String createdAt;
    private String updatedAt;

    public Student() {}

    public Student(String id, String name, String email, String major, int enrollmentYear, int graduationYear) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.major = major;
        this.enrollmentYear = enrollmentYear;
        this.graduationYear = graduationYear;
        this.createdAt = Instant.now().toString();
        this.updatedAt = this.createdAt;
    }

    @JsonProperty("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("email")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @JsonProperty("major")
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    @JsonProperty("enrollmentYear")
    public int getEnrollmentYear() { return enrollmentYear; }
    public void setEnrollmentYear(int enrollmentYear) { this.enrollmentYear = enrollmentYear; }

    @JsonProperty("graduationYear")
    public int getGraduationYear() { return graduationYear; }
    public void setGraduationYear(int graduationYear) { this.graduationYear = graduationYear; }

    @JsonProperty("createdAt")
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @JsonProperty("updatedAt")
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}