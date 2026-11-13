/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

import estructuras.Arista;
import estructuras.Estacion;
import estructuras.Grafo;
import estructuras.ListaPropia;
import java.nio.file.Files;
import java.nio.file.Path;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.GraphReplay;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.stylesheet.Color;

/**
 * Verifica el controlador sin ventanas, el guardado de las ediciones y la
 * proyección de las filas al visor y al grafo del equipo.
 *
 * @author Eliam
 */
public class PruebaSesionCsv {

    /** Ejecuta las comprobaciones con los CSV del proyecto y un par temporal. */
    public static void main(String[] args) throws Exception {
        PruebaSesionCsv prueba = new PruebaSesionCsv();
        prueba.probarMapaDeEjemplo();
        Path carpeta = Files.createTempDirectory("urbanroute-sesion-");
        Path estaciones = carpeta.resolve("estaciones.csv");
        Path conexiones = carpeta.resolve("conexiones.csv");
        Path invalido = carpeta.resolve("invalido.csv");
        try {
            Files.writeString(estaciones, "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\n"
                    + "A,Central,L1,Centro,100\nB,Norte,L1,Norte,200\n");
            Files.writeString(conexiones, "Estacion_Origen,Estacion_Destino,Tiempo_Minutos\nA,B,7\n");
            Files.writeString(invalido, "Estacion_Origen,Estacion_Destino,Tiempo_Minutos\nA,Z,7\n");
            prueba.probar(estaciones, conexiones, invalido);
        } finally {
            Files.deleteIfExists(invalido);
            Files.deleteIfExists(conexiones);
            Files.deleteIfExists(estaciones);
            Files.deleteIfExists(carpeta);
        }
    }

