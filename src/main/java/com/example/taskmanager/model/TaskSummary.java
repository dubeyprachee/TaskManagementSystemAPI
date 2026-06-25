package com.example.taskmanager.model;

import lombok.Data;

import java.util.List;

@Data
public class TaskSummary {
    List<String> months;
    private List<Integer> compledtedCounts;
    private List<Integer> pendingCounts;
    private List<Integer> overdueCounts;
}
