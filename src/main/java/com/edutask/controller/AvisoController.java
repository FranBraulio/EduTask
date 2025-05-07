package com.edutask.controller;

import com.edutask.entities.*;
import com.edutask.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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

    public AvisoController(AlumnoService alumnoService, GrupoService grupoService, ProfesorService profesorService, TelegramService telegramService, AvisoService avisoService) {
        this.alumnoService = alumnoService;
        this.grupoService = grupoService;
        this.profesorService = profesorService;
        this.telegramService = telegramService;
        this.avisoService = avisoService;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearAviso(@RequestBody Map<String, String> datos) {
        String mensajeAviso = datos.get("mensaje_aviso");
        String asignarA = datos.get("enviar_a_aviso");
        String profesorId = datos.get("profesorId");
        String enviarPor = datos.get("enviar_por");

        Aviso aviso = new Aviso();
        aviso.setMensaje(mensajeAviso);
        aviso.setCanal(enviarPor);
        aviso.setProfesor(profesorService.findById(Long.parseLong(profesorId)));
        avisoService.save(aviso);

        Set<Alumno> alumnosAsignados = new HashSet<>();
        Long idAsignado = Long.parseLong(asignarA);

        if (idAsignado <= 1000) {
            Grupo grupo = grupoService.findById(idAsignado);
            alumnosAsignados.addAll(grupo.getAlumnos());
        } else {
            Alumno alumno = alumnoService.findById(idAsignado);
            alumnosAsignados.add(alumno);
        }
        for (Alumno alumno : alumnosAsignados) {
            String chatId = alumno.getTelegramChatId();
            if (chatId != null && !chatId.isEmpty()) {
                String mensaje = "AVISO: " + mensajeAviso;
                telegramService.sendMessage(chatId, mensaje);
            }
        }

        return ResponseEntity.ok("Aviso creado exitosamente");
    }
}
