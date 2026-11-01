/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author Eliam
 */
public class ColaPrioridad {

    private NodoPrioridad cabeza;
    private NodoPrioridad cola;
    private int size;

    public ColaPrioridad() {
        this.cabeza = null;
        this.cola = null;
        this.size = 0;
    }

    // Los nodos se exponen para recorrer; los enlaces se modifican desde la cola.
    public NodoPrioridad getCabeza() {
        return this.cabeza;
    }

    public NodoPrioridad getCola() {
        return this.cola;
    }

    public int getSize() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    // Insercion O(n), extraccion O(1). Menor valor significa mayor prioridad.
    public void encolar(Object dato, double prioridad) {
        NodoPrioridad pNew = new NodoPrioridad(dato, prioridad);
        if (this.isEmpty()) {
            this.cabeza = pNew;
            this.cola = pNew;
        } else if (prioridad < this.cabeza.getPrioridad()) {
            pNew.setpNext(this.cabeza);
            this.cabeza = pNew;
        } else {
            NodoPrioridad anterior = this.cabeza;
            // Avanzar sobre los empates conserva su orden de llegada.
            while (anterior.getpNext() != null
                    && anterior.getpNext().getPrioridad() <= prioridad) {
                anterior = anterior.getpNext();
            }
            pNew.setpNext(anterior.getpNext());
            anterior.setpNext(pNew);
            if (pNew.getpNext() == null) {
                this.cola = pNew;
            }
        }
        this.size++;
    }

    // Se permiten datos repetidos con prioridades distintas y datos null.
    public Object desencolar() {
        if (this.isEmpty()) {
            return null;
        }
        NodoPrioridad eliminado = this.cabeza;
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
        NodoPrioridad temp = this.cabeza;
        while (temp != null) {
            resultado.append(temp.getDato()).append(" (")
                    .append(temp.getPrioridad()).append(") -> ");
            temp = temp.getpNext();
        }
        return resultado.append("null").toString();
    }
}
