package com.sivalabs.bookstore.orderservice.notifications;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendEmail(String to, String subject, String content) {
        System.out.println("=============================");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println(content);
        System.out.println("=============================");
    }
}
