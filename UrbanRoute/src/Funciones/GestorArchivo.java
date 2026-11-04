/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

import EDD.Lista;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Lee y persiste los dos archivos CSV de UrbanRoute sin depender del TDA Grafo.
 *
 * @author Eliam
 */
public class GestorArchivo {

    private static final String CABECERA_ESTACIONES
            = "ID_Estacion,Nombre,Linea,Zona,Capacidad_Pasajeros_Hora";
    private static final String CABECERA_CONEXIONES
            = "Estacion_Origen,Estacion_Destino,Tiempo_Minutos";

    private File archivoEstacionesActual;
    private File archivoConexionesActual;

    /**
     * Carga y valida ambos archivos antes de cambiar la sesión actual.
     *
     * @param archivoEstaciones CSV de estaciones
     * @param archivoConexiones CSV de conexiones
     * @return datos listos para la interfaz
     * @throws ExcepcionArchivoCsv cuando un archivo no es válido
     */
    public DatosCsv cargar(File archivoEstaciones, File archivoConexiones)
            throws ExcepcionArchivoCsv {
        this.validarArchivoLegible(archivoEstaciones, "estaciones");
        this.validarArchivoLegible(archivoConexiones, "conexiones");
        DatoEstacionCsv[] estaciones = this.leerEstaciones(archivoEstaciones);
        DatoConexionCsv[] conexiones = this.leerConexiones(archivoConexiones);
        this.validarConjunto(estaciones, conexiones);
        this.archivoEstacionesActual = archivoEstaciones.getAbsoluteFile();
        this.archivoConexionesActual = archivoConexiones.getAbsoluteFile();
        return new DatosCsv(estaciones, conexiones);
    }

    /**
     * Guarda en los archivos actualmente cargados.
     *
     * @param datos filas actualizadas desde la interfaz
     * @throws ExcepcionArchivoCsv cuando no hay sesión o no se puede guardar
     */
    public void guardar(DatosCsv datos) throws ExcepcionArchivoCsv {
        if (this.archivoEstacionesActual == null || this.archivoConexionesActual == null) {
            throw new ExcepcionArchivoCsv("Primero debe cargar los archivos de estaciones y conexiones");
        }
        this.guardar(datos, this.archivoEstacionesActual, this.archivoConexionesActual);
    }

