package com.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class Grade {
    private String id;
    private String studentId;
    private String courseId;
    private String grade;
    private String semester;
    private String createdAt;
    private String updatedAt;

    public Grade() {}

    public Grade(String id, String studentId, String courseId, String grade, String semester) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = grade;
        this.semester = semester;
        this.createdAt = Instant.now().toString();
        this.updatedAt = this.createdAt;
    }

    @JsonProperty("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @JsonProperty("studentId")
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    @JsonProperty("courseId")
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    @JsonProperty("grade")
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

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