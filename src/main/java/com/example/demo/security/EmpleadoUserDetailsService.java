package com.example.demo.security;

import com.example.demo.entidades.Empleado;
import com.example.demo.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoUserDetailsService implements UserDetailsService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Empleado empleado = empleadoRepository.findByDni(dni);
        if (empleado == null) {
            throw new UsernameNotFoundException("Empleado no encontrado con DNI: " + dni);
        }
        return new EmpleadoUserDetails(empleado);
    }
}