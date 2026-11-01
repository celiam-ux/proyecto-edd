/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author Eliam
 */
public class Nodo {

    private Object dato;
    private Nodo pNext;

    public Nodo() {
        this(null);
    }

    public Nodo(Object dato) {
        this.dato = dato;
        this.pNext = null;
    }

    public Object getDato() {
        return this.dato;
    }

    public void setDato(Object dato) {
        this.dato = dato;
    }

    public Nodo getpNext() {
        return this.pNext;
    }

    public void setpNext(Nodo pNext) {
        this.pNext = pNext;
    }
}