    /**
     * Prepara ambos CSV y restaura el primero si falla el reemplazo del segundo.
     *
     * @param datos filas actualizadas
     * @param archivoEstaciones destino de estaciones
     * @param archivoConexiones destino de conexiones
     * @throws ExcepcionArchivoCsv cuando los datos o destinos no son válidos
     */
    public void guardar(DatosCsv datos, File archivoEstaciones, File archivoConexiones)
            throws ExcepcionArchivoCsv {
        if (datos == null) {
            throw new ExcepcionArchivoCsv("No hay datos para guardar");
        }
        if (archivoEstaciones == null || archivoConexiones == null) {
            throw new ExcepcionArchivoCsv("Debe indicar ambos archivos de destino");
        }
        DatoEstacionCsv[] estaciones = datos.getEstaciones();
        DatoConexionCsv[] conexiones = datos.getConexiones();
        this.validarConjunto(estaciones, conexiones);
        Path temporalEstaciones = null;
        Path temporalConexiones = null;
        Path respaldoEstaciones = null;
        Path destinoEstaciones = archivoEstaciones.toPath();
        try {
            temporalEstaciones = this.crearTemporal(archivoEstaciones);
            temporalConexiones = this.crearTemporal(archivoConexiones);
            this.escribirEstaciones(estaciones, temporalEstaciones.toFile());
            this.escribirConexiones(conexiones, temporalConexiones.toFile());
            if (!Files.notExists(destinoEstaciones, LinkOption.NOFOLLOW_LINKS)) {
                if (!Files.isRegularFile(destinoEstaciones)) {
                    throw new IOException("El destino de estaciones debe ser un archivo");
                }
                respaldoEstaciones = this.crearTemporal(archivoEstaciones);
                Files.copy(destinoEstaciones, respaldoEstaciones, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS);
            }
            this.reemplazar(temporalEstaciones, destinoEstaciones);
            temporalEstaciones = null;
            try {
                this.reemplazar(temporalConexiones, archivoConexiones.toPath());
            } catch (IOException | SecurityException excepcion) {
                try {
                    if (respaldoEstaciones == null) {
                        Files.deleteIfExists(destinoEstaciones);
                    } else {
                        this.reemplazar(respaldoEstaciones, destinoEstaciones);
                    }
                } catch (IOException | SecurityException errorRestauracion) {
                    excepcion.addSuppressed(errorRestauracion);
                    String mensaje = "Falló el guardado y no se pudo restaurar el CSV de estaciones.";
                    if (respaldoEstaciones != null) {
                        mensaje += "\nCopia anterior: " + respaldoEstaciones;
                        respaldoEstaciones = null;
                    }
                    throw new ExcepcionArchivoCsv(mensaje, excepcion);
                }
                throw excepcion;
            }
            temporalConexiones = null;
            this.archivoEstacionesActual = archivoEstaciones.getAbsoluteFile();
            this.archivoConexionesActual = archivoConexiones.getAbsoluteFile();
        } catch (IOException | SecurityException excepcion) {
            throw new ExcepcionArchivoCsv("No se pudieron guardar los archivos CSV", excepcion);
        } finally {
            this.eliminarTemporal(temporalEstaciones);
            this.eliminarTemporal(temporalConexiones);
            this.eliminarTemporal(respaldoEstaciones);
        }
    }

    /** @return archivo de estaciones de la sesión o {@code null} */
    public File getArchivoEstacionesActual() {
        return this.archivoEstacionesActual;
    }

    /** @return archivo de conexiones de la sesión o {@code null} */
    public File getArchivoConexionesActual() {
        return this.archivoConexionesActual;
    }

    private void validarArchivoLegible(File archivo, String tipo) throws ExcepcionArchivoCsv {
        if (archivo == null || !archivo.isFile() || !archivo.canRead()) {
            throw new ExcepcionArchivoCsv("El archivo de " + tipo + " no existe o no se puede leer");
        }
        if (!archivo.getName().toLowerCase().endsWith(".csv")) {
            throw new ExcepcionArchivoCsv("El archivo de " + tipo + " debe tener extensión .csv");
        }
    }

