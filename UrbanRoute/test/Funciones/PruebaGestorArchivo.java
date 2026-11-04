/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Pruebas de lectura, validación y persistencia de los archivos CSV.
 *
 * @author Eliam
 */
public class PruebaGestorArchivo {

    private int verificaciones;
    private Path carpetaTemporal;

    /** Ejecuta las pruebas con archivos temporales independientes de los datos del proyecto. */
    public static void main(String[] args) throws Exception {
        PruebaGestorArchivo pruebas = new PruebaGestorArchivo();
        pruebas.ejecutar();
    }

    private void ejecutar() throws Exception {
        this.carpetaTemporal = Files.createTempDirectory("urbanroute-pruebas-");
        try {
            this.probarCargaValidaYComillas();
            this.probarGuardadoYRecarga();
            this.probarCabecerasYFilasInvalidas();
            this.probarReferenciasYDuplicados();
            this.probarRechazoAlGuardar();
            this.probarSesionDeArchivos();
            System.out.println("Pruebas de GestorArchivo: " + this.verificaciones
                    + " verificaciones correctas.");
        } finally {
            this.eliminarContenido(this.carpetaTemporal.toFile());
        }
    }

    private void probarCargaValidaYComillas() throws Exception {
        File estaciones = this.crear("estaciones-validas.csv",
                "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\n"
                + "A, \"Estación, Central\" ,Linea 1,Centro,100\n"
                + "B,\"Estación \"\"Norte\"\"\",Linea 2,Norte,0\n");
        File conexiones = this.crear("conexiones-validas.csv",
                "Estacion_Origen,Estacion_Destino,Tiempo_Minutos\nA,B,3\n");
        GestorArchivo gestor = new GestorArchivo();
        DatosCsv datos = gestor.cargar(estaciones, conexiones);
        this.verificar(datos.getCantidadEstaciones() == 2, "Carga dos estaciones");
        this.verificar(datos.getCantidadConexiones() == 1, "Carga una conexión");
        this.verificar("Estación, Central".equals(datos.getEstaciones()[0].getNombre()),
                "Lee coma entre comillas");
        this.verificar("Estación \"Norte\"".equals(datos.getEstaciones()[1].getNombre()),
                "Lee comillas escapadas");
        this.verificar(datos.getEstaciones()[1].getCapacidadPasajerosHora() == 0,
                "Capacidad cero permitida");
        this.verificar(datos.getConexiones()[0].getTiempoMinutos() == 3, "Tiempo leído");
        DatoEstacionCsv[] copia = datos.getEstaciones();
        copia[0] = null;
        this.verificar(datos.getEstaciones()[0] != null, "DatosCsv entrega copia defensiva");
    }

    private void probarGuardadoYRecarga() throws Exception {
        File estaciones = this.ruta("salida-estaciones.csv");
        File conexiones = this.ruta("salida-conexiones.csv");
        DatoEstacionCsv[] filasEstaciones = {
            new DatoEstacionCsv("A", "Estación, A", "L1", "Centro", 10),
            new DatoEstacionCsv("B", "Estación \"B\"", "L2", "Oeste", 20),
            new DatoEstacionCsv("C", "Estación C", "L3", "Sur", 30)
        };
        DatoConexionCsv[] filasConexiones = {
            new DatoConexionCsv("A", "B", 2),
            new DatoConexionCsv("B", "C", 4)
        };
        GestorArchivo gestor = new GestorArchivo();
        gestor.guardar(new DatosCsv(filasEstaciones, filasConexiones), estaciones, conexiones);
        this.verificar(estaciones.isFile(), "Crea archivo de estaciones");
        this.verificar(conexiones.isFile(), "Crea archivo de conexiones");
        DatosCsv recargados = gestor.cargar(estaciones, conexiones);
        this.verificar(recargados.getCantidadEstaciones() == 3, "Recarga alta de estación");
        this.verificar(recargados.getCantidadConexiones() == 2, "Recarga conexiones");
        this.verificar("Estación, A".equals(recargados.getEstaciones()[0].getNombre()),
                "Conserva coma al guardar");
        this.verificar("Estación \"B\"".equals(recargados.getEstaciones()[1].getNombre()),
                "Conserva comillas al guardar");
        DatoEstacionCsv[] despuesBaja = {filasEstaciones[0], filasEstaciones[1]};
        DatoConexionCsv[] despuesBajaConexion = {filasConexiones[0]};
        gestor.guardar(new DatosCsv(despuesBaja, despuesBajaConexion));
        recargados = gestor.cargar(estaciones, conexiones);
        this.verificar(recargados.getCantidadEstaciones() == 2, "Persiste baja de estación");
        this.verificar(recargados.getCantidadConexiones() == 1, "Persiste baja de conexión");
    }

