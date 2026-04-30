package com.minimaxi.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String toEmail, String contactName, Long accessRequestId) {
        String activationLink = "http://localhost:5173/activate?id=" + accessRequestId;

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #1a1a2e, #16213e); padding: 40px 30px; text-align: center; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 28px; letter-spacing: 2px; }
                    .header p { color: #a0aec0; margin: 8px 0 0; font-size: 14px; }
                    .body { padding: 40px 30px; }
                    .body h2 { color: #1a1a2e; font-size: 22px; margin-bottom: 16px; }
                    .body p { color: #4a5568; font-size: 15px; line-height: 1.7; margin: 0 0 16px; }
                    .highlight { background-color: #f0f7ff; border-left: 4px solid #3182ce; padding: 16px 20px; border-radius: 6px; margin: 24px 0; }
                    .highlight p { margin: 0; color: #2b6cb0; font-size: 14px; }
                    .btn-container { text-align: center; margin: 32px 0; }
                    .btn { display: inline-block; background: linear-gradient(135deg, #3182ce, #2b6cb0); color: #ffffff !important; text-decoration: none; padding: 14px 36px; border-radius: 8px; font-size: 16px; font-weight: bold; letter-spacing: 1px; }
                    .footer { background-color: #f7fafc; padding: 24px 30px; text-align: center; border-top: 1px solid #e2e8f0; }
                    .footer p { color: #a0aec0; font-size: 12px; margin: 0; }
                    .footer strong { color: #4a5568; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>MiniMaxi</h1>
                        <p>AI-Powered Predictive Maintenance Platform</p>
                    </div>
                    <div class="body">
                        <h2>Welcome, %s! 👋</h2>
                        <p>We're excited to inform you that your access request has been <strong>approved</strong>. You're one step away from accessing the MiniMaxi platform.</p>
                        <div class="highlight">
                            <p>🔐 Click the button below to set your password and activate your account. This link is unique to your request.</p>
                        </div>
                        <div class="btn-container">
                            <a href="%s" class="btn">Activate My Account</a>
                        </div>
                        <p>If the button doesn't work, copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; color: #3182ce; font-size: 13px;">%s</p>
                        <p>If you have any questions, feel free to contact our support team.</p>
                        <p>Best regards,<br><strong>The MiniMaxi Team</strong></p>
                    </div>
                    <div class="footer">
                        <p>© 2026 <strong>MiniMaxi</strong> — AI-Powered Predictive Maintenance</p>
                        <p>This email was sent to %s</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(contactName, activationLink, activationLink, toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("✅ Your MiniMaxi Access Has Been Approved!");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    public void sendOtpEmail(String toEmail, String contactName, String otp) {
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #1a1a2e, #16213e); padding: 40px 30px; text-align: center; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 28px; letter-spacing: 2px; }
                    .header p { color: #a0aec0; margin: 8px 0 0; font-size: 14px; }
                    .body { padding: 40px 30px; text-align: center; }
                    .body h2 { color: #1a1a2e; font-size: 22px; margin-bottom: 16px; }
                    .body p { color: #4a5568; font-size: 15px; line-height: 1.7; margin: 0 0 16px; }
                    .otp-box { background: #f0f7ff; border: 2px dashed #3182ce; border-radius: 12px; padding: 24px; margin: 24px 0; }
                    .otp-code { font-size: 42px; font-weight: bold; color: #2b6cb0; letter-spacing: 12px; }
                    .expire { color: #e53e3e; font-size: 13px; margin-top: 8px; }
                    .footer { background-color: #f7fafc; padding: 24px 30px; text-align: center; border-top: 1px solid #e2e8f0; }
                    .footer p { color: #a0aec0; font-size: 12px; margin: 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>⚡ MiniMaxi</h1>
                        <p>AI-Powered Predictive Maintenance Platform</p>
                    </div>
                    <div class="body">
                        <h2>Password Reset Request 🔐</h2>
                        <p>Hi <strong>%s</strong>, we received a request to reset your password.</p>
                        <p>Use the OTP code below to reset your password:</p>
                        <div class="otp-box">
                            <div class="otp-code">%s</div>
                            <div class="expire">⏱ This code expires in 15 minutes</div>
                        </div>
                        <p>If you didn't request this, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>© 2026 <strong>MiniMaxi</strong> — AI-Powered Predictive Maintenance</p>
                        <p>This email was sent to %s</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(contactName, otp, toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("🔐 Your MiniMaxi Password Reset Code");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }
}