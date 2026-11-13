/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

import algoritmos.Conectividad;
import algoritmos.Dijkstra;
import estructuras.Estacion;
import estructuras.Grafo;
import estructuras.ListaPropia;
import estructuras.Nodo;

/**
 * Adapta los resultados del grafo a las consultas de la sesión CSV.
 *
 * @author Eliam
 */
public class ConsultasRed {

    private final SesionCsv sesion;

    public ConsultasRed(SesionCsv sesion) {
        this.sesion = sesion;
    }

    public String consultarRuta(String origen, String destino) {
        if (origen == null || destino == null || origen.isBlank() || destino.isBlank()) {
            throw new IllegalArgumentException("Selecciona una estación de origen y una de destino.");
        }
        if (origen.equals(destino)) {
            throw new IllegalArgumentException("El origen y el destino deben ser estaciones distintas.");
        }
        Grafo grafo = this.sesion.crearGrafo();
        if (!grafo.existeEstacion(origen) || !grafo.existeEstacion(destino)) {
            throw new IllegalArgumentException("El origen y el destino deben estar en la red actual.");
        }
        Dijkstra dijkstra = new Dijkstra(grafo);
        ListaPropia<String> ruta = dijkstra.rutaMasRapida(origen, destino);
        if (ruta == null) {
            return "No hay una ruta entre " + this.etiquetaEstacion(grafo, origen)
                    + " y " + this.etiquetaEstacion(grafo, destino) + ".";
        }
        StringBuilder resultado = new StringBuilder("Tiempo total: ")
                .append(dijkstra.getTiempoTotal()).append(" min\n\nEstaciones en orden:\n");
        this.agregarEstaciones(resultado, grafo, ruta);
        return resultado.toString();
    }

    public String consultarSubredes() {
        Grafo grafo = this.sesion.crearGrafo();
        ListaPropia<ListaPropia<String>> subredes = new Conectividad(grafo).obtenerSubredes();
        StringBuilder resultado = new StringBuilder("Subredes encontradas: ").append(subredes.tamano());
        int numero = 1;
        for (Nodo<ListaPropia<String>> nodo = subredes.getCabeza(); nodo != null; nodo = nodo.getSiguiente()) {
            ListaPropia<String> estaciones = nodo.getInfo();
            resultado.append("\n\nSubred ").append(numero++).append(" (")
                    .append(estaciones.tamano()).append(estaciones.tamano() == 1 ? " estación):\n" : " estaciones):\n");
            this.agregarEstaciones(resultado, grafo, estaciones);
        }
        return resultado.toString();
    }

    public String consultarHubs() {
        Grafo grafo = this.sesion.crearGrafo();
        ListaPropia<String> ids = grafo.getIdsEstaciones();
        int gradoMaximo = 0;
        ListaPropia<String> hubs = new ListaPropia<>();
        for (Nodo<String> nodo = ids.getCabeza(); nodo != null; nodo = nodo.getSiguiente()) {
            String id = nodo.getInfo();
            int grado = grafo.getGrado(id);
            if (grado > gradoMaximo) {
                gradoMaximo = grado;
                hubs.vaciar();
            }
            if (grado > 0 && grado == gradoMaximo) {
                hubs.agregarFinal(id);
            }
        }
        if (hubs.esVacia()) {
            return "No hay hubs porque ninguna estación tiene conexiones directas.";
        }
        StringBuilder resultado = new StringBuilder("Máximo de conexiones directas por estación: ")
                .append(gradoMaximo).append("\n\n");
        this.agregarEstaciones(resultado, grafo, hubs);
        return resultado.toString();
    }

    private void agregarEstaciones(StringBuilder resultado, Grafo grafo, ListaPropia<String> ids) {
        for (Nodo<String> nodo = ids.getCabeza(); nodo != null; nodo = nodo.getSiguiente()) {
            resultado.append("• ").append(this.etiquetaEstacion(grafo, nodo.getInfo()));
            if (nodo.getSiguiente() != null) {
                resultado.append('\n');
            }
        }
    }

    private String etiquetaEstacion(Grafo grafo, String id) {
        Estacion estacion = grafo.getEstacion(id);
        return estacion.getNombre() + " (" + id + ")";
    }
}