    private void probarCabecerasYFilasInvalidas() throws Exception {
        File conexiones = this.crear("conexiones-base.csv",
                "Estacion_Origen,Estacion_Destino,Tiempo_Minutos\nA,B,2\n");
        this.verificarError(this.crear("cabecera.csv", "ID,Nombre\nA,Uno\n"), conexiones,
                "Cabecera inválida");
        this.verificarError(this.crear("columnas.csv",
                "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\nA,Uno,L1\n"),
                conexiones, "Cantidad de columnas");
        this.verificarError(this.crear("numero.csv",
                "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\nA,Uno,L1,Z,no\n"),
                conexiones, "número entero");
        this.verificarError(this.crear("comillas.csv",
                "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\nA,\"Uno,L1,Z,2\n"),
                conexiones, "Comillas sin cerrar");
        this.verificarError(this.crear("comillas-en-texto.csv",
                "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\nA,U\"n\"o,L1,Z,2\n"),
                conexiones, "línea 2: Comillas dentro");
        this.verificarError(this.crear("texto-tras-comillas.csv",
                "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\nA,\"Un\"o,L1,Z,2\n"),
                conexiones, "línea 2: Texto después");
        File malCodificado = this.crear("mal-codificado.csv",
                "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\n"
                + "A,\u00D1,L1,Z,2\nB,Dos,L1,Z,3\n");
        Files.writeString(malCodificado.toPath(), Files.readString(malCodificado.toPath()),
                StandardCharsets.ISO_8859_1);
        this.verificarError(malCodificado, conexiones, "debe estar codificado en UTF-8");
    }

    private void probarReferenciasYDuplicados() throws Exception {
        File conexiones = this.crear("conexion-referencia.csv",
                "Estacion_Origen,Estacion_Destino,Tiempo_Minutos\nA,C,2\n");
        File duplicadas = this.crear("duplicadas.csv",
                "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\n"
                + "A,Uno,L1,Z,1\nA,Otro,L2,Z,2\n");
        this.verificarError(duplicadas, conexiones, "repetido");
        File estaciones = this.crear("estaciones-referencia.csv",
                "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\n"
                + "A,Uno,L1,Z,1\nB,Dos,L1,Z,2\n");
        this.verificarError(estaciones, conexiones, "inexistente");
        File lazo = this.crear("conexion-lazo.csv",
                "Estacion_Origen,Estacion_Destino,Tiempo_Minutos\nA,A,2\n");
        this.verificarError(estaciones, lazo, "diferentes");
    }

    private void probarSesionDeArchivos() throws Exception {
        GestorArchivo gestor = new GestorArchivo();
        try {
            gestor.guardar(new DatosCsv(new DatoEstacionCsv[0], new DatoConexionCsv[0]));
        } catch (ExcepcionArchivoCsv excepcion) {
            this.verificar(excepcion.getMessage().contains("Primero debe cargar"),
                    "Guardar sin sesión explica el error");
        }
        this.verificar(gestor.getArchivoEstacionesActual() == null, "Sesión inicia sin estaciones");
        this.verificar(gestor.getArchivoConexionesActual() == null, "Sesión inicia sin conexiones");
    }

    private void probarRechazoAlGuardar() throws Exception {
        String contenido = "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora\nA,Uno,L1,Z,1\n";
        File estaciones = this.crear("protegido.csv", contenido);
        File alias = this.ruta("./protegido.csv");
        File conexiones = this.ruta("sin-crear.csv");
        DatoEstacionCsv fila = new DatoEstacionCsv("A", "Uno", "L1", "Z", 1);
        DatosCsv datos = new DatosCsv(new DatoEstacionCsv[]{fila}, new DatoConexionCsv[0]);
        GestorArchivo gestor = new GestorArchivo();

        this.verificarError(estaciones, alias, "archivos CSV distintos");
        this.verificarErrorDeGuardado(gestor, datos, estaciones, alias, "archivos CSV distintos");
        DatosCsv datosConFilaVacia = new DatosCsv(
                new DatoEstacionCsv[]{fila, null}, new DatoConexionCsv[0]);
        this.verificarErrorDeGuardado(gestor, datosConFilaVacia, estaciones, conexiones,
                "estación vacía");
        this.verificar(contenido.equals(Files.readString(estaciones.toPath())) && !conexiones.exists(),
                "Los guardados inválidos no alteran ni crean archivos");
        this.verificar(gestor.getArchivoEstacionesActual() == null
                && gestor.getArchivoConexionesActual() == null, "No se abre una sesión al fallar");
    }

    private void verificarErrorDeGuardado(GestorArchivo gestor, DatosCsv datos,
            File estaciones, File conexiones, String fragmento) throws Exception {
        try {
            gestor.guardar(datos, estaciones, conexiones);
        } catch (ExcepcionArchivoCsv excepcion) {
            this.verificar(excepcion.getMessage().contains(fragmento), "Error claro: " + fragmento);
            return;
        }
        throw new AssertionError("Se aceptó un guardado inválido: " + fragmento);
    }

    private void verificarError(File estaciones, File conexiones, String fragmento)
            throws Exception {
        try {
            new GestorArchivo().cargar(estaciones, conexiones);
        } catch (ExcepcionArchivoCsv excepcion) {
            this.verificar(excepcion.getMessage().contains(fragmento),
                    "Error claro: " + fragmento);
            return;
        }
        throw new AssertionError("Se aceptaron datos inválidos: " + fragmento);
    }

    private File crear(String nombre, String contenido) throws IOException {
        File archivo = this.ruta(nombre);
        Files.writeString(archivo.toPath(), contenido, StandardCharsets.UTF_8);
        return archivo;
    }

    private File ruta(String nombre) {
        return this.carpetaTemporal.resolve(nombre).toFile();
    }

    private void eliminarContenido(File archivo) throws IOException {
        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();
            if (hijos != null) {
                for (File hijo : hijos) {
                    this.eliminarContenido(hijo);
                }
            }
        }
        Files.deleteIfExists(archivo.toPath());
    }

    private void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
        this.verificaciones++;
    }
}
