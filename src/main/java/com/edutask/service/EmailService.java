package com.edutask.service;

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

    public void enviarCorreo(String destinatario, String asunto, String contenido) throws MessagingException {
        if (destinatario == null) {
            return;
        }
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);
        helper.setFrom("athmosedutask@gmail.com");
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(contenido, true);
        mailSender.send(mensaje);
    }
}