    private DatoEstacionCsv[] leerEstaciones(File archivo) throws ExcepcionArchivoCsv {
        Lista filas = new Lista();
        try (BufferedReader lector = this.crearLector(archivo)) {
            String cabecera = lector.readLine();
            this.validarCabecera(cabecera, CABECERA_ESTACIONES, archivo);
            String linea;
            int numeroLinea = 1;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] campos = this.separarCsv(linea, 5, numeroLinea, archivo);
                int capacidad = this.convertirEntero(campos[4], "capacidad", numeroLinea, archivo);
                try {
                    filas.insertarFinal(new DatoEstacionCsv(campos[0], campos[1], campos[2],
                            campos[3], capacidad));
                } catch (IllegalArgumentException excepcion) {
                    throw this.errorFila(excepcion.getMessage(), numeroLinea, archivo);
                }
            }
        } catch (IOException excepcion) {
            throw new ExcepcionArchivoCsv("No se pudo leer " + archivo.getName(), excepcion);
        }
        DatoEstacionCsv[] resultado = new DatoEstacionCsv[filas.getSize()];
        for (int indice = 0; indice < resultado.length; indice++) {
            resultado[indice] = (DatoEstacionCsv) filas.getValor(indice);
        }
        return resultado;
    }

    private DatoConexionCsv[] leerConexiones(File archivo) throws ExcepcionArchivoCsv {
        Lista filas = new Lista();
        try (BufferedReader lector = this.crearLector(archivo)) {
            String cabecera = lector.readLine();
            this.validarCabeceraConexion(cabecera, archivo);
            String linea;
            int numeroLinea = 1;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] campos = this.separarCsv(linea, 3, numeroLinea, archivo);
                int tiempo = this.convertirEntero(campos[2], "tiempo", numeroLinea, archivo);
                try {
                    filas.insertarFinal(new DatoConexionCsv(campos[0], campos[1], tiempo));
                } catch (IllegalArgumentException excepcion) {
                    throw this.errorFila(excepcion.getMessage(), numeroLinea, archivo);
                }
            }
        } catch (IOException excepcion) {
            throw new ExcepcionArchivoCsv("No se pudo leer " + archivo.getName(), excepcion);
        }
        DatoConexionCsv[] resultado = new DatoConexionCsv[filas.getSize()];
        for (int indice = 0; indice < resultado.length; indice++) {
            resultado[indice] = (DatoConexionCsv) filas.getValor(indice);
        }
        return resultado;
    }

    private BufferedReader crearLector(File archivo) throws IOException {
        return new BufferedReader(new InputStreamReader(
                new FileInputStream(archivo), StandardCharsets.UTF_8));
    }

    private void validarCabecera(String cabecera, String esperada, File archivo)
            throws ExcepcionArchivoCsv {
        if (cabecera == null) {
            throw new ExcepcionArchivoCsv("El archivo " + archivo.getName() + " está vacío");
        }
        String limpia = cabecera.startsWith("\uFEFF") ? cabecera.substring(1) : cabecera;
        if (!esperada.equals(limpia.trim())) {
            throw new ExcepcionArchivoCsv("Cabecera inválida en " + archivo.getName()
                    + ". Se esperaba: " + esperada);
        }
    }

    private void validarCabeceraConexion(String cabecera, File archivo)
            throws ExcepcionArchivoCsv {
        if (cabecera == null) {
            throw new ExcepcionArchivoCsv("El archivo " + archivo.getName() + " está vacío");
        }
        String limpia = cabecera.startsWith("\uFEFF") ? cabecera.substring(1) : cabecera;
        String alternativa = "Estacion_Origen,Estacion_Destino,Tiempo_Traslado_Min";
        if (!CABECERA_CONEXIONES.equals(limpia.trim()) && !alternativa.equals(limpia.trim())) {
            throw new ExcepcionArchivoCsv("Cabecera inválida en " + archivo.getName());
        }
    }

    private String[] separarCsv(String linea, int cantidadEsperada, int numeroLinea, File archivo)
            throws ExcepcionArchivoCsv {
        Lista campos = new Lista();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;
        for (int indice = 0; indice < linea.length(); indice++) {
            char caracter = linea.charAt(indice);
            if (caracter == '"') {
                if (entreComillas && indice + 1 < linea.length()
                        && linea.charAt(indice + 1) == '"') {
                    campo.append('"');
                    indice++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (caracter == ',' && !entreComillas) {
                campos.insertarFinal(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(caracter);
            }
        }
        if (entreComillas) {
            throw this.errorFila("Comillas sin cerrar", numeroLinea, archivo);
        }
        campos.insertarFinal(campo.toString());
        if (campos.getSize() != cantidadEsperada) {
            throw this.errorFila("Cantidad de columnas incorrecta", numeroLinea, archivo);
        }
        String[] resultado = new String[cantidadEsperada];
        for (int indice = 0; indice < resultado.length; indice++) {
            resultado[indice] = ((String) campos.getValor(indice)).trim();
        }
        return resultado;
    }

    private int convertirEntero(String valor, String campo, int numeroLinea, File archivo)
            throws ExcepcionArchivoCsv {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException excepcion) {
            throw this.errorFila("El campo " + campo + " debe ser un número entero",
                    numeroLinea, archivo);
        }
    }

    private ExcepcionArchivoCsv errorFila(String mensaje, int numeroLinea, File archivo) {
        return new ExcepcionArchivoCsv(archivo.getName() + ", línea " + numeroLinea + ": " + mensaje);
    }

    /**
     * Comprueba que las filas de una sesión también se puedan guardar.
     */
    void validarConjunto(DatoEstacionCsv[] estaciones, DatoConexionCsv[] conexiones)
            throws ExcepcionArchivoCsv {
        for (int primero = 0; primero < estaciones.length; primero++) {
            if (estaciones[primero] == null) {
                throw new ExcepcionArchivoCsv("Hay una estación vacía en los datos");
            }
            for (int segundo = primero + 1; segundo < estaciones.length; segundo++) {
                if (estaciones[primero].getId().equals(estaciones[segundo].getId())) {
                    throw new ExcepcionArchivoCsv("ID de estación repetido: "
                            + estaciones[primero].getId());
                }
            }
        }
        for (DatoConexionCsv conexion : conexiones) {
            if (conexion == null) {
                throw new ExcepcionArchivoCsv("Hay una conexión vacía en los datos");
            }
            if (!this.existeEstacion(estaciones, conexion.getIdOrigen())
                    || !this.existeEstacion(estaciones, conexion.getIdDestino())) {
                throw new ExcepcionArchivoCsv("La conexión " + conexion.getIdOrigen() + " - "
                        + conexion.getIdDestino() + " usa una estación inexistente");
            }
        }
    }

    private boolean existeEstacion(DatoEstacionCsv[] estaciones, String id) {
        for (DatoEstacionCsv estacion : estaciones) {
            if (estacion.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private Path crearTemporal(File destino) throws IOException {
        File padre = destino.getAbsoluteFile().getParentFile();
        if (padre == null || (!padre.exists() && !padre.mkdirs()) || !padre.isDirectory()) {
            throw new IOException("No existe la carpeta de destino");
        }
        return Files.createTempFile(padre.toPath(), destino.getName(), ".tmp");
    }

    private void escribirEstaciones(DatoEstacionCsv[] estaciones, File archivo) throws IOException {
        try (BufferedWriter escritor = this.crearEscritor(archivo)) {
            escritor.write(CABECERA_ESTACIONES);
            escritor.newLine();
            for (DatoEstacionCsv estacion : estaciones) {
                escritor.write(this.escapar(estacion.getId()) + ","
                        + this.escapar(estacion.getNombre()) + ","
                        + this.escapar(estacion.getLinea()) + ","
                        + this.escapar(estacion.getZona()) + ","
                        + estacion.getCapacidadPasajerosHora());
                escritor.newLine();
            }
        }
    }

    private void escribirConexiones(DatoConexionCsv[] conexiones, File archivo) throws IOException {
        try (BufferedWriter escritor = this.crearEscritor(archivo)) {
            escritor.write(CABECERA_CONEXIONES);
            escritor.newLine();
            for (DatoConexionCsv conexion : conexiones) {
                escritor.write(this.escapar(conexion.getIdOrigen()) + ","
                        + this.escapar(conexion.getIdDestino()) + ","
                        + conexion.getTiempoMinutos());
                escritor.newLine();
            }
        }
    }

    private BufferedWriter crearEscritor(File archivo) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(archivo), StandardCharsets.UTF_8));
    }

    private String escapar(String valor) {
        if (valor.indexOf(',') < 0 && valor.indexOf('"') < 0
                && valor.indexOf('\n') < 0 && valor.indexOf('\r') < 0) {
            return valor;
        }
        return '"' + valor.replace("\"", "\"\"") + '"';
    }

    private void reemplazar(Path temporal, Path destino) throws IOException {
        try {
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException excepcion) {
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void eliminarTemporal(Path temporal) {
        if (temporal != null) {
            try {
                Files.deleteIfExists(temporal);
            } catch (IOException | SecurityException excepcion) {
                // El archivo temporal no afecta los datos guardados.
            }
        }
    }
}
