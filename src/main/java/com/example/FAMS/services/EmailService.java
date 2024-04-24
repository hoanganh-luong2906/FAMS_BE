package com.example.FAMS.services;

import com.example.FAMS.models.EmailDetails;

public interface EmailService {
    // Send mail function
    String sendMail(EmailDetails details);
}
