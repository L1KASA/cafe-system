package com.cafe.management.system.rest;

import com.cafe.management.system.POJO.Product;
import com.cafe.management.system.wrapper.ProducWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {

    @PostMapping(path = "/add")
    ResponseEntity<String> addProduct(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/get")
    ResponseEntity<List<ProducWrapper>> getAllProduct();

    @PostMapping(path = "/update")
    ResponseEntity<String> updateProduct(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable("id") Integer id);

    @PostMapping(path = "/updateStatus")
    ResponseEntity<String> updateStatus(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/getByCategory/{id}")
    ResponseEntity<List<ProducWrapper>> getByCategory(@PathVariable("id") Integer id);

    @GetMapping(path = "/getById/{id}")
    ResponseEntity<ProducWrapper> getProductById(@PathVariable("id") Integer id);

}
