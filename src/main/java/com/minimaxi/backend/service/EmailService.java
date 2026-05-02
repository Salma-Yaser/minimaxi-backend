package com.minimaxi.backend.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    @Value("${MAIL_FROM:no-reply@minimaxi.com}")
    private String fromEmail;

    public void sendActivationEmail(String toEmail, String contactName, Long accessRequestId) {
        String activationLink = "http://localhost:5173/activate?id=" + accessRequestId;
        String subject = "✅ Your MiniMaxi Access Has Been Approved!";
        String htmlContent = """
            <h2>Welcome, %s!</h2>
            <p>Your access request has been <strong>approved</strong>. Click the button below to activate your account:</p>
            <a href=\"%s\" style=\"background:#3182ce;color:#fff;padding:10px 20px;border-radius:5px;text-decoration:none;\">Activate My Account</a>
            <p>If the button doesn't work, copy and paste this link: %s</p>
        """.formatted(contactName, activationLink, activationLink);
        sendEmail(toEmail, subject, htmlContent);
    }

    public void sendOtpEmail(String toEmail, String contactName, String otp) {
        String subject = "🔐 Your MiniMaxi Password Reset Code";
        String htmlContent = """
            <h2>Password Reset Request</h2>
            <p>Hi <strong>%s</strong>, use the OTP code below to reset your password:</p>
            <div style=\"font-size:2em;font-weight:bold;\">%s</div>
            <p>This code expires in 15 minutes.</p>
        """.formatted(contactName, otp);
        sendEmail(toEmail, subject, htmlContent);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("Failed to send email: " + response.getBody());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to send email: " + ex.getMessage());
        }
    }
}