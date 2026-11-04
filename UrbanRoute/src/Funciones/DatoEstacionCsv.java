/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

/**
 * Representa una fila validada del archivo CSV de estaciones.
 *
 * @author Eliam
 */
public class DatoEstacionCsv {

    private final String id;
    private final String nombre;
    private final String linea;
    private final String zona;
    private final int capacidadPasajerosHora;

    /**
     * Crea una fila de estación lista para mostrarse o guardarse.
     *
     * @param id identificador único de la estación
     * @param nombre nombre visible
     * @param linea línea de transporte
     * @param zona zona de la ciudad
     * @param capacidadPasajerosHora capacidad no negativa
     */
    public DatoEstacionCsv(String id, String nombre, String linea, String zona,
            int capacidadPasajerosHora) {
        this.id = this.validarTexto(id, "ID");
        this.nombre = this.validarTexto(nombre, "nombre");
        this.linea = this.validarTexto(linea, "línea");
        this.zona = this.validarTexto(zona, "zona");
        if (capacidadPasajerosHora < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa");
        }
        this.capacidadPasajerosHora = capacidadPasajerosHora;
    }

    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio");
        }
        return valor.trim();
    }

    /** @return identificador único de la estación */
    public String getId() {
        return this.id;
    }

    /** @return nombre visible de la estación */
    public String getNombre() {
        return this.nombre;
    }

    /** @return línea de transporte */
    public String getLinea() {
        return this.linea;
    }

    /** @return zona de la ciudad */
    public String getZona() {
        return this.zona;
    }

    /** @return capacidad de pasajeros por hora */
    public int getCapacidadPasajerosHora() {
        return this.capacidadPasajerosHora;
    }
}
