package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
            return taskRepository.findByPriorityAndStatusAndDueDate(priority, status, dueDate);
        } else if (priority != null && status != null) {
            return taskRepository.findByPriorityAndStatus(priority, status);
        } else if (priority != null && dueDate != null) {
            return taskRepository.findByPriorityAndDueDate(priority, dueDate);
        } else if (status != null && dueDate != null) {
            return taskRepository.findByStatusAndDueDate(status, dueDate);
        } else if (priority != null) {
            return taskRepository.findByPriority(priority);
        } else if (status != null) {
            return taskRepository.findByStatus(status);
        } else if (dueDate != null) {
            return taskRepository.findByDueDate(dueDate);
        } else {
            return taskRepository.findAll();
        }
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        logger.info("Creating new task: {}", task.getName());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        logger.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
    }

    public Task assignTaskToUser(Long taskId, Long userId) {
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
        task.setAssignedUser(user);
        return taskRepository.save(task);
    }
}
