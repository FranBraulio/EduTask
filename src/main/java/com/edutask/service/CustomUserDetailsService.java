package com.edutask.service;

import com.edutask.entities.Profesor;
import com.edutask.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private ProfesorRepository profesorRepository;
    static int contador=0;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Profesor profesor = profesorRepository.findByEmail(email);
            if (profesor == null) {
                throw new BadCredentialsException("Credenciales incorrectas");
            }else{
                contador++;
            }
            if (contador>=2){
                contador=0;
                throw new BadCredentialsException("Credenciales incorrectas");
            }
            return User.withUsername(profesor.getEmail())
                .password(profesor.getPassword())
                .roles(profesor.getRol())
                .build();
        } catch (Exception e) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }
    }

    public static void resetContador() {
        contador = 0;
    }

}