    private void probar(Path estaciones, Path conexiones, Path invalido) throws Exception {
        this.probarCamposInvalidos();
        SesionCsv sesion = new SesionCsv();
        DatoEstacionCsv nueva = new DatoEstacionCsv("C", "Sur, terminal", "L2", "Sur", 0);
        this.verificarRechazo(() -> sesion.editarEstacion(-1, nueva), "Carga ambos");
        sesion.cargar(estaciones.toFile(), conexiones.toFile());
        Graph redInicial = new MostrarRed().crearRed(sesion.getDatos());
        this.probarGrafoCargado(sesion);

        this.verificarRechazo(() -> sesion.editarEstacion(-1,
                new DatoEstacionCsv("A", "Duplicada", "L1", "Norte", 0)), "repetido");
        this.verificarRechazo(() -> sesion.editarEstacion(1, null), "Retira primero");
        this.verificarRechazo(() -> sesion.editarConexion(0,
                new DatoConexionCsv("A", "C", 5)), "inexistente");
        this.verificar(!sesion.tieneCambios(), "Las ediciones inválidas no ensucian la sesión.");
        this.verificarTramo(sesion.crearGrafo(), "A", "B", 7);

        sesion.editarEstacion(-1, nueva);
        Grafo grafo = sesion.crearGrafo();
        this.verificar(grafo.cantidadEstaciones() == 3 && grafo.getGrado("C") == 0
                && grafo.getTablaHash().contiene("C"), "El alta incorpora una estación aislada.");

        sesion.editarEstacion(0, new DatoEstacionCsv("A", "Centro", "L2", "Oeste", 120));
        Estacion actualizada = sesion.crearGrafo().getEstacion("A");
        this.verificar(actualizada != null && "Centro".equals(actualizada.getNombre())
                && "L2".equals(actualizada.getLinea()) && "Oeste".equals(actualizada.getZona())
                && actualizada.getCapacidad() == 120, "El grafo y el índice reflejan la edición de la estación.");

        sesion.editarConexion(0, new DatoConexionCsv("A", "C", 5));
        grafo = sesion.crearGrafo();
        this.verificarTramo(grafo, "A", "C", 5);
        this.verificar(grafo.getGrado("B") == 0, "Cambiar un extremo retira el tramo anterior.");

        sesion.editarConexion(-1, new DatoConexionCsv("B", "C", 9));
        this.verificarTramo(sesion.crearGrafo(), "B", "C", 9);
        sesion.editarConexion(1, new DatoConexionCsv("B", "C", 6));
        this.verificarTramo(sesion.crearGrafo(), "B", "C", 6);
        sesion.editarConexion(1, null);
        grafo = sesion.crearGrafo();
        this.verificar(grafo.getGrado("B") == 0 && grafo.getGrado("C") == 1,
                "La baja retira la conexión de sus dos extremos.");
        this.verificarTramo(grafo, "A", "C", 5);

        sesion.editarConexion(-1, new DatoConexionCsv("A", "C", 4));
        grafo = sesion.crearGrafo();
        this.verificarTramo(grafo, "A", "C", 5);
        this.verificar(grafo.getGrado("A") == 1 && grafo.getGrado("C") == 1,
                "El grafo conserva un solo tramo para las filas paralelas.");
        sesion.editarEstacion(1, null);
        grafo = sesion.crearGrafo();
        this.verificar(grafo.cantidadEstaciones() == 2 && !grafo.existeEstacion("B")
                && !grafo.getTablaHash().contiene("B"), "La baja retira la estación del grafo y del índice.");

        this.verificarRechazo(() -> sesion.cargar(estaciones.toFile(), invalido.toFile()), "inexistente");
        this.verificar(sesion.tieneCambios() && sesion.getArchivoConexiones().equals(conexiones.toFile()),
                "La carga inválida conserva archivos y cambios pendientes.");
        this.verificarTramo(sesion.crearGrafo(), "A", "C", 5);
        this.probarRepresentacion(redInicial, sesion.getDatos());
        sesion.editarConexion(0, null);
        this.verificarTramo(sesion.crearGrafo(), "A", "C", 4);
        this.probarGuardadoFallido(sesion, estaciones, conexiones);
        sesion.guardar();
        this.verificar(!sesion.tieneCambios(), "Guardar limpia el indicador.");
        DatosCsv recarga = new GestorArchivo().cargar(estaciones.toFile(), conexiones.toFile());
        this.verificar(recarga.getCantidadEstaciones() == 2
                && "Centro".equals(recarga.getEstaciones()[0].getNombre())
                && "Sur, terminal".equals(recarga.getEstaciones()[1].getNombre()),
                "La recarga conserva el alta, el cambio y la baja de estaciones.");
        this.verificar(recarga.getCantidadConexiones() == 1
                && recarga.getConexiones()[0].getTiempoMinutos() == 4,
                "Se conserva la conexión restante.");

        sesion.editarConexion(0, null);
        grafo = sesion.crearGrafo();
        this.verificar(grafo.getGrado("A") == 0 && grafo.getGrado("C") == 0,
                "Eliminar la última fila de un par retira ambos sentidos.");
        sesion.cargar(estaciones.toFile(), conexiones.toFile());
        this.verificarTramo(sesion.crearGrafo(), "A", "C", 4);
    }

    private void probarGrafoCargado(SesionCsv sesion) {
        Grafo grafo = sesion.crearGrafo();
        Estacion estacion = grafo.getTablaHash().obtener("A");
        this.verificar(grafo.cantidadEstaciones() == 2 && grafo.getTablaHash().tamano() == 2
                && grafo.existeEstacion("A") && grafo.existeEstacion("B") && estacion != null
                && "A".equals(estacion.getId()) && "Central".equals(estacion.getNombre())
                && "L1".equals(estacion.getLinea()) && "Centro".equals(estacion.getZona())
                && estacion.getCapacidad() == 100, "La carga registra las estaciones con todos sus campos.");
        this.verificarTramo(grafo, "A", "B", 7);

        grafo.eliminarEstacion("B");
        estacion.setNombre("Cambio externo");
        Grafo vigente = sesion.crearGrafo();
        this.verificar("Central".equals(vigente.getEstacion("A").getNombre()),
                "Modificar una instantánea no modifica los datos de la sesión.");
        this.verificarTramo(vigente, "A", "B", 7);
    }

