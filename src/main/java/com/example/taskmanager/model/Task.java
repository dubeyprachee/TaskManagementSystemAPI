package com.example.taskmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskId")
    private int taskId;

    @NotBlank(message = "Task title is required")
    @Column(name = "taskTitle")
    private String taskTitle;

    @Column(name = "taskDescription")
    private String taskDescription;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(name = "taskStatus")
    private Status taskStatus;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Priority is required")
    @Column(name = "taskPriority")
    private Priority taskPriority;

    @NotNull(message = "Due date is required")
    @Column(name = "taskDueDate")
    private LocalDate taskDueDate;


    @Column(name = "assignedTo")
    private Integer assignedTo;

    public Task() {
    }

    public Task(String name, String description, Status status, Priority priority, LocalDate dueDate) {
        this.taskTitle = name;
        this.taskDescription = description;
        this.taskStatus = status;
        this.taskPriority = priority;
        this.taskDueDate = dueDate;
    }
}
