package com.cafe.management.system.service.impl;

import com.cafe.management.system.JWT.CustomerUserDetailsService;
import com.cafe.management.system.JWT.JwtFilter;
import com.cafe.management.system.JWT.JwtUtil;
import com.cafe.management.system.POJO.User;
import com.cafe.management.system.constents.CafeConstants;
import com.cafe.management.system.dao.UserDao;
import com.cafe.management.system.service.UserService;
import com.cafe.management.system.utils.CafeUtils;
import com.cafe.management.system.utils.EmailUtils;
import com.cafe.management.system.wrapper.UserWrapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    // Dependency injection to interact with the user database
    @Autowired
    UserDao userDao;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Sign up request : {}", requestMap); // Logging the incoming registration request

        try {
            if (validateSignUpMap(requestMap)) {

                User user = userDao.findByEmailId(requestMap.get("email"));

                if (Objects.isNull(user)) {
                    String encodedPassword = passwordEncoder.encode(requestMap.get("password"));
                    requestMap.put("password", encodedPassword);
                    userDao.save(getUserFromMap(requestMap)); // Save new user data to the database
                    return CafeUtils.getResponseEntity("Successfully registered", HttpStatus.OK); // Success response
                } else {
                    return CafeUtils.getResponseEntity("Email already exists.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (authentication.isAuthenticated()) {
                UserDetails userDetails = customerUserDetailsService.loadUserByUsername(requestMap.get("email"));
                log.info("User details: {}", userDetails);
                 if(customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                     return new ResponseEntity<String>("{\"token\":\""+
                             jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                     customerUserDetailsService.getUserDetail().getRole()) + "\"}",
                             HttpStatus.OK);
                 }
                 else {
                     return new ResponseEntity<String>("{\"message\":\""+"Wait for admin approval."+"\"}",
                             HttpStatus.BAD_REQUEST);
                 }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return new ResponseEntity<String>("{\"message\":\""+"Bad Credentials."+"\"}",
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try {
            if(jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            log.info("Checking if user is admin...");

            if (jwtFilter.isAdmin()) {

                log.info("User is admin. Proceeding with update...");

                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if(optional.isPresent()) {
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmins());
                    return CafeUtils.getResponseEntity("User Status Updated Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("User id does not exist.", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmins) {
        allAdmins.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER:– " + user + " \n is approved by \nADMIN:–" + jwtFilter.getCurrentUser(), allAdmins);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "USER:– " + user + " \n is disabled by \nADMIN:–" + jwtFilter.getCurrentUser(), allAdmins);
        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        log.info("oldPassword: {}", requestMap.get("oldPassword"));
        log.info("newPassword: {}", requestMap.get("newPassword"));
        try {
            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
            if(userObj != null) {
                log.info("oldPassword: {}", requestMap.get("oldPassword"));
                log.info("newPassword: {}", requestMap.get("newPassword"));
                if(passwordEncoder.matches(requestMap.get("oldPassword"), userObj.getPassword())) {
                    String encodedNewPassword = passwordEncoder.encode(requestMap.get("newPassword"));
                    userObj.setPassword(encodedNewPassword);
                    userDao.save(userObj);
                    return CafeUtils.getResponseEntity("Password successfully changed", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
            }
            log.info("user obj is null");
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
                emailUtils.forgotMail(user.getEmail(), "Credentials by Cafe Management System", user.getPassword());
                return CafeUtils.getResponseEntity("Check your mail for Credentials.", HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
