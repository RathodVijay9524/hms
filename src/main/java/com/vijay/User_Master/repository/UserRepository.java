package com.vijay.User_Master.repository;


import com.vijay.User_Master.entity.User;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {


    User findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);



    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    User findByUsername(String username);


    Page<User> findAllByIsDeletedFalse(Pageable pageable);

    Page<User> findAllByIsDeletedTrue(Pageable pageable);

    Page<User> findAllByIsDeleted(boolean isDeleted, Pageable pageable);

    Page<User> findAllByAccountStatus_IsActive(boolean isActive, Pageable pageable);

    Page<User> findAllByIsDeletedAndAccountStatus_IsActive(boolean isDeleted, boolean isActive, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.username LIKE %:keyword%) AND " +
            "(:isDeleted IS NULL OR u.isDeleted = :isDeleted) AND " +
            "(:isActive IS NULL OR u.accountStatus.isActive = :isActive)")
    Page<User> searchUsersWithKeyword(@Param("keyword") String keyword,
                                      @Param("isDeleted") Boolean isDeleted,
                                      @Param("isActive") Boolean isActive,
                                      Pageable pageable);



}
