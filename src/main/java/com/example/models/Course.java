package com.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class Course {
    private String id;
    private String code;
    private String name;
    private String description;
    private int credits;
    private String instructor;
    private String semester;
    private String createdAt;
    private String updatedAt;

    public Course() {}

    public Course(String id, String code, String name, String description, int credits, String instructor, String semester) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.credits = credits;
        this.instructor = instructor;
        this.semester = semester;
        this.createdAt = Instant.now().toString();
        this.updatedAt = this.createdAt;
    }

    @JsonProperty("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @JsonProperty("code")
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @JsonProperty("credits")
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    @JsonProperty("instructor")
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    @JsonProperty("semester")
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    @JsonProperty("createdAt")
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @JsonProperty("updatedAt")
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
} 