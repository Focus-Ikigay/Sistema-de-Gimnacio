package com.example.demo.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entidades.Membresia;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    long countByTipo(Membresia.Tipo tipo);
    
    @Query("SELECT COUNT(m) FROM Membresia m " +
    	       "WHERE m.fechaAsignacion >= :fechaLimite")
    	long countActiveMemberships(@Param("fechaLimite") LocalDateTime fechaLimite);

    	@Query("SELECT COUNT(m) FROM Membresia m " +
    	       "WHERE m.fechaAsignacion <= :fechaLimite")
    	long countRenovacionesPendientes(@Param("fechaLimite") LocalDateTime fechaLimite);
}