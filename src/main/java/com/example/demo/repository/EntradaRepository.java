package com.example.demo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entidades.Entrada;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {
    
    // Contar entradas por fecha específica
    @Query("SELECT COUNT(e) FROM Entrada e WHERE DATE(e.fecha) = :fecha")
    long countByFecha(@Param("fecha") LocalDate fecha);
    
    // Contar entradas por DNI
    long countByDniCliente(String dniCliente);
    
    // Buscar entradas por DNI
    List<Entrada> findByDniCliente(String dniCliente);
    
    // Buscar entradas por rango de fechas
    @Query("SELECT e FROM Entrada e WHERE e.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Entrada> findByFechaBetween(
        @Param("fechaInicio") LocalDateTime fechaInicio, 
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    // Obtener entradas del día actual
    @Query("SELECT e FROM Entrada e WHERE DATE(e.fecha) = CURRENT_DATE")
    List<Entrada> findEntradasDeHoy();
    
    // Obtener total de ingresos por fecha
    @Query("SELECT SUM(e.monto) FROM Entrada e WHERE DATE(e.fecha) = :fecha")
    Double getTotalIngresosPorFecha(@Param("fecha") LocalDate fecha);
    
    // Obtener entradas ordenadas por fecha descendente
    List<Entrada> findAllByOrderByFechaDesc();
    
    @Query("SELECT e FROM Entrada e ORDER BY e.fecha DESC LIMIT 5")
    List<Entrada> findTop5ByOrderByFechaDesc();
}