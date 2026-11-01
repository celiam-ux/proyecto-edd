/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author Eliam
 */
public class PruebaLista {

    private int verificaciones;

    public static void main(String[] args) {
        PruebaLista pruebas = new PruebaLista();
        pruebas.ejecutar();
    }

    private void ejecutar() {
        this.probarNodo();
        this.probarListaVacia();
        this.probarInserciones();
        this.probarEliminaciones();
        this.probarBusqueda();
        this.probarIndicesInvalidos();
        this.probarVaciado();
        this.probarListasIndependientes();
        System.out.println("Pruebas de Nodo y Lista: " + this.verificaciones
                + " verificaciones correctas.");
    }

    private void probarNodo() {
        Nodo nodo = new Nodo();
        this.verificar(nodo.getDato() == null, "Nodo sin dato inicial");
        this.verificar(nodo.getpNext() == null, "Nodo sin enlace inicial");
        Nodo siguiente = new Nodo("Destino");
        nodo.setDato("Origen");
        nodo.setpNext(siguiente);
        this.verificar("Origen".equals(nodo.getDato()), "Cambiar dato del nodo");
        this.verificar(nodo.getpNext() == siguiente, "Cambiar enlace del nodo");
        this.verificar("Destino".equals(siguiente.getDato()), "Constructor con dato");
        this.verificar(siguiente.getpNext() == null, "Constructor termina el enlace");
    }

    private void probarListaVacia() {
        Lista lista = new Lista();
        this.verificarLista(lista);
        this.verificar(lista.buscar("Ausente") == null, "Buscar en lista vacia");
        this.verificar(lista.buscar(null) == null, "Buscar null en lista vacia");
        this.verificar(lista.eliminarInicio() == null, "Eliminar inicio vacio");
        this.verificarLista(lista);
        this.verificar(lista.eliminarFinal() == null, "Eliminar final vacio");
        this.verificarLista(lista);
        this.verificar("null".equals(lista.toString()), "Representacion vacia");
        lista.destruir();
        this.verificarLista(lista);
    }

    private void probarInserciones() {
        Lista lista = new Lista();
        lista.insertarFinal("B");
        this.verificarLista(lista, "B");
        lista.insertarInicio("A");
        this.verificarLista(lista, "A", "B");
        lista.insertar("D", 2);
        this.verificarLista(lista, "A", "B", "D");
        lista.insertar("C", 2);
        this.verificarLista(lista, "A", "B", "C", "D");
        lista.insertar(null, 0);
        this.verificarLista(lista, null, "A", "B", "C", "D");
        this.verificar("null -> A -> B -> C -> D -> null".equals(lista.toString()),
                "Representacion en orden");
    }

    private void probarEliminaciones() {
        Lista lista = new Lista();
        lista.insertarInicio("A");
        this.verificarLista(lista, "A");
        Nodo eliminado = lista.getpFirst();
        this.verificar("A".equals(lista.eliminarFinal()), "Eliminar unico desde final");
        this.verificarLista(lista);
        this.verificar(eliminado.getpNext() == null, "Unico nodo separado");

        lista.insertarFinal("A");
        lista.insertarFinal("B");
        lista.insertarFinal("C");
        lista.insertarFinal("D");
        eliminado = lista.getpFirst().getpNext();
        this.verificar("B".equals(lista.eliminar(1)), "Eliminar posicion intermedia");
        this.verificarLista(lista, "A", "C", "D");
        this.verificar(eliminado.getpNext() == null, "Nodo intermedio separado");
        eliminado = lista.getpFirst();
        this.verificar("A".equals(lista.eliminarInicio()), "Eliminar primer elemento");
        this.verificarLista(lista, "C", "D");
        this.verificar(eliminado.getpNext() == null, "Primero separado del resto");
        this.verificar("D".equals(lista.eliminarFinal()), "Eliminar ultimo elemento");
        this.verificarLista(lista, "C");
        this.verificar("C".equals(lista.eliminarInicio()), "Eliminar unico desde inicio");
        this.verificarLista(lista);

        lista.insertarFinal(null);
        this.verificarLista(lista, (Object) null);
        this.verificar(lista.eliminar(0) == null, "Eliminar dato null por posicion");
        this.verificarLista(lista);
    }

    private void probarBusqueda() {
        Lista lista = new Lista();
        String primero = new String("Centro");
        String equivalente = new String("Centro");
        this.verificar(primero != equivalente, "Busqueda usa referencias distintas");
        lista.insertarFinal(primero);
        lista.insertarFinal(null);
        lista.insertarFinal(equivalente);
        lista.insertarFinal("Terminal");
        this.verificar(lista.buscar(equivalente) == lista.getpFirst(),
                "equals encuentra la primera coincidencia");
        this.verificar(lista.buscar(null) == lista.getpFirst().getpNext(),
                "Encontrar null entre otros datos");
        this.verificar("Terminal".equals(lista.buscar(new String("Terminal")).getDato()),
                "Encontrar ultimo por contenido");
        this.verificar(lista.buscar("Ausente") == null, "Dato ausente");
        this.verificarLista(lista, "Centro", null, "Centro", "Terminal");
    }

