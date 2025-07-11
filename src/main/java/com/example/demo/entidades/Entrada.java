package com.example.demo.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrada")
public class Entrada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "dni_cliente", nullable = false, length = 255)
    private String dniCliente;
    
    @Column(nullable = false)
    private double monto;
    
    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;

    // Constructor por defecto
    public Entrada() {
        this.fecha = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Entrada(String dniCliente, double monto) {
        this.dniCliente = dniCliente;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Entrada{" +
                "id=" + id +
                ", dniCliente='" + dniCliente + '\'' +
                ", monto=" + monto +
                ", fecha=" + fecha +
                '}';
    }
}