/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 * Cola dinámica genérica (FIFO) implementada desde cero, sin utilizar el Java
 * Collections Framework. Tanto el encolar como el desencolar operan en O(1)
 * gracias a que se mantiene una referencia directa al frente y al final.
 *
 * <p>La usa el módulo de lógica para el recorrido en anchura (BFS) empleado en
 * la detección de subredes o componentes conexos.</p>
 *
 * @author Jorge
 * @param <T> tipo de dato almacenado en la cola
 */
public class ColaPropia<T> {
    private Nodo<T> pFrente;
    private Nodo<T> pFinal;
    private int iN;

    /**
     * Construye una cola vacía.
     */
    public ColaPropia() {
        this.pFrente = null;
        this.pFinal = null;
        this.iN = 0;
    }

    /**
     * @return {@code true} si la cola no contiene elementos
     */
    public boolean esVacia() {
        return this.iN == 0;
    }

    /**
     * @return la cantidad de elementos encolados
     */
    public int tamano() {
        return this.iN;
    }

    /**
     * Inserta un elemento al final de la cola.
     *
     * @param x elemento a encolar
     */
    public void encolar(T x) {
        Nodo<T> pNew = new Nodo<>(x);
        if (this.pFinal == null) {
            this.pFrente = pNew;
            this.pFinal = pNew;
        } else {
            this.pFinal.setSiguiente(pNew);
            this.pFinal = pNew;
        }
        this.iN++;
    }

    /**
     * Extrae y retorna el elemento del frente de la cola.
     *
     * @return el elemento que estaba en el frente
     * @throws IllegalStateException si la cola está vacía
     */
    public T desencolar() {
        if (this.esVacia()) {
            throw new IllegalStateException("Precondición violada: La cola está vacía.");
        }
        T elemento = this.pFrente.getInfo();
        this.pFrente = this.pFrente.getSiguiente();
        if (this.pFrente == null) {
            this.pFinal = null;
        }
        this.iN--;
        return elemento;
    }

    /**
     * Consulta el elemento del frente sin extraerlo.
     *
     * @return el elemento ubicado en el frente
     * @throws IllegalStateException si la cola está vacía
     */
    public T frente() {
        if (this.esVacia()) {
            throw new IllegalStateException("Precondición violada: La cola está vacía.");
        }
        return this.pFrente.getInfo();
    }
}
