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
//    @Column("task_id")
    private Long taskId;

    @NotBlank(message = "Task name is required")
//    @Column("task_title")
    private String taskTitle;

//    @Column("task_description")
    private String taskDescription;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
//    @Column("task_status")
    private Status taskStatus;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Priority is required")
//    @Column("task_priority")
    private Priority taskPriority;

    @NotNull(message = "Due date is required")
//    @Column("task_due_date")
    private LocalDate taskDueDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignedUser;

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
