/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

/**
 * Agrupa una copia segura de las estaciones y conexiones cargadas desde CSV.
 *
 * @author Eliam
 */
public class DatosCsv {

    private final DatoEstacionCsv[] estaciones;
    private final DatoConexionCsv[] conexiones;

    /**
     * Crea el conjunto de datos que la interfaz puede mostrar y modificar.
     *
     * @param estaciones filas de estaciones
     * @param conexiones filas de conexiones
     */
    public DatosCsv(DatoEstacionCsv[] estaciones, DatoConexionCsv[] conexiones) {
        if (estaciones == null || conexiones == null) {
            throw new IllegalArgumentException("Los arreglos de datos son obligatorios");
        }
        this.estaciones = this.copiar(estaciones);
        this.conexiones = this.copiar(conexiones);
    }

    private DatoEstacionCsv[] copiar(DatoEstacionCsv[] origen) {
        DatoEstacionCsv[] copia = new DatoEstacionCsv[origen.length];
        System.arraycopy(origen, 0, copia, 0, origen.length);
        return copia;
    }

    private DatoConexionCsv[] copiar(DatoConexionCsv[] origen) {
        DatoConexionCsv[] copia = new DatoConexionCsv[origen.length];
        System.arraycopy(origen, 0, copia, 0, origen.length);
        return copia;
    }

    /** @return copia de las estaciones cargadas */
    public DatoEstacionCsv[] getEstaciones() {
        return this.copiar(this.estaciones);
    }

    /** @return copia de las conexiones cargadas */
    public DatoConexionCsv[] getConexiones() {
        return this.copiar(this.conexiones);
    }

    /** @return cantidad de estaciones */
    public int getCantidadEstaciones() {
        return this.estaciones.length;
    }

    /** @return cantidad de conexiones */
    public int getCantidadConexiones() {
        return this.conexiones.length;
    }
}
