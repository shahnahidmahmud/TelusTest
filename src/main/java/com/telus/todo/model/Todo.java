package com.telus.todo.model;

import jakarta.persistence.*;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;

    @Enumerated(EnumType.STRING)
    private CompletionStatus completionStatus;

    public Todo() {}

    public Todo(Long id, String description, CompletionStatus completionStatus) {
        this.id = id;
        this.description = description;
        this.completionStatus = completionStatus;
    }

    // Standard getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CompletionStatus getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(CompletionStatus completionStatus) {
        this.completionStatus = completionStatus;
    }
}

