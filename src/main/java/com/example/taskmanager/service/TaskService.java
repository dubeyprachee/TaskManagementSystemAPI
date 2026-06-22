package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<Task> getAllTasks() {
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
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Task assignTaskToUser(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setAssignedUser(user);
        return taskRepository.save(task);
    }
}
