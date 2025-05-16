package com.edutask.controller;

import com.edutask.entities.*;
import com.edutask.service.*;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/aviso")
public class AvisoController {

    private final AlumnoService alumnoService;
    private final GrupoService grupoService;
    private final ProfesorService profesorService;
    private final TelegramService telegramService;
    private final AvisoService avisoService;
    private final EmailService emailService;

    public AvisoController(AlumnoService alumnoService, GrupoService grupoService, ProfesorService profesorService, TelegramService telegramService, AvisoService avisoService, EmailService emailService) {
        this.alumnoService = alumnoService;
        this.grupoService = grupoService;
        this.profesorService = profesorService;
        this.telegramService = telegramService;
        this.avisoService = avisoService;
        this.emailService = emailService;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearAviso(@RequestBody Map<String, String> datos) throws MessagingException {
        String mensajeAviso = datos.get("mensaje_aviso");
        String asignarA = datos.get("enviar_a_aviso");
        String profesorId = datos.get("profesorId");
        String enviarPor = datos.get("enviar_por");

        Profesor profesor = profesorService.findById(Long.parseLong(profesorId));

        if (profesor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profesor no encontrado");
        }

        String contenidoHTML = """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; color: #333; background-color: #f9f9f9; padding: 20px; }
                    .container { max-width: 600px; margin: auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); padding: 30px; }
                    .header { border-bottom: 2px solid #4CAF50; margin-bottom: 20px; }
                    .header h2 { color: #4CAF50; }
                    .content p { line-height: 1.6; }
                    .footer { font-size: 0.9em; color: #777; border-top: 1px solid #eee; margin-top: 30px; padding-top: 15px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>EduTask - Notificación de Aviso</h2>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        <p>Te informamos que el siguiente aviso ha sido enviado correctamente:</p>
                        <blockquote style="border-left: 4px solid #4CAF50; margin: 20px 0; padding-left: 15px; color: #555;">
                            %s
                        </blockquote>
                        <p>Gracias por confiar en EduTask.</p>
                    </div>
                    <div class="footer">
                        <p>Este correo ha sido enviado automáticamente por EduTask. No respondas a este mensaje.</p>
                        <p>&copy; 2025 EduTask. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
    """.formatted(profesor.getUsername(), mensajeAviso);


        Aviso aviso = new Aviso();
        aviso.setMensaje(mensajeAviso);
        aviso.setCanal(enviarPor);
        if (profesor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profesor no encontrado");
        }
        aviso.setProfesor(profesor);
        avisoService.save(aviso);

        Set<Alumno> alumnosAsignados = new HashSet<>();
        Long idAsignado = Long.parseLong(asignarA);

        if (idAsignado <= 1000) {
            Grupo grupo = grupoService.findById(idAsignado);
            if (grupo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Grupo no encontrado");
            }
            alumnosAsignados.addAll(grupo.getAlumnos());
        } else {
            Alumno alumno = alumnoService.findById(idAsignado);
            if (alumno == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alumno no encontrado");
            }
            alumnosAsignados.add(alumno);
        }
        for (Alumno alumno : alumnosAsignados) {
            String chatId = alumno.getTelegramChatId();
            if (chatId != null && !chatId.isEmpty()) {
                String mensaje = "AVISO: " + mensajeAviso;
                telegramService.sendMessage(chatId, mensaje);
            }
        }
        emailService.enviarCorreo(
            profesor.getEmail(),
            "Notificación de Aviso - EduTask",
            contenidoHTML
        );

        return ResponseEntity.ok("Aviso creado exitosamente");
    }
}
