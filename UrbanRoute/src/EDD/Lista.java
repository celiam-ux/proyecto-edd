/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author Eliam
 */
public class Lista {

    private Nodo pFirst;
    private int size;

    public Lista() {
        this.pFirst = null;
        this.size = 0;
    }

    // Los nodos se exponen para recorrer; los enlaces se modifican desde la lista.
    public Nodo getpFirst() {
        return this.pFirst;
    }

    public int getSize() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void insertarInicio(Object dato) {
        this.insertar(dato, 0);
    }

    public void insertarFinal(Object dato) {
        this.insertar(dato, this.size);
    }

    // Las posiciones comienzan en cero; insertar en size agrega al final.
    public void insertar(Object dato, int posicion) {
        if (posicion < 0 || posicion > this.size) {
            throw new IndexOutOfBoundsException("Indice invalido para insertar: " + posicion);
        }
        Nodo pNew = new Nodo(dato);
        if (posicion == 0) {
            pNew.setpNext(this.pFirst);
            this.pFirst = pNew;
        } else {
            Nodo anterior = this.obtenerNodo(posicion - 1);
            pNew.setpNext(anterior.getpNext());
            anterior.setpNext(pNew);
        }
        this.size++;
    }

    // Eliminar un extremo de una lista vacia devuelve null.
    public Object eliminarInicio() {
        return this.isEmpty() ? null : this.eliminar(0);
    }

    public Object eliminarFinal() {
        return this.isEmpty() ? null : this.eliminar(this.size - 1);
    }

    public Object eliminar(int posicion) {
        Nodo eliminado = this.obtenerNodo(posicion);
        if (posicion == 0) {
            this.pFirst = eliminado.getpNext();
        } else {
            Nodo anterior = this.obtenerNodo(posicion - 1);
            anterior.setpNext(eliminado.getpNext());
        }
        eliminado.setpNext(null);
        this.size--;
        return eliminado.getDato();
    }

    // Devuelve la primera coincidencia por contenido, incluyendo datos null.
    public Nodo buscar(Object dato) {
        Nodo temp = this.pFirst;
        while (temp != null) {
            Object valor = temp.getDato();
            if (valor == null ? dato == null : valor.equals(dato)) {
                return temp;
            }
            temp = temp.getpNext();
        }
        return null;
    }

    public Object getValor(int posicion) {
        return this.obtenerNodo(posicion).getDato();
    }

    private Nodo obtenerNodo(int posicion) {
        if (posicion < 0 || posicion >= this.size) {
            throw new IndexOutOfBoundsException("Indice invalido: " + posicion
                    + ". Tamano de la lista: " + this.size);
        }
        Nodo temp = this.pFirst;
        for (int indice = 0; indice < posicion; indice++) {
            temp = temp.getpNext();
        }
        return temp;
    }

    public void destruir() {
        Nodo temp = this.pFirst;
        while (temp != null) {
            Nodo siguiente = temp.getpNext();
            temp.setpNext(null);
            temp = siguiente;
        }
        this.pFirst = null;
        this.size = 0;
    }

    public void mostrar() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder();
        Nodo temp = this.pFirst;
        while (temp != null) {
            resultado.append(temp.getDato()).append(" -> ");
            temp = temp.getpNext();
        }
        return resultado.append("null").toString();
    }
}
