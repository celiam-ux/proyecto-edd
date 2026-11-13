/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 * Arista ponderada de la lista de adyacencia. Representa una conexión directa
 * hacia una estación destino con un peso asociado, que en la red de transporte
 * modela el tiempo de traslado en minutos.
 *
 * @author Jorge
 */
public class Arista {
    private String sDestino;
    private int iPeso;

    /**
     * Crea una arista hacia la estación destino con el peso indicado.
     *
     * @param sDestino identificador de la estación destino
     * @param iPeso    peso de la conexión (tiempo de traslado en minutos)
     */
    public Arista(String sDestino, int iPeso) {
        this.sDestino = sDestino;
        this.iPeso = iPeso;
    }

    /**
     * @return el identificador de la estación destino
     */
    public String getDestino() {
        return sDestino;
    }

    /**
     * @return el peso de la conexión
     */
    public int getPeso() {
        return iPeso;
    }

    /**
     * @param iPeso nuevo peso de la conexión
     */
    public void setPeso(int iPeso) {
        this.iPeso = iPeso;
    }

    /**
     * Dos aristas se consideran iguales cuando apuntan al mismo destino, con
     * independencia del peso. Así la {@link ListaPropia} puede localizar y
     * eliminar una conexión por su estación destino.
     *
     * @param obj objeto a comparar
     * @return {@code true} si ambas aristas comparten el mismo destino
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Arista otra = (Arista) obj;
        return this.sDestino.equals(otra.sDestino);
    }

    /**
     * @return el código hash derivado del destino
     */
    @Override
    public int hashCode() {
        return this.sDestino.hashCode();
    }
}
