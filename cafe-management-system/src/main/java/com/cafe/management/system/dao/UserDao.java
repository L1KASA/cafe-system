package com.cafe.management.system.dao;

import com.cafe.management.system.POJO.User;
import com.cafe.management.system.wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {

    User findByEmailId(@Param("email") String email);

    List<UserWrapper> getAllUsers();

    List<String> getAllAdmins();

    @Transactional
    @Modifying
    void updateStatus(@Param("status") String status, @Param("id") Integer id);

    User findByEmail(@Param("email") String email);
}
