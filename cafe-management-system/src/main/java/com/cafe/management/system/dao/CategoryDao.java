package com.cafe.management.system.dao;

import com.cafe.management.system.POJO.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category, Integer> {

    List<Category> getAllCategory();
}
