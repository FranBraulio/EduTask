package com.edutask.controller;

import com.edutask.entities.*;
import com.edutask.repository.*;
import com.edutask.service.*;
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

    public TareaController(TareaService tareaService, AlumnoService alumnoService, GrupoService grupoService, AlumnoTareaService alumnoTareaService, ProfesorService profesorService, TelegramService telegramService) {
        this.tareaService = tareaService;
        this.alumnoService = alumnoService;
        this.grupoService = grupoService;
        this.alumnoTareaService = alumnoTareaService;
        this.profesorService = profesorService;
        this.telegramService = telegramService;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearTarea(@RequestBody Map<String, String> datos) {
        String descripcion = datos.get("descripcion");
        String fechaLimite = datos.get("fecha_limite");
        String asignarA = datos.get("asignar_a");
        String profesorId = datos.get("profesorId");
        System.out.println("AQUI "+asignarA);


        Tarea tarea = new Tarea();
        tarea.setMensaje(descripcion);

        if (fechaLimite != null && !fechaLimite.isEmpty()) {
            tarea.setFecha_fin(LocalDate.parse(fechaLimite).atStartOfDay());
        }

        tarea.setProfesor(profesorService.findById(Long.parseLong(profesorId)));
        tareaService.saveTarea(tarea);

        Set<Alumno> alumnosAsignados = new HashSet<>();
        Long idAsignado = Long.parseLong(asignarA);
        System.out.println("AQUI "+idAsignado);

        if (idAsignado <= 1000) {
            Grupo grupo = grupoService.findById(idAsignado);
            alumnosAsignados.addAll(grupo.getAlumnos());
        } else {
            Alumno alumno = alumnoService.findById(idAsignado);
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

