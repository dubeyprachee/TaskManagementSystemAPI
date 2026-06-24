package com.example.taskmanager;

import com.example.taskmanager.config.JwtUtils;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskFilteringTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    private String userToken;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new User("testuser", "password", "ROLE_USER"));
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        userToken = "Bearer " + jwtUtils.generateToken(userDetails);

        taskRepository.save(new Task("Task 1", "Desc 1", Status.PENDING, Priority.HIGH, LocalDate.now().plusDays(1)));
        taskRepository.save(new Task("Task 2", "Desc 2", Status.IN_PROGRESS, Priority.MEDIUM, LocalDate.now().plusDays(2)));
        taskRepository.save(new Task("Task 3", "Desc 3", Status.COMPLETED, Priority.LOW, LocalDate.now().plusDays(3)));
    }

    @Test
    void filterByStatus() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", userToken)
                .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].taskTitle").value("Task 1"));
    }

    @Test
    void filterByPriority() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", userToken)
                .param("priority", "MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].taskTitle").value("Task 2"));
    }

    @Test
    void filterByDueDate() throws Exception {
        String date = LocalDate.now().plusDays(3).toString();
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", userToken)
                .param("dueDate", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].taskTitle").value("Task 3"));
    }

    @Test
    void assignTaskToUser() throws Exception {
        User user = userRepository.save(new User("assignee", "password", "ROLE_USER"));
        Task task = taskRepository.findAll().get(0);

        mockMvc.perform(put("/api/tasks/" + task.getTaskId() + "/assign/" + user.getUserId())
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedTo").value(user.getUserId()));
    }
}
