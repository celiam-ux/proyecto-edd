/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author Eliam
 */
public class NodoPrioridad {

    private final Object dato;
    private final double prioridad;
    private NodoPrioridad pNext;

    public NodoPrioridad(Object dato, double prioridad) {
        if (!Double.isFinite(prioridad)) {
            throw new IllegalArgumentException("La prioridad debe ser un numero finito");
        }
        this.dato = dato;
        this.prioridad = prioridad;
        this.pNext = null;
    }

    public Object getDato() {
        return this.dato;
    }

    // La prioridad no cambia mientras el nodo esta encolado.
    public double getPrioridad() {
        return this.prioridad;
    }

    public NodoPrioridad getpNext() {
        return this.pNext;
    }

    public void setpNext(NodoPrioridad pNext) {
        this.pNext = pNext;
    }
}
