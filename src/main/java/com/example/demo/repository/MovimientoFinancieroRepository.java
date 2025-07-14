package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entidades.MovimientoFinanciero;
import java.time.LocalDate;
import java.util.List;

public interface MovimientoFinancieroRepository extends JpaRepository<MovimientoFinanciero, Long> {
    
    // Buscar por fecha exacta
    List<MovimientoFinanciero> findByFecha(LocalDate fecha);
    
    // Buscar por tipo
    List<MovimientoFinanciero> findByTipo(MovimientoFinanciero.Tipo tipo);
    
    // Buscar por fecha y tipo
    List<MovimientoFinanciero> findByFechaAndTipo(LocalDate fecha, MovimientoFinanciero.Tipo tipo);
    
    // Buscar por rango de fechas
    List<MovimientoFinanciero> findByFechaBetween(LocalDate start, LocalDate end);
    
    // Buscar por tipo y rango de fechas
    List<MovimientoFinanciero> findByTipoAndFechaBetween(MovimientoFinanciero.Tipo tipo, LocalDate start, LocalDate end);
    
    // Sumar montos por tipo y rango de fechas
    @Query("SELECT SUM(m.monto) FROM MovimientoFinanciero m " +
           "WHERE m.tipo = :tipo AND m.fecha BETWEEN :start AND :end")
    Double sumMontoByTipoAndFechaBetween(
        @Param("tipo") MovimientoFinanciero.Tipo tipo,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
    
    // Sumar todos los ingresos
    @Query("SELECT SUM(m.monto) FROM MovimientoFinanciero m WHERE m.tipo = 'Ingreso'")
    Double sumTotalIngresos();
    
    // Sumar todos los gastos
    @Query("SELECT SUM(m.monto) FROM MovimientoFinanciero m WHERE m.tipo = 'Gasto'")
    Double sumTotalGastos();
}