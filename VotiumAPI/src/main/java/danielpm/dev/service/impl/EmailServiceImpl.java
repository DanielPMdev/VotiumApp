package danielpm.dev.service.impl;

import danielpm.dev.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * @author danielpm.dev
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Restablecer contraseña de Votium APP");

            // Simplified HTML content
            String htmlContent = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Restablecer Contraseña - Votium APP</title>
                <style>
                    body { background-color: #f8f9fa; font-family: system-ui, -apple-system, Arial, sans-serif; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 6px; padding: 20px; }
                    .logo { display: block; margin: 0 auto 20px; max-width: 150px; }
                    h1 { font-size: 22px; color: #212529; margin-bottom: 15px; text-align: center; }
                    p { font-size: 16px; color: #212529; margin-bottom: 15px; }
                    .btn {\s
                        display: inline-block;\s
                        background-color: #ffc107;\s
                        color: #212529;\s
                        text-decoration: none;\s
                        padding: 10px 20px;\s
                        border-radius: 4px;\s
                        font-weight: bold;\s
                        font-size: 16px;\s
                        text-align: center;\s
                    }
                    .btn:hover { background-color: #e0a800; }
                    .footer { font-size: 14px; color: #6c757d; text-align: center; margin-top: 20px; }
                    hr { border: 0; border-top: 1px solid #dee2e6; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <img src="https://i.imgur.com/eLixp4G.png" alt="Votium APP" class="logo">
                    <h1>Restablecer tu contraseña</h1>
                    <p>Haz clic en el botón para elegir una nueva contraseña:</p>
                    <p style="text-align: center;">
                        <a href="%s" class="btn">Restablecer Contraseña</a>
                    </p>
                    <hr>
                    <div class="footer">
                        <p>© 2025 Votium APP. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
           \s""".formatted(resetUrl);

            helper.setText(htmlContent, true); // true indicates the content is HTML
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de restablecimiento", e);
        }
    }
}