    private void verificarTramo(Grafo grafo, String origen, String destino, int peso) {
        this.verificar(this.tieneArista(grafo.getVecinos(origen), destino, peso)
                && this.tieneArista(grafo.getVecinos(destino), origen, peso),
                "El tramo " + origen + " - " + destino + " debe pesar " + peso + " en ambos sentidos.");
    }

    private boolean tieneArista(ListaPropia<Arista> vecinos, String destino, int peso) {
        if (vecinos != null) {
            for (estructuras.Nodo<Arista> nodo = vecinos.getCabeza(); nodo != null; nodo = nodo.getSiguiente()) {
                Arista arista = nodo.getInfo();
                if (destino.equals(arista.getDestino())) {
                    return arista.getPeso() == peso;
                }
            }
        }
        return false;
    }

    private void probarGuardadoFallido(SesionCsv sesion, Path estaciones, Path conexiones)
            throws Exception {
        String contenidoAnterior = Files.readString(estaciones);
        Path copia = conexiones.resolveSibling("copia-conexiones.csv");
        Files.move(conexiones, copia);
        Path bloqueo = conexiones.resolve("bloqueo");
        try {
            Files.createDirectory(conexiones);
            Files.createFile(bloqueo);
            this.verificarRechazo(sesion::guardar, "No se pudieron guardar");
            this.verificar(contenidoAnterior.equals(Files.readString(estaciones)),
                    "Si falla el segundo reemplazo, se restaura el primer CSV.");
            this.verificar(sesion.tieneCambios(), "El guardado fallido conserva los cambios.");
        } finally {
            Files.deleteIfExists(bloqueo);
            Files.deleteIfExists(conexiones);
            Files.move(copia, conexiones);
        }
    }

    private void probarCamposInvalidos() {
        this.verificarCampoInvalido(() -> new DatoEstacionCsv("A", " ", "L1", "Z", 0), "obligatorio");
        this.verificarCampoInvalido(() -> new DatoEstacionCsv("A", "Uno\rDos", "L1", "Z", 0),
                "sola línea");
        this.verificarCampoInvalido(() -> new DatoEstacionCsv("A", "Uno", "L1", "Z", -1), "negativa");
        this.verificarCampoInvalido(() -> new DatoConexionCsv("A", "A", 2), "diferentes");
        this.verificarCampoInvalido(() -> new DatoConexionCsv("A", "B", 0), "mayor que cero");
    }

    private void verificarCampoInvalido(Runnable operacion, String mensaje) {
        try {
            operacion.run();
        } catch (IllegalArgumentException excepcion) {
            this.verificar(excepcion.getMessage().contains(mensaje), "Error esperado: " + mensaje);
            return;
        }
        throw new AssertionError("El campo debía rechazarse.");
    }

