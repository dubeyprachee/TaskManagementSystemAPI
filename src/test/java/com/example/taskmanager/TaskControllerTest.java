package com.example.taskmanager;

import com.example.taskmanager.config.JwtUtils;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        com.example.taskmanager.model.User userEntity = new com.example.taskmanager.model.User("user", "password", "ROLE_USER");
        userRepository.save(userEntity);
        UserDetails user = new org.springframework.security.core.userdetails.User("user", "password", Collections.singletonList(() -> "ROLE_USER"));
        userToken = "Bearer " + jwtUtils.generateToken(user);

        com.example.taskmanager.model.User adminEntity = new com.example.taskmanager.model.User("admin", "password", "ROLE_ADMIN");
        userRepository.save(adminEntity);
        UserDetails admin = new org.springframework.security.core.userdetails.User("admin", "password", Collections.singletonList(() -> "ROLE_ADMIN"));
        adminToken = "Bearer " + jwtUtils.generateToken(admin);
    }

    @Test
    void getAllTasks_ShouldReturnEmptyList_WhenNoTasksExist() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        Task task = new Task("Test Task", "Description", Status.PENDING, Priority.MEDIUM, LocalDate.now());

        mockMvc.perform(post("/api/tasks")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskTitle").value("Test Task"));
    }

    @Test
    void deleteTask_ShouldReturnForbidden_ForUserRole() throws Exception {
        Task task = taskRepository.save(new Task("Test Task", "Description", Status.PENDING, Priority.MEDIUM, LocalDate.now()));

        mockMvc.perform(delete("/api/tasks/" + task.getTaskId())
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteTask_ShouldReturnNoContent_ForAdminRole() throws Exception {
        Task task = taskRepository.save(new Task("Test Task", "Description", Status.PENDING, Priority.MEDIUM, LocalDate.now()));

        mockMvc.perform(delete("/api/tasks/" + task.getTaskId())
                .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllTasks_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }
}
