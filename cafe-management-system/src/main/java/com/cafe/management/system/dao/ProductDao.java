package com.cafe.management.system.dao;

import com.cafe.management.system.POJO.Product;
import com.cafe.management.system.wrapper.ProducWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductDao extends JpaRepository<Product, Integer> {
    List<ProducWrapper> getAllProduct();

    @Modifying
    @Transactional
    Integer updateProductStatus(@Param("status") String status, @Param("id") Integer id);

    List<ProducWrapper> getProductByCategory(@Param("id") Integer id);

    ProducWrapper getProductById(@Param("id") Integer id);
}
