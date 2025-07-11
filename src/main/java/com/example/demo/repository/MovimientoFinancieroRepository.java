package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entidades.MovimientoFinanciero;
import java.time.LocalDate;
import java.util.List;

public interface MovimientoFinancieroRepository extends JpaRepository<MovimientoFinanciero, Long> {
    List<MovimientoFinanciero> findByFechaBetween(LocalDate start, LocalDate end);
    List<MovimientoFinanciero> findByTipo(MovimientoFinanciero.Tipo tipo);

    @Query("SELECT SUM(m.monto) FROM MovimientoFinanciero m " +
           "WHERE m.tipo = :tipo AND m.fecha BETWEEN :start AND :end")
    Double sumMontoByTipoAndFechaBetween(
        @Param("tipo") MovimientoFinanciero.Tipo tipo,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
}
