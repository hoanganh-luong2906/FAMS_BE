package com.example.FAMS.service_implementors;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.example.FAMS.models.EmailDetails;
import com.example.FAMS.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    MimeMessage mimeMessage;

    private final JavaMailSender javaMailSender;

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendMail(EmailDetails details) {
        try {
            mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setSubject(details.getSubject());
            mimeMessageHelper.setText(readFormat(details.getMsgBody()), true);

            String message = readFormat(details.getMsgBody());

            mimeMessage.setContent(message, "text/html; charset=utf-8");

            SendEmailRequest sendEmailRequest = new SendEmailRequest()
                    .withSource(mimeMessage.getFrom()[0].toString())
                    .withDestination(new Destination().withToAddresses(details.getRecipient()))
                    .withMessage(new Message()
                            .withSubject(new Content().withData(details.getSubject()))
                            .withBody(new Body().withHtml(new Content().withData(readFormat(details.getMsgBody()))))
                    );


            amazonSimpleEmailService.sendEmail(sendEmailRequest);

            return "Mail sent successfully";
        } catch (Exception e) {
            return "Mail sent fail";
        }
    }

    private String readFormat(String randomPass) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("src/main/java/com/example/FAMS/files/MailFormat.txt"));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        String[] arr = result.toString().split("@@@@@@@@######");
        if (arr.length == 2) {
            result = new StringBuilder(arr[0] + randomPass + arr[1]);
        }
        return result.toString();
    }

}
