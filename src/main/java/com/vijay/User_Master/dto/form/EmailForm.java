package com.vijay.User_Master.dto.form;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EmailForm {

    private String to;

    private String subject;

    private String title;

    private String message;
}
