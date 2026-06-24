package com.example.taskmanager;

import com.example.taskmanager.config.JwtUtils;
import com.example.taskmanager.model.User;
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

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
        userRepository.deleteAll();

        User userEntity = new User("user", "password", "ROLE_USER");
        userRepository.save(userEntity);
        UserDetails user = new org.springframework.security.core.userdetails.User("user", "password", Collections.singletonList(() -> "ROLE_USER"));
        userToken = "Bearer " + jwtUtils.generateToken(user);

        User adminEntity = new User("admin", "password", "ROLE_ADMIN");
        userRepository.save(adminEntity);
        UserDetails admin = new org.springframework.security.core.userdetails.User("admin", "password", Collections.singletonList(() -> "ROLE_ADMIN"));
        adminToken = "Bearer " + jwtUtils.generateToken(admin);
    }

    @Test
    void getAllUsers_ShouldReturnForbidden_ForUserRole() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_ShouldReturnList_ForAdminRole() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        User newUser = new User("newuser", "password", "ROLE_USER");

        mockMvc.perform(post("/api/users")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\":\"newuser\", \"password\":\"password\", \"role\":\"ROLE_USER\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("newuser"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        User user = userRepository.findByUserName("user").get();

        mockMvc.perform(get("/api/users/" + user.getUserId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("user"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        User user = userRepository.findByUserName("user").get();

        mockMvc.perform(put("/api/users/" + user.getUserId())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\":\"user\", \"password\":\"newpassword\", \"role\":\"ROLE_ADMIN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        User user = userRepository.findByUserName("user").get();

        mockMvc.perform(delete("/api/users/" + user.getUserId())
                .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }
}
