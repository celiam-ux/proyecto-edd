/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 * Representa una estación o parada dentro de la red de transporte multimodal.
 * Es el objeto de datos que la {@link TablaHash} indexa por su identificador y
 * que el {@link Grafo} utiliza como vértice.
 *
 * <p>Los campos se corresponden con las columnas del dataset maestro:
 * {@code ID_Estacion, Nombre, Linea, Zona, Capacidad_Pasajeros_Hora}.</p>
 *
 * @author Jorge
 */
public class Estacion {
    private String sId;
    private String sNombre;
    private String sLinea;
    private String sZona;
    private int iCapacidad;

    /**
     * Construye una estación con todos sus atributos.
     *
     * @param sId        identificador único (clave en la tabla hash)
     * @param sNombre    nombre descriptivo de la estación
     * @param sLinea     línea de transporte a la que pertenece
     * @param sZona      zona geográfica
     * @param iCapacidad capacidad en pasajeros por hora
     */
    public Estacion(String sId, String sNombre, String sLinea, String sZona, int iCapacidad) {
        this.sId = sId;
        this.sNombre = sNombre;
        this.sLinea = sLinea;
        this.sZona = sZona;
        this.iCapacidad = iCapacidad;
    }

    /**
     * @return el identificador único de la estación
     */
    public String getId() {
        return sId;
    }

    /**
     * @param sId nuevo identificador único
     */
    public void setId(String sId) {
        this.sId = sId;
    }

    /**
     * @return el nombre descriptivo de la estación
     */
    public String getNombre() {
        return sNombre;
    }

    /**
     * @param sNombre nuevo nombre descriptivo
     */
    public void setNombre(String sNombre) {
        this.sNombre = sNombre;
    }

    /**
     * @return la línea de transporte de la estación
     */
    public String getLinea() {
        return sLinea;
    }

    /**
     * @param sLinea nueva línea de transporte
     */
    public void setLinea(String sLinea) {
        this.sLinea = sLinea;
    }

    /**
     * @return la zona geográfica de la estación
     */
    public String getZona() {
        return sZona;
    }

    /**
     * @param sZona nueva zona geográfica
     */
    public void setZona(String sZona) {
        this.sZona = sZona;
    }

    /**
     * @return la capacidad en pasajeros por hora
     */
    public int getCapacidad() {
        return iCapacidad;
    }

    /**
     * @param iCapacidad nueva capacidad en pasajeros por hora
     */
    public void setCapacidad(int iCapacidad) {
        this.iCapacidad = iCapacidad;
    }

    /**
     * Dos estaciones se consideran iguales cuando comparten el mismo
     * identificador. Esto permite que la {@link ListaPropia} y la
     * {@link TablaHash} ubiquen y eliminen estaciones por su clave.
     *
     * @param obj objeto a comparar
     * @return {@code true} si ambos identificadores coinciden
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Estacion otra = (Estacion) obj;
        return this.sId.equals(otra.sId);
    }

    /**
     * @return el código hash derivado del identificador
     */
    @Override
    public int hashCode() {
        return this.sId.hashCode();
    }

    /**
     * @return una representación textual breve de la estación
     */
    @Override
    public String toString() {
        return this.sId + " - " + this.sNombre + " (" + this.sLinea + ")";
    }
}
