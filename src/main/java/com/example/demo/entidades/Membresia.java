package com.example.demo.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "membresia")
public class Membresia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;
    
    @Column(name = "fecha_asignacion", updatable = false)
    private LocalDateTime fechaAsignacion = LocalDateTime.now();
    
    public enum Tipo {
        Diario, Mensual, Bimensual, Trimestral
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public LocalDateTime getFechaAsignacion() {
		return fechaAsignacion;
	}

	public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
		this.fechaAsignacion = fechaAsignacion;
	}
    
    
}