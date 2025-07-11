package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entidades.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByDni(String dni);
    List<Cliente> findTop5ByOrderByFechaRegistroDesc();
}