    private void probarIndicesInvalidos() {
        Lista lista = new Lista();
        this.verificarIndice(() -> lista.getValor(0));
        this.verificarLista(lista);
        this.verificarIndice(() -> lista.eliminar(0));
        this.verificarLista(lista);
        this.verificarIndice(() -> lista.insertar("X", -1));
        this.verificarLista(lista);
        this.verificarIndice(() -> lista.insertar("X", 1));
        this.verificarLista(lista);

        lista.insertar("A", 0);
        lista.insertar("B", 1);
        int[] indices = {-1, 2, Integer.MIN_VALUE, Integer.MAX_VALUE};
        for (int posicion : indices) {
            this.verificarIndice(() -> lista.getValor(posicion));
            this.verificarLista(lista, "A", "B");
            this.verificarIndice(() -> lista.eliminar(posicion));
            this.verificarLista(lista, "A", "B");
        }
        int[] indicesInsercion = {-1, 3, Integer.MIN_VALUE, Integer.MAX_VALUE};
        for (int posicion : indicesInsercion) {
            this.verificarIndice(() -> lista.insertar("X", posicion));
            this.verificarLista(lista, "A", "B");
        }
        this.verificar("B".equals(lista.eliminar(1)), "Eliminar ultima posicion valida");
        this.verificarLista(lista, "A");
        this.verificar("A".equals(lista.eliminar(0)), "Eliminar primera posicion valida");
        this.verificarLista(lista);
    }

    private void probarVaciado() {
        Lista lista = new Lista();
        lista.insertarFinal("A");
        lista.insertarFinal("B");
        lista.insertarFinal("C");
        Nodo primero = lista.getpFirst();
        Nodo segundo = primero.getpNext();
        Nodo ultimo = segundo.getpNext();
        lista.destruir();
        this.verificarLista(lista);
        this.verificar(primero.getpNext() == null, "Vaciado separa primer nodo");
        this.verificar(segundo.getpNext() == null, "Vaciado separa nodo intermedio");
        this.verificar(ultimo.getpNext() == null, "Vaciado deja final sin enlace");
        this.verificar(lista.buscar("A") == null, "Vaciado elimina resultados");
        this.verificarIndice(() -> lista.getValor(0));
        lista.destruir();
        this.verificarLista(lista);
        lista.insertarInicio("Nueva");
        this.verificarLista(lista, "Nueva");
        lista.destruir();
        this.verificarLista(lista);
        lista.insertarFinal("Otra");
        this.verificarLista(lista, "Otra");
    }

    private void probarListasIndependientes() {
        Lista primera = new Lista();
        Lista segunda = new Lista();
        Object dato = new Object();
        primera.insertarFinal(dato);
        segunda.insertarFinal(dato);
        this.verificar(primera.getpFirst() != segunda.getpFirst(), "Nodos independientes");
        primera.destruir();
        this.verificarLista(primera);
        this.verificarLista(segunda, dato);
    }

    // El arreglo esperado fija el orden y limita el recorrido para detectar ciclos.
    private void verificarLista(Lista lista, Object... esperados) {
        this.verificar(lista.getSize() == esperados.length, "Tamano exacto");
        this.verificar(lista.isEmpty() == (esperados.length == 0), "Estado vacio coherente");
        this.verificar((lista.getpFirst() == null) == (esperados.length == 0),
                "Cabeza coherente con el tamano");
        Nodo temp = lista.getpFirst();
        for (int posicion = 0; posicion < esperados.length; posicion++) {
            this.verificar(temp != null, "Enlace presente en posicion " + posicion);
            Object esperado = esperados[posicion];
            this.verificar(esperado == null ? temp.getDato() == null
                    : esperado.equals(temp.getDato()), "Dato enlazado en posicion " + posicion);
            Object obtenido = lista.getValor(posicion);
            this.verificar(esperado == null ? obtenido == null : esperado.equals(obtenido),
                    "Obtencion por posicion " + posicion);
            temp = temp.getpNext();
        }
        this.verificar(temp == null, "Ultimo enlace nulo, sin nodos extra ni ciclos");
    }

    private void verificarIndice(Runnable operacion) {
        try {
            operacion.run();
        } catch (IndexOutOfBoundsException excepcion) {
            this.verificar(true, "Indice invalido rechazado");
            return;
        }
        throw new AssertionError("Se acepto un indice invalido");
    }

    private void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
        this.verificaciones++;
    }
}
