package com.example.taskmanager.repository;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatusMonthProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByTaskPriority(Priority priority);
    List<Task> findByTaskStatus(Status status);
    List<Task> findByTaskDueDate(LocalDate dueDate);
    List<Task> findByTaskPriorityAndTaskStatusAndTaskDueDate(Priority priority, Status status, LocalDate dueDate);
    List<Task> findByTaskPriorityAndTaskStatus(Priority priority, Status status);
    List<Task> findByTaskPriorityAndTaskDueDate(Priority priority, LocalDate dueDate);
    List<Task> findByTaskStatusAndTaskDueDate(Status status, LocalDate dueDate);

    @Query(value = "SELECT count(*) as count , DATE_FORMAT(taskDueDate,'%M') AS taskMonth, taskStatus FROM tasks t group by DATE_FORMAT(taskDueDate,'%M') , taskStatus order by taskMonth, taskStatus", nativeQuery = true)
    List<TaskStatusMonthProjection> getTasksSummary();
}
