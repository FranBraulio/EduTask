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

        if (profesorId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profesor no encontrado");
        }

        Profesor profesor = profesorService.findById(Long.parseLong(profesorId));

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
        emailService.enviarCorreo(profesor.getEmail(), "Tarea asignada", "Se ha asignado la tarea: "+ descripcion +" correctamente.");

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

