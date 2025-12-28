package com.vijay.User_Master.dto.reception;

import lombok.Data;
import com.vijay.User_Master.entity.Enquiry.EnquiryStatus;

@Data
public class EnquiryDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String subject;
    private String message;
    private EnquiryStatus status;
    private String resolutionNotes;
}
