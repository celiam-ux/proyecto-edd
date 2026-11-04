/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

import java.io.File;

/**
 * Conserva el par de CSV y los cambios pendientes. Valida cada edición antes de
 * publicarla, con las mismas reglas que usa GestorArchivo al guardar.
 *
 * @author Eliam
 */
public class SesionCsv {

    private final GestorArchivo gestor = new GestorArchivo();
    private DatosCsv datos = new DatosCsv(new DatoEstacionCsv[0], new DatoConexionCsv[0]);
    private boolean cambios;

    /** Carga el par completo. Si falla la validación, conserva la sesión anterior. */
    public void cargar(File estaciones, File conexiones) throws ExcepcionArchivoCsv {
        this.datos = this.gestor.cargar(estaciones, conexiones);
        this.cambios = false;
    }

    /** Guarda en el par cargado y limpia el indicador solo si termina correctamente. */
    public void guardar() throws ExcepcionArchivoCsv {
        this.gestor.guardar(this.datos);
        this.cambios = false;
    }

    /** @return filas vigentes de ambas tablas */
    public DatosCsv getDatos() {
        return this.datos;
    }

    /** @return si hay cambios pendientes de guardar */
    public boolean tieneCambios() {
        return this.cambios;
    }

    /** @return archivo de estaciones o null si todavía no hay sesión */
    public File getArchivoEstaciones() {
        return this.gestor.getArchivoEstacionesActual();
    }

    /** @return archivo de conexiones o null si todavía no hay sesión */
    public File getArchivoConexiones() {
        return this.gestor.getArchivoConexionesActual();
    }

    /**
     * Agrega una estación con índice -1, modifica la fila indicada o la elimina
     * cuando la estación es null. Una estación referenciada debe conservarse.
     */
    public void editarEstacion(int indice, DatoEstacionCsv estacion) throws ExcepcionArchivoCsv {
        this.validarSesion();
        DatoEstacionCsv[] anteriores = this.datos.getEstaciones();
        DatoConexionCsv[] conexiones = this.datos.getConexiones();
        int cantidad = this.cantidadTrasEdicion(anteriores.length, indice, estacion);
        if (estacion == null) {
            String id = anteriores[indice].getId();
            for (DatoConexionCsv conexion : conexiones) {
                if (id.equals(conexion.getIdOrigen()) || id.equals(conexion.getIdDestino())) {
                    throw new ExcepcionArchivoCsv("Retira primero las conexiones de la estación " + id + ".");
                }
            }
        }
        DatoEstacionCsv[] estaciones = new DatoEstacionCsv[cantidad];
        this.copiarEdicion(anteriores, estaciones, indice, estacion);
        this.aplicar(estaciones, conexiones);
    }

    /**
     * Agrega una conexión con índice -1, modifica la fila indicada o la elimina
     * cuando la conexión es null. Los extremos deben existir en la sesión.
     */
    public void editarConexion(int indice, DatoConexionCsv conexion) throws ExcepcionArchivoCsv {
        this.validarSesion();
        DatoConexionCsv[] anteriores = this.datos.getConexiones();
        DatoConexionCsv[] conexiones = new DatoConexionCsv[
                this.cantidadTrasEdicion(anteriores.length, indice, conexion)];
        this.copiarEdicion(anteriores, conexiones, indice, conexion);
        this.aplicar(this.datos.getEstaciones(), conexiones);
    }

    private void validarSesion() throws ExcepcionArchivoCsv {
        if (this.getArchivoEstaciones() == null) {
            throw new ExcepcionArchivoCsv("Carga ambos archivos CSV para editar la red.");
        }
    }

    private int cantidadTrasEdicion(int cantidad, int indice, Object fila) {
        if (indice == -1 && fila != null) {
            return cantidad + 1;
        }
        if (indice < 0 || indice >= cantidad) {
            throw new IllegalArgumentException("Selecciona una fila válida.");
        }
        return fila == null ? cantidad - 1 : cantidad;
    }

    private <T> void copiarEdicion(T[] anteriores, T[] nuevas, int indice, T fila) {
        if (indice == -1) {
            System.arraycopy(anteriores, 0, nuevas, 0, anteriores.length);
            nuevas[anteriores.length] = fila;
        } else if (fila == null) {
            System.arraycopy(anteriores, 0, nuevas, 0, indice);
            System.arraycopy(anteriores, indice + 1, nuevas, indice, nuevas.length - indice);
        } else {
            System.arraycopy(anteriores, 0, nuevas, 0, anteriores.length);
            nuevas[indice] = fila;
        }
    }

    private void aplicar(DatoEstacionCsv[] estaciones, DatoConexionCsv[] conexiones)
            throws ExcepcionArchivoCsv {
        this.gestor.validarConjunto(estaciones, conexiones);
        this.datos = new DatosCsv(estaciones, conexiones);
        this.cambios = true;
    }
}
