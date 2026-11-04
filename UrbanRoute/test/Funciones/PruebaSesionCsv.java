/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

import java.nio.file.Files;
import java.nio.file.Path;
import org.graphstream.graph.Graph;

/**
 * Verifica el controlador sin ventanas, el guardado de las ediciones y la
 * proyección de las filas al visor.
 *
 * @author Eliam
 */
public class PruebaSesionCsv {

    /** Ejecuta las comprobaciones con un par de CSV temporales. */
    public static void main(String[] args) throws Exception {
        Path carpeta = Files.createTempDirectory("urbanroute-sesion-");
        Path estaciones = carpeta.resolve("estaciones.csv");
        Path conexiones = carpeta.resolve("conexiones.csv");
        Path invalido = carpeta.resolve("invalido.csv");
        try {
            Files.writeString(estaciones, "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\n"
                    + "A,Central,L1,Centro,100\nB,Norte,L1,Norte,200\n");
            Files.writeString(conexiones, "Estacion_Origen,Estacion_Destino,Tiempo_Minutos\nA,B,7\n");
            Files.writeString(invalido, "Estacion_Origen,Estacion_Destino,Tiempo_Minutos\nA,Z,7\n");
            new PruebaSesionCsv().probar(estaciones, conexiones, invalido);
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

        this.verificarRechazo(() -> sesion.editarEstacion(-1,
                new DatoEstacionCsv("A", "Duplicada", "L1", "Norte", 0)), "repetido");
        this.verificarRechazo(() -> sesion.editarEstacion(1, null), "Retira primero");
        this.verificarRechazo(() -> sesion.editarConexion(0,
                new DatoConexionCsv("A", "C", 5)), "inexistente");
        this.verificar(!sesion.tieneCambios(), "Las ediciones inválidas no ensucian la sesión.");

        sesion.editarEstacion(-1, nueva);
        sesion.editarEstacion(0, new DatoEstacionCsv("A", "Centro", "L1", "Centro", 120));
        sesion.editarConexion(0, new DatoConexionCsv("A", "C", 5));
        sesion.editarConexion(-1, new DatoConexionCsv("A", "C", 8));
        sesion.editarEstacion(1, null);

        this.verificarRechazo(() -> sesion.cargar(estaciones.toFile(), invalido.toFile()), "inexistente");
        this.verificar(sesion.tieneCambios() && sesion.getArchivoConexiones().equals(conexiones.toFile()),
                "La carga inválida conserva archivos y cambios pendientes.");
        this.probarRepresentacion(sesion.getDatos());
        sesion.editarConexion(0, null);
        this.probarGuardadoFallido(sesion, estaciones, conexiones);
        sesion.guardar();
        this.verificar(!sesion.tieneCambios(), "Guardar limpia el indicador.");
        DatosCsv recarga = new GestorArchivo().cargar(estaciones.toFile(), conexiones.toFile());
        this.verificar(recarga.getCantidadEstaciones() == 2
                && "Centro".equals(recarga.getEstaciones()[0].getNombre())
                && "Sur, terminal".equals(recarga.getEstaciones()[1].getNombre()),
                "La recarga conserva el alta, el cambio y la baja de estaciones.");
        this.verificar(recarga.getCantidadConexiones() == 1
                && recarga.getConexiones()[0].getTiempoMinutos() == 8,
                "Se conserva la conexión restante.");
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

    private void probarRepresentacion(DatosCsv datos) {
        Graph red = new MostrarRed().crearRed(datos);
        this.verificar(red.getNodeCount() == 2 && red.getNode("C") != null,
                "La visualización incorpora las estaciones de la sesión.");
        this.verificar(red.getEdgeCount() == 2 && red.getEdge(0).isDirected()
                && "C".equals(red.getEdge(0).getTargetNode().getId())
                && "5 min".equals(red.getEdge(0).getAttribute("ui.label")),
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
