package com.example.back_end.repository;

import com.example.back_end.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IUserRepository extends JpaRepository<Users,Long> {
    Users findByUsername(String username);
    Users findByEmail(String email);

    @Query(value = "select u from users u where u.username=:username ",nativeQuery = true)
    Users getByUsername(@Param("username") String username);
    @Modifying
    @Query(value = "UPDATE users SET password = :password,verification_code = 0 WHERE id = :id", nativeQuery = true)
    void saveNewPassword(@Param("id") Long id, @Param("password") String password);

}
