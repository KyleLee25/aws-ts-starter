package com.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class Item {
    private String id;
    private String name;
    private double price;
    private String description;
    private String createdAt;
    private String updatedAt;

    public Item() {}

    public Item(String id, String name, double price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.createdAt = Instant.now().toString();
        this.updatedAt = this.createdAt;
    }

    // Getters and setters
    @JsonProperty("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("price")
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @JsonProperty("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @JsonProperty("createdAt")
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @JsonProperty("updatedAt")
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
} 