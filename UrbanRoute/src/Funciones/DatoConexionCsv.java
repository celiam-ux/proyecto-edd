/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

/**
 * Representa una fila validada del archivo CSV de conexiones.
 *
 * @author Eliam
 */
public class DatoConexionCsv {

    private final String idOrigen;
    private final String idDestino;
    private final int tiempoMinutos;

    /**
     * Crea una conexión entre dos estaciones diferentes.
     *
     * @param idOrigen identificador de origen
     * @param idDestino identificador de destino
     * @param tiempoMinutos tiempo positivo de traslado
     */
    public DatoConexionCsv(String idOrigen, String idDestino, int tiempoMinutos) {
        this.idOrigen = this.validarTexto(idOrigen, "origen");
        this.idDestino = this.validarTexto(idDestino, "destino");
        if (this.idOrigen.equals(this.idDestino)) {
            throw new IllegalArgumentException("Una conexión debe unir estaciones diferentes");
        }
        if (tiempoMinutos <= 0) {
            throw new IllegalArgumentException("El tiempo debe ser mayor que cero");
        }
        this.tiempoMinutos = tiempoMinutos;
    }

    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio");
        }
        return valor.trim();
    }

    /** @return identificador de la estación de origen */
    public String getIdOrigen() {
        return this.idOrigen;
    }

    /** @return identificador de la estación de destino */
    public String getIdDestino() {
        return this.idDestino;
    }

    /** @return tiempo de traslado en minutos */
    public int getTiempoMinutos() {
        return this.tiempoMinutos;
    }
}
