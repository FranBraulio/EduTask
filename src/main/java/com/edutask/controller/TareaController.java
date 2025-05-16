package com.edutask.controller;

import com.edutask.entities.*;
import com.edutask.repository.*;
import com.edutask.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService tareaService;
    private final AlumnoService alumnoService;
    private final GrupoService grupoService;
    private final AlumnoTareaService alumnoTareaService;
    private final ProfesorService profesorService;
    private final TelegramService telegramService;
    private final EmailService emailService;

    public TareaController(TareaService tareaService, AlumnoService alumnoService, GrupoService grupoService, AlumnoTareaService alumnoTareaService, ProfesorService profesorService, TelegramService telegramService, EmailService emailService) {
        this.tareaService = tareaService;
        this.alumnoService = alumnoService;
        this.grupoService = grupoService;
        this.alumnoTareaService = alumnoTareaService;
        this.profesorService = profesorService;
        this.telegramService = telegramService;
        this.emailService = emailService;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearTarea(@RequestBody Map<String, String> datos) throws MessagingException {
        String descripcion = datos.get("descripcion");
        String fechaLimite = datos.get("fecha_limite");
        String asignarA = datos.get("asignar_a");
        String profesorId = datos.get("profesorId");

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
            .header { border-bottom: 2px solid #2196F3; margin-bottom: 20px; }
            .header h2 { color: #2196F3; }
            .content p { line-height: 1.6; }
            .footer { font-size: 0.9em; color: #777; border-top: 1px solid #eee; margin-top: 30px; padding-top: 15px; }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h2>EduTask - Nueva Tarea Asignada</h2>
            </div>
            <div class="content">
                <p>Hola <strong>%s</strong>,</p>
                <p>La siguiente tarea ha sido asignada correctamente:</p>
                <blockquote style="border-left: 4px solid #2196F3; margin: 20px 0; padding-left: 15px; color: #555;">
                    %s
                </blockquote>
                <p>Puedes consultar todos los detalles accediendo a tu panel de EduTask.</p>
            </div>
            <div class="footer">
                <p>Este correo ha sido enviado automáticamente por EduTask. No respondas a este mensaje.</p>
                <p>&copy; 2025 EduTask. Todos los derechos reservados.</p>
            </div>
        </div>
    </body>
    </html>
    """.formatted(profesor.getUsername(), descripcion);


        Tarea tarea = new Tarea();
        tarea.setMensaje(descripcion);

        if (fechaLimite != null && !fechaLimite.isEmpty()) {
            tarea.setFecha_fin(LocalDate.parse(fechaLimite).atStartOfDay());
        }

        tarea.setProfesor(profesor);
        tareaService.saveTarea(tarea);

        Set<Alumno> alumnosAsignados = new HashSet<>();
        Long idAsignado = Long.parseLong(asignarA);

        if (idAsignado <= 1000) {
            Grupo grupo = grupoService.findById(idAsignado);
            alumnosAsignados.addAll(grupo.getAlumnos());
        } else {
            Alumno alumno = alumnoService.findById(idAsignado);
            if (alumno == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alumno no encontrado");
            }
            alumnosAsignados.add(alumno);
        }
        for (Alumno alumno : alumnosAsignados) {
            AlumnoTarea alumnoTarea = new AlumnoTarea();
            alumnoTarea.setAlumno(alumno);
            alumnoTarea.setTarea(tarea);
            alumnoTareaService.save(alumnoTarea);

            String chatId = alumno.getTelegramChatId();
            if (chatId != null && !chatId.isEmpty()) {
                String mensaje;
                if (fechaLimite != null && !fechaLimite.isEmpty()) {
                    mensaje = "Buenas, te han asignado la siguiente tarea: " + descripcion + ". Recuerda que tienes de fecha limite: " + fechaLimite;
                }else {
                    mensaje = "Buenas, te han asignado la siguiente tarea: " + descripcion;
                }
                telegramService.sendMessage(chatId, mensaje);
            }
        }
        emailService.enviarCorreo(
            profesor.getEmail(),
            "Tarea asignada - EduTask",
            contenidoHTML
        );

        return ResponseEntity.ok("Tarea creada exitosamente");
    }

    //Metodo para sacar todas las tareas
    @GetMapping("/history")
    public List<Tarea> tareas() {
        return tareaService.findAll();
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<String> deleteTarea(@PathVariable("id") Long id) {
        tareaService.deleteById(id);
        return ResponseEntity.ok("Tarea eliminada correctamente");
    }

}

