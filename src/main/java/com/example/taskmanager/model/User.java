package com.example.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column("user_id")
    private Long userId;

    @NotBlank(message = "Username is required")
    @Column(unique = true)
//    @Column("username")
    private String userName;

    @NotBlank(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @Column("password")
    private String password;

    @NotBlank(message = "Role is required")
//    @Column("role")
    private String role; // e.g., ROLE_USER, ROLE_ADMIN

    @OneToMany(mappedBy = "assignedUser")
    @JsonIgnore
    private List<Task> tasks;

    public User() {
    }

    public User(String username, String password, String role) {
        this.userName = username;
        this.password = password;
        this.role = role;
    }

}
