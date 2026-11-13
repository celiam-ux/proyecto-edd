/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 * Lista enlazada simple genérica implementada desde cero, sin utilizar el
 * Java Collections Framework. Mantiene referencias a la cabeza y a la cola para
 * que las inserciones al final se resuelvan en O(1).
 *
 * <p>Es la estructura de propósito general del núcleo: la {@link TablaHash} la
 * usa para las cadenas de colisión y el {@link Grafo} la usa para las listas de
 * adyacencia y para devolver conjuntos de resultados a los demás módulos.</p>
 *
 * @author Jorge
 * @param <T> tipo de dato almacenado en la lista
 */
public class ListaPropia<T> {
    private Nodo<T> pCabeza;
    private Nodo<T> pCola;
    private int iN;

    /**
     * Construye una lista vacía.
     */
    public ListaPropia() {
        this.pCabeza = null;
        this.pCola = null;
        this.iN = 0;
    }

    /**
     * @return {@code true} si la lista no contiene elementos
     */
    public boolean esVacia() {
        return this.iN == 0;
    }

    /**
     * @return la cantidad de elementos almacenados
     */
    public int tamano() {
        return this.iN;
    }

    /**
     * Inserta un elemento al inicio de la lista en O(1).
     *
     * @param x elemento a insertar
     */
    public void agregarInicio(T x) {
        Nodo<T> pNew = new Nodo<>(x);
        pNew.setSiguiente(this.pCabeza);
        this.pCabeza = pNew;
        if (this.pCola == null) {
            this.pCola = pNew;
        }
        this.iN++;
    }

    /**
     * Inserta un elemento al final de la lista en O(1).
     *
     * @param x elemento a insertar
     */
    public void agregarFinal(T x) {
        Nodo<T> pNew = new Nodo<>(x);
        if (this.pCabeza == null) {
            this.pCabeza = pNew;
            this.pCola = pNew;
        } else {
            this.pCola.setSiguiente(pNew);
            this.pCola = pNew;
        }
        this.iN++;
    }

    /**
     * Retorna el elemento ubicado en la posición indicada (base 0).
     *
     * @param iPos posición solicitada
     * @return el elemento en esa posición
     * @throws IndexOutOfBoundsException si la posición está fuera de rango
     */
    public T obtener(int iPos) {
        if (iPos < 0 || iPos >= this.iN) {
            throw new IndexOutOfBoundsException("Posición fuera de rango: " + iPos);
        }
        Nodo<T> pAux = this.pCabeza;
        for (int i = 0; i < iPos; i++) {
            pAux = pAux.getSiguiente();
        }
        return pAux.getInfo();
    }

    /**
     * Indica si el elemento dado se encuentra en la lista, comparando mediante
     * {@code equals}.
     *
     * @param x elemento buscado
     * @return {@code true} si el elemento está presente
     */
    public boolean contiene(T x) {
        Nodo<T> pAux = this.pCabeza;
        while (pAux != null) {
            if (pAux.getInfo().equals(x)) {
                return true;
            }
            pAux = pAux.getSiguiente();
        }
        return false;
    }

    /**
     * Elimina la primera aparición del elemento indicado.
     *
     * @param x elemento a eliminar
     * @return {@code true} si se eliminó algún elemento
     */
    public boolean eliminar(T x) {
        Nodo<T> pAnt = null;
        Nodo<T> pAux = this.pCabeza;
        while (pAux != null) {
            if (pAux.getInfo().equals(x)) {
                if (pAnt == null) {
                    this.pCabeza = pAux.getSiguiente();
                } else {
                    pAnt.setSiguiente(pAux.getSiguiente());
                }
                if (pAux == this.pCola) {
                    this.pCola = pAnt;
                }
                this.iN--;
                return true;
            }
            pAnt = pAux;
            pAux = pAux.getSiguiente();
        }
        return false;
    }

    /**
     * Elimina todos los elementos de la lista, dejándola vacía.
     */
    public void vaciar() {
        this.pCabeza = null;
        this.pCola = null;
        this.iN = 0;
    }

    /**
     * Retorna la cabeza de la lista para recorridos externos. Se expone de forma
     * controlada para que los módulos de lógica (recorridos BFS/DFS, Dijkstra)
     * puedan iterar nodo a nodo sin copiar la estructura.
     *
     * @return el primer nodo, o {@code null} si la lista está vacía
     */
    public Nodo<T> getCabeza() {
        return this.pCabeza;
    }
}