    private void probarMapaDeEjemplo() throws Exception {
        DatosCsv datos = new GestorArchivo().cargar(Path.of("datos", "diccionario.csv").toFile(),
                Path.of("datos", "grafo.csv").toFile());
        Graph red = new MostrarRed().crearRed(datos);
        this.verificar(red.getNodeCount() == 44 && red.getEdgeCount() == 41,
                "El mapa contiene todas las estaciones y conexiones de los CSV de ejemplo.");
        for (DatoEstacionCsv estacion : datos.getEstaciones()) {
            Node nodo = red.getNode(estacion.getId());
            this.verificar(nodo != null && estacion.getNombre().equals(nodo.getAttribute("ui.label")),
                    "El mapa muestra el nombre completo de " + estacion.getId() + ".");
        }
        DatoConexionCsv[] conexiones = datos.getConexiones();
        for (int indice = 0; indice < conexiones.length; indice++) {
            DatoConexionCsv conexion = conexiones[indice];
            Edge arista = red.getEdge("conexion-" + indice);
            this.verificar(arista != null && arista.isDirected()
                    && conexion.getIdOrigen().equals(arista.getSourceNode().getId())
                    && conexion.getIdDestino().equals(arista.getTargetNode().getId())
                    && (conexion.getTiempoMinutos() + " min").equals(arista.getAttribute("ui.label")),
                    "La conexión de ejemplo conserva el sentido y el tiempo en minutos.");
        }
        this.verificar(red.getNode("LOS_D").getDegree() == 0, "El mapa conserva las estaciones aisladas.");

        GraphicGraph vista = new GraphicGraph("Prueba del estilo");
        GraphReplay repeticion = new GraphReplay("CSV de ejemplo");
        repeticion.addSink(vista);
        repeticion.replay(red);
        Color primeraLinea = this.colorDe((GraphicElement) vista.getNode("PRO"));
        Color[] colores = {primeraLinea, this.colorDe((GraphicElement) vista.getNode("ZOO")),
            this.colorDe((GraphicElement) vista.getNode("RIN")),
            this.colorDe((GraphicElement) vista.getNode("ZUM"))};
        for (int primero = 0; primero < colores.length; primero++) {
            for (int segundo = primero + 1; segundo < colores.length; segundo++) {
                this.verificar(!colores[primero].equals(colores[segundo]),
                        "Cada línea de ejemplo se dibuja en un color distinto.");
            }
        }
        this.verificar(primeraLinea.equals(this.colorDe((GraphicElement) vista.getNode("PER")))
                && primeraLinea.equals(this.colorDe((GraphicElement) vista.getEdge("conexion-0"))),
                "La estación y el tramo comparten el color de su línea.");
        Edge transbordo = red.getNode("CUD").getEdgeToward("PZA_VZLA");
        Color colorTransbordo = this.colorDe((GraphicElement) vista.getEdge(transbordo.getId()));
        this.verificar(!colorTransbordo.equals(colores[0]) && !colorTransbordo.equals(colores[2]),
                "El transbordo se distingue de las líneas que conecta.");
    }

    private Color colorDe(GraphicElement elemento) {
        return elemento.getStyle().getFillColor(0);
    }

    private void probarRepresentacion(Graph redInicial, DatosCsv datos) {
        Graph red = new MostrarRed().crearRed(datos);
        this.verificar(red.getNodeCount() == 2 && red.getNode("B") == null
                && "Sur, terminal".equals(red.getNode("C").getAttribute("ui.label"))
                && "Centro".equals(red.getNode("A").getAttribute("ui.label"))
                && "Central".equals(redInicial.getNode("A").getAttribute("ui.label")),
                "Al volver a abrir el mapa se reflejan las ediciones sin guardar.");
        this.verificar(red.getEdgeCount() == 2 && red.getEdge(0).isDirected()
                && "C".equals(red.getEdge(0).getTargetNode().getId())
                && "5 min".equals(red.getEdge(0).getAttribute("ui.label"))
                && "4 min".equals(red.getEdge(1).getAttribute("ui.label")),
                "La visualización conserva extremos, sentido, tiempo y filas paralelas.");
    }

    private void verificarRechazo(Operacion operacion, String mensaje) throws Exception {
        try {
            operacion.ejecutar();
        } catch (ExcepcionArchivoCsv excepcion) {
            this.verificar(excepcion.getMessage().contains(mensaje), "Error esperado: " + mensaje);
            return;
        }
        throw new AssertionError("La operación debía rechazarse.");
    }

    private void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    @FunctionalInterface
    private interface Operacion {

        void ejecutar() throws ExcepcionArchivoCsv;
    }
}
