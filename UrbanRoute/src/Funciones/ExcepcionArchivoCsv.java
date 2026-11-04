/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

/**
 * Describe un error de formato que la interfaz debe mostrar al usuario.
 *
 * @author Eliam
 */
public class ExcepcionArchivoCsv extends Exception {

    /**
     * Crea un error con un mensaje apto para mostrar en la interfaz.
     *
     * @param mensaje explicación del problema
     */
    public ExcepcionArchivoCsv(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea un error de archivo conservando su causa técnica.
     *
     * @param mensaje explicación para el usuario
     * @param causa error original
     */
    public ExcepcionArchivoCsv(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
