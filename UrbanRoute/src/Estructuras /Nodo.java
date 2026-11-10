/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 * Nodo genérico simplemente enlazado. Es la celda básica a partir de la cual se
 * construyen la {@link ListaPropia} y la {@link ColaPropia}.
 *
 * @author Jorge
 * @param <T> tipo de dato almacenado en el nodo
 */
public class Nodo<T> {
    private T info;
    private Nodo<T> pNext;

    /**
     * Crea un nodo que contiene el elemento indicado y sin sucesor.
     *
     * @param info elemento a almacenar
     */
    public Nodo(T info) {
        this.info = info;
        this.pNext = null;
    }

    /**
     * @return el elemento almacenado en el nodo
     */
    public T getInfo() {
        return info;
    }

    /**
     * @param info nuevo elemento a almacenar
     */
    public void setInfo(T info) {
        this.info = info;
    }

    /**
     * @return el nodo siguiente, o {@code null} si es el último
     */
    public Nodo<T> getSiguiente() {
        return pNext;
    }

    /**
     * @param siguiente nodo que pasa a ser el sucesor de éste
     */
    public void setSiguiente(Nodo<T> siguiente) {
        this.pNext = siguiente;
    }
}
