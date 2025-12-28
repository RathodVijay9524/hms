package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enquiries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enquiry extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    private String email;

    @Column(nullable = false)
    private String subject;

    @Column(length = 2000, nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EnquiryStatus status = EnquiryStatus.PENDING;

    private String resolutionNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum EnquiryStatus {
        PENDING, IN_PROGRESS, RESOLVED, CLOSED
    }
}
