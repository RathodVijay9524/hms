package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "workers")
@EntityListeners(AuditingEntityListener.class)
public class Worker extends BaseModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String phoNo;
    private boolean isDeleted;
    private LocalDateTime deletedOn;
    @Column(length = 1000)
    private String about;

    private String imageName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "workers_roles",
            joinColumns = @JoinColumn(name = "worker_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private AccountStatus accountStatus;

}
