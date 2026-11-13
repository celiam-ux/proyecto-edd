/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

import algoritmos.Dijkstra;

/**
 * Comprueba el contrato mínimo que necesita la interfaz para integrar la red.
 *
 * @author Eliam
 */
public class PruebaGrafoEquipo {

    private static int verificaciones;

    /** Ejecuta las pruebas sin depender de una biblioteca externa. */
    public static void main(String[] args) {
        probarTablaHash();
        probarGrafoYRuta();
        System.out.println("Grafo del equipo: " + verificaciones + " verificaciones correctas.");
    }

    private static void probarTablaHash() {
        TablaHash tabla = new TablaHash();
        for (int indice = 0; indice < 40; indice++) {
            String id = "E" + indice;
            tabla.insertar(id, new Estacion(id, "Estación " + indice,
                    "Línea 1", "Centro", 1000 + indice));
        }
        verificar(tabla.tamano() == 40, "La tabla conserva todas las estaciones al crecer");
        verificar("Estación 25".equals(tabla.obtener("E25").getNombre()),
                "La tabla recupera una estación por su ID");
        verificar(tabla.eliminar("E25"), "La tabla elimina una estación existente");
        verificar(!tabla.contiene("E25"), "La estación eliminada deja de estar indexada");
    }

    private static void probarGrafoYRuta() {
        TablaHash tabla = new TablaHash();
        Grafo grafo = new Grafo(tabla);
        grafo.agregarEstacion(new Estacion("A", "Alfa", "Línea 1", "Centro", 1000));
        grafo.agregarEstacion(new Estacion("B", "Beta", "Línea 1", "Centro", 1000));
        grafo.agregarEstacion(new Estacion("C", "Gamma", "Línea 2", "Este", 1000));
        grafo.agregarArista("A", "B", 2);
        grafo.agregarArista("B", "C", 3);
        grafo.agregarArista("A", "C", 10);

        verificar(grafo.cantidadEstaciones() == 3, "El grafo registra sus vértices");
        verificar(grafo.getGrado("A") == 2, "El grafo informa el grado de una estación");

        Dijkstra dijkstra = new Dijkstra(grafo);
        ListaPropia<String> ruta = dijkstra.rutaMasRapida("A", "C");
        verificar(ruta != null && ruta.tamano() == 3, "Dijkstra devuelve la ruta completa");
        verificar("A".equals(ruta.obtener(0)) && "B".equals(ruta.obtener(1))
                && "C".equals(ruta.obtener(2)), "Dijkstra elige el trayecto de menor tiempo");
        verificar(dijkstra.getTiempoTotal() == 5, "Dijkstra acumula el tiempo mínimo");

        verificar(grafo.eliminarEstacion("B"), "El grafo elimina una estación existente");
        verificar(!grafo.existeEstacion("B") && grafo.getGrado("A") == 1,
                "Eliminar una estación retira también sus conexiones");
    }

    private static void verificar(boolean condicion, String mensaje) {
        verificaciones++;
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
