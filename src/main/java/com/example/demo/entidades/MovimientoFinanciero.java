package com.example.demo.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "movimiento_financiero")
public class MovimientoFinanciero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Column(nullable = false)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;
    
    @Column(nullable = false)
    private double monto;
    
    public enum Tipo {
        Ingreso, Gasto
    }

    // Constructores
    public MovimientoFinanciero() {
    }

    public MovimientoFinanciero(LocalDate fecha, String descripcion, Tipo tipo, double monto) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.monto = monto;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    // Método toString para facilitar la depuración
    @Override
    public String toString() {
        return "MovimientoFinanciero{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                ", tipo=" + tipo +
                ", monto=" + monto +
                '}';
    }
}

