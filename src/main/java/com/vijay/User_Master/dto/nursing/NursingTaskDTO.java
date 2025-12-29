package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NursingTaskDTO {
    private Long id;
    private Long wardId;
    private Long assignmentId;

    private String title;
    private String description;
    private String priority;
    private String status;
    private String shift;

    private LocalDateTime dueAt;
    private LocalDateTime completedAt;
}
