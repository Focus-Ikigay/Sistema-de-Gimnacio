package com.example.demo.repository;

import com.example.demo.entidades.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
	Empleado findByDni(String dni);
	
    // Buscar por rol
    List<Empleado> findByRol(Empleado.Rol rol);
    
    // Buscar por estado
    List<Empleado> findByEstado(Empleado.Estado estado);
    
    // Buscar por rol y estado (para filtros combinados)
    List<Empleado> findByRolAndEstado(Empleado.Rol rol, Empleado.Estado estado);
    
    // OPCIÓN 1: Método con nombre largo (Spring Data JPA automático)
    List<Empleado> findByNombreCompletoContainingIgnoreCaseOrDniContaining(String nombreCompleto, String dni);
    
    // OPCIÓN 2: Método con @Query personalizada (MÁS LEGIBLE)
    @Query("SELECT e FROM Empleado e WHERE " +
           "LOWER(e.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "e.dni LIKE CONCAT('%', :busqueda, '%')")
    List<Empleado> buscarPorNombreODni(@Param("busqueda") String busqueda);
    
    // OPCIÓN 3: Búsqueda más amplia (recomendada)
    @Query("SELECT e FROM Empleado e WHERE " +
           "LOWER(e.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "e.dni LIKE CONCAT('%', :busqueda, '%') OR " +
           "LOWER(e.correo) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Empleado> buscarEmpleado(@Param("busqueda") String busqueda);
    
    @Query("SELECT e FROM Empleado e WHERE " +
    	       "(?1 IS NULL OR e.rol = ?1) AND " +
    	       "(?2 IS NULL OR e.estado = ?2) " +
    	       "ORDER BY e.nombreCompleto ASC")
    	List<Empleado> filtrarPorRolYEstado(Empleado.Rol rol, Empleado.Estado estado);

    	@Query("SELECT e FROM Empleado e WHERE " +
    	       "(?1 IS NULL OR e.rol = ?1) AND " +
    	       "(?2 IS NULL OR e.estado = ?2) AND " +
    	       "(?3 IS NULL OR ?3 = '' OR " +
    	       "LOWER(e.nombreCompleto) LIKE LOWER(CONCAT('%', ?3, '%')) OR " +
    	       "e.dni LIKE CONCAT('%', ?3, '%') OR " +
    	       "LOWER(e.correo) LIKE LOWER(CONCAT('%', ?3, '%'))) " +
    	       "ORDER BY e.nombreCompleto ASC")
    	List<Empleado> filtrarPorRolEstadoYBusqueda(Empleado.Rol rol, 
    	                                          Empleado.Estado estado, 
    	                                          String busqueda);
}
