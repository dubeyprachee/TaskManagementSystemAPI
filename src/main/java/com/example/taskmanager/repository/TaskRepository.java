package com.example.taskmanager.repository;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByPriority(Priority priority);
    List<Task> findByStatus(Status status);
    List<Task> findByDueDate(LocalDate dueDate);
    List<Task> findByPriorityAndStatusAndDueDate(Priority priority, Status status, LocalDate dueDate);
    List<Task> findByPriorityAndStatus(Priority priority, Status status);
    List<Task> findByPriorityAndDueDate(Priority priority, LocalDate dueDate);
    List<Task> findByStatusAndDueDate(Status status, LocalDate dueDate);
}
