package com.example.taskmanager.service;

import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<Task> getAllTasks() {
        logger.debug("Fetching all tasks");
        return taskRepository.findAll();
    }

    public List<Task> getFilteredTasks(Priority priority, Status status, LocalDate dueDate) {
        if (priority != null && status != null && dueDate != null) {
            return taskRepository.findByTaskPriorityAndTaskStatusAndTaskDueDate(priority, status, dueDate);
        } else if (priority != null && status != null) {
            return taskRepository.findByTaskPriorityAndTaskStatus(priority, status);
        } else if (priority != null && dueDate != null) {
            return taskRepository.findByTaskPriorityAndTaskDueDate(priority, dueDate);
        } else if (status != null && dueDate != null) {
            return taskRepository.findByTaskStatusAndTaskDueDate(status, dueDate);
        } else if (priority != null) {
            return taskRepository.findByTaskPriority(priority);
        } else if (status != null) {
            return taskRepository.findByTaskStatus(status);
        } else if (dueDate != null) {
            return taskRepository.findByTaskDueDate(dueDate);
        } else {
            return taskRepository.findAll();
        }
    }

    public Optional<Task> getTaskById(Integer id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        logger.info("Creating new task: {}", task.getTaskTitle());
        return taskRepository.save(task);
    }

    public void deleteTask(Integer id) {
        logger.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
    }

    public Task assignTaskToUser(Integer taskId, Integer userId) {
        logger.info("Assigning task {} to user {}", taskId, userId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    logger.error("Task not found: {}", taskId);
                    return new RuntimeException("Task not found");
                });
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", userId);
                    return new RuntimeException("User not found");
                });
        task.setAssignedTo(userId);
        return taskRepository.save(task);
    }

    public TaskSummary getTasksSummary() {

//        List<String> month = List.of("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

        List<TaskStatusMonthProjection> summary = taskRepository.getTasksSummary();

        List<String> months = new ArrayList<>();
        List<Integer> completedTasks = new ArrayList<>();
        List<Integer> pendingTasks = new ArrayList<>();

        for(Month month : Month.values())
        {
            String monthName = month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
            months.add(monthName);

            Long completedCount = summary.stream()
                    .filter(projection -> projection.getTaskMonth() != null)
                    .filter(projection -> projection.getTaskMonth().equals(monthName))
                    .filter(task -> Status.valueOf(task.getTaskStatus()).equals(Status.COMPLETED))
                    .count();

            Long pendingCount = summary.stream()
                    .filter(projection -> projection.getTaskMonth() != null)
                    .filter(projection -> projection.getTaskMonth().equals(monthName))
                    .filter(task -> Status.valueOf(task.getTaskStatus()).equals(Status.PENDING))
                    .count();

            completedTasks.add(completedCount.intValue());
            pendingTasks.add(pendingCount.intValue());
        }

        TaskSummary taskSummary = new TaskSummary();
        taskSummary.setMonths(months);
        taskSummary.setCompledtedCounts(completedTasks);
        taskSummary.setPendingCounts(pendingTasks);

        logger.info("Task summary generated successfully {}" , taskSummary);

        return taskSummary;
    }
}
