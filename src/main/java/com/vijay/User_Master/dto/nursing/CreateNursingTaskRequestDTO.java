package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNursingTaskRequestDTO {
    private Long wardId;
    private Long assignmentId;

    private String title;
    private String description;

    private String priority; // HIGH/MEDIUM/LOW
    private String shift;
    private LocalDateTime dueAt;
}
