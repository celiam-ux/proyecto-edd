/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author Eliam
 */
public class Cola {

    private Nodo cabeza;
    private Nodo cola;
    private int size;

    public Cola() {
        this.cabeza = null;
        this.cola = null;
        this.size = 0;
    }

    // Los nodos se exponen para recorrer; los enlaces se modifican desde la cola.
    public Nodo getCabeza() {
        return this.cabeza;
    }

    public Nodo getCola() {
        return this.cola;
    }

    public int getSize() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void encolar(Object dato) {
        Nodo pNew = new Nodo(dato);
        if (this.isEmpty()) {
            this.cabeza = pNew;
        } else {
            this.cola.setpNext(pNew);
        }
        this.cola = pNew;
        this.size++;
    }

    // Una cola vacia devuelve null; isEmpty distingue un dato null encolado.
    public Object desencolar() {
        if (this.isEmpty()) {
            return null;
        }
        Nodo eliminado = this.cabeza;
        this.cabeza = eliminado.getpNext();
        eliminado.setpNext(null);
        this.size--;
        if (this.cabeza == null) {
            this.cola = null;
        }
        return eliminado.getDato();
    }

    public void destruir() {
        while (!this.isEmpty()) {
            this.desencolar();
        }
    }

    public void mostrar() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder();
        Nodo temp = this.cabeza;
        while (temp != null) {
            resultado.append(temp.getDato()).append(" -> ");
            temp = temp.getpNext();
        }
        return resultado.append("null").toString();
    }
}
