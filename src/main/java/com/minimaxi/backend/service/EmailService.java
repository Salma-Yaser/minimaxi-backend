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

    // ✅ رابط الفرونت بقى configurable بدل ما يكون مكتوب جوه الكود (hardcoded)
    // لو مفيش env variable متظبط هيستخدم رابط الـ Vercel كـ fallback
    @Value("${APP_FRONTEND_URL:https://minimaxi.vercel.app}")
    private String frontendUrl;

    public void sendActivationEmail(String toEmail, String contactName, Long accessRequestId) {
        String activationLink = frontendUrl + "/activate?id=" + accessRequestId;
        String subject = "✅ Your MiniMaxi Access Has Been Approved!";

        String body = """
            <p style="margin:0 0 16px;">Your access request has been <strong>approved</strong>.
            Click the button below to activate your account and get started:</p>
            %s
            <p style="margin:24px 0 0; font-size:13px; color:#718096;">
                If the button doesn't work, copy and paste this link into your browser:<br>
                <a href="%s" style="color:#3182ce; word-break:break-all;">%s</a>
            </p>
            """.formatted(buildButton("Activate My Account", activationLink), activationLink, activationLink);

        String htmlContent = buildEmailShell("Welcome to MiniMaxi, " + escape(contactName) + "!", body);
        sendEmail(toEmail, subject, htmlContent);
    }

    public void sendOtpEmail(String toEmail, String contactName, String otp) {
        String subject = "🔐 Your MiniMaxi Password Reset Code";

        String body = """
            <p style="margin:0 0 16px;">Hi <strong>%s</strong>, use the verification code below to reset your password:</p>
            <div style="margin:24px 0; text-align:center;">
                <span style="display:inline-block; font-size:32px; font-weight:700; letter-spacing:8px;
                             background:#f7fafc; border:1px solid #e2e8f0; border-radius:8px;
                             padding:16px 24px; color:#1a202c;">%s</span>
            </div>
            <p style="margin:0; font-size:13px; color:#718096;">
                This code expires in 15 minutes. If you didn't request this, you can safely ignore this email.
            </p>
            """.formatted(escape(contactName), otp);

        String htmlContent = buildEmailShell("Password Reset Request", body);
        sendEmail(toEmail, subject, htmlContent);
    }

    public void sendInvitationEmail(String toEmail, String contactName, String inviteToken) {
        String activationLink = frontendUrl + "/set-password?token=" + inviteToken;
        String subject = "You've Been Invited to MiniMaxi!";

        String body = """
            <p style="margin:0 0 16px;">You have been invited to join <strong>MiniMaxi</strong>.
            Click the button below to set your password and activate your account:</p>
            %s
            <p style="margin:24px 0 0; font-size:13px; color:#718096;">
                If the button doesn't work, copy and paste this link into your browser:<br>
                <a href="%s" style="color:#3182ce; word-break:break-all;">%s</a>
            </p>
            <p style="margin:16px 0 0; font-size:13px; color:#718096;">This link expires in 48 hours.</p>
            """.formatted(buildButton("Set My Password", activationLink), activationLink, activationLink);

        String htmlContent = buildEmailShell("Hello, " + escape(contactName) + "!", body);
        sendEmail(toEmail, subject, htmlContent);
    }

    // ===================== Helpers (تصميم الإيميل) =====================

    private String buildButton(String label, String link) {
        return """
            <div style="text-align:center; margin:28px 0;">
                <a href="%s" style="background:#3182ce; color:#ffffff; padding:12px 28px;
                   border-radius:6px; text-decoration:none; font-weight:600; font-size:15px;
                   display:inline-block;">%s</a>
            </div>
            """.formatted(link, label);
    }

    /**
     * Shell موحّد لكل الإيميلات: header بالـ logo/title + body ديناميكي + footer.
     * بيدي شكل احترافي وموحّد لكل الرسايل.
     */
    private String buildEmailShell(String heading, String bodyHtml) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0; padding:0; background:#edf2f7; font-family:Segoe UI, Arial, sans-serif;">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#edf2f7; padding:32px 0;">
                    <tr>
                        <td align="center">
                            <table role="presentation" width="480" cellpadding="0" cellspacing="0"
                                   style="background:#ffffff; border-radius:10px; overflow:hidden; max-width:480px;">
                                <tr>
                                    <td style="background:#1a202c; padding:24px 32px;">
                                        <span style="color:#ffffff; font-size:20px; font-weight:700; letter-spacing:0.5px;">MiniMaxi</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:32px;">
                                        <h2 style="margin:0 0 16px; color:#1a202c; font-size:20px;">%s</h2>
                                        <div style="color:#2d3748; font-size:15px; line-height:1.6;">
                                            %s
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="background:#f7fafc; padding:18px 32px; border-top:1px solid #e2e8f0;">
                                        <p style="margin:0; font-size:12px; color:#a0aec0;">
                                            © %d MiniMaxi. All rights reserved. This is an automated message, please do not reply.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(heading, bodyHtml, java.time.Year.now().getValue());
    }

    /** حماية بسيطة من HTML injection في الاسم/المحتوى الديناميكي */
    private String escape(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
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