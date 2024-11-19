package com.cafe.management.system.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {
    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String content, List<String> list) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("anzhelika.yarmak@yandex.ru");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        if(list != null && list.size() > 0) {
            message.setCc(getCcArray(list));
            emailSender.send(message);
        }


    }

    private String[] getCcArray(List<String> cclist) {
        String[] ccArray = new String[cclist.size()];
        for (int i = 0; i < cclist.size(); i++) {
            ccArray[i] = cclist.get(i);
        }
        return ccArray;
    }

    public void forgotMail(String to, String subject, String password) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("anzhelika.yarmak@yandex.ru");
        helper.setTo(to);
        helper.setSubject(subject);
        String htmlMsg = "<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b>" + to
                + "<br><b>Password: </b>" + password
                + "<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";
        message.setContent(htmlMsg, "text/html");
        emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
