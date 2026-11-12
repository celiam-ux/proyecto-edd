/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 * Grafo ponderado que modela la red de transporte multimodal. Los vértices son
 * estaciones y las aristas representan conexiones directas con un tiempo de
 * traslado asociado. La estructura de conexión es una lista de adyacencia propia
 * construida desde cero, sin el Java Collections Framework.
 *
 * <p>El grafo no almacena la información completa de las estaciones: delega esa
 * responsabilidad en la {@link TablaHash}. Internamente sólo guarda, por cada
 * identificador de estación, su {@link ListaPropia} de {@link Arista}. Para
 * localizar esa lista en O(1) utiliza su propia tabla de dispersión de
 * adyacencias indexada también por el identificador.</p>
 *
 * <p>Por defecto el grafo es no dirigido: cada conexión se refleja en ambos
 * sentidos, tal como ocurre con los tramos del metro.</p>
 *
 * @author Jorge
 */
public class Grafo {

    /**
     * Entrada interna que asocia el identificador de una estación con su lista
     * de aristas salientes.
     */
    private static class EntradaAdy {
        private final String sId;
        private final ListaPropia<Arista> adyacentes;

        private EntradaAdy(String sId) {
            this.sId = sId;
            this.adyacentes = new ListaPropia<>();
        }
    }

    private final TablaHash estaciones;
    private ListaPropia<EntradaAdy>[] adyacencia;
    private int iCapacidad;
    private int iVertices;

    private static final int CAPACIDAD_INICIAL = 11;

    /**
     * Construye un grafo vacío que se apoya en la tabla hash indicada para la
     * gestión de los datos de las estaciones.
     *
     * @param estaciones tabla hash que indexa los objetos {@link Estacion}
     */
    @SuppressWarnings("unchecked")
    public Grafo(TablaHash estaciones) {
        this.estaciones = estaciones;
        this.iCapacidad = CAPACIDAD_INICIAL;
        this.adyacencia = new ListaPropia[this.iCapacidad];
        this.iVertices = 0;
    }

    /**
     * Función de dispersión interna para indexar las listas de adyacencia por el
     * identificador de la estación (método de Horner con base 31).
     *
     * @param sId identificador a dispersar
     * @return índice no negativo dentro del arreglo de adyacencia
     */
    private int dispersion(String sId) {
        int iHash = 0;
        for (int i = 0; i < sId.length(); i++) {
            iHash = (31 * iHash + sId.charAt(i)) % this.iCapacidad;
        }
        if (iHash < 0) {
            iHash = iHash + this.iCapacidad;
        }
        return iHash;
    }

    /**
     * Ubica la entrada de adyacencia correspondiente a un identificador.
     *
     * @param sId identificador buscado
     * @return la entrada de adyacencia, o {@code null} si el vértice no existe
     */
    private EntradaAdy buscarEntrada(String sId) {
        int iPos = dispersion(sId);
        if (this.adyacencia[iPos] == null) {
            return null;
        }
        Nodo<EntradaAdy> pAux = this.adyacencia[iPos].getCabeza();
        while (pAux != null) {
            if (pAux.getInfo().sId.equals(sId)) {
                return pAux.getInfo();
            }
            pAux = pAux.getSiguiente();
        }
        return null;
    }

    /**
     * Agrega una estación al grafo. La registra como vértice (creando su lista de
     * adyacencia vacía) y la indexa en la {@link TablaHash} asociada. Si la
     * estación ya existía, no se duplica.
     *
     * @param estacion estación a incorporar
     */
    public void agregarEstacion(Estacion estacion) {
        if (buscarEntrada(estacion.getId()) != null) {
            this.estaciones.insertar(estacion.getId(), estacion);
            return;
        }
        int iPos = dispersion(estacion.getId());
        if (this.adyacencia[iPos] == null) {
            this.adyacencia[iPos] = new ListaPropia<>();
        }
        this.adyacencia[iPos].agregarInicio(new EntradaAdy(estacion.getId()));
        this.estaciones.insertar(estacion.getId(), estacion);
        this.iVertices++;
    }

    /**
     * Elimina una estación del grafo simulando el cierre temporal de un nodo.
     * Retira su lista de adyacencia, elimina todas las aristas que apuntaban a
     * ella desde otros vértices y la borra de la {@link TablaHash}.
     *
     * @param sId identificador de la estación a eliminar
     * @return {@code true} si la estación existía y fue eliminada
     */
    public boolean eliminarEstacion(String sId) {
        EntradaAdy entrada = buscarEntrada(sId);
        if (entrada == null) {
            return false;
        }
        int iPos = dispersion(sId);
        this.adyacencia[iPos].eliminar(entrada);
        for (int i = 0; i < this.iCapacidad; i++) {
            if (this.adyacencia[i] != null) {
                Nodo<EntradaAdy> pAux = this.adyacencia[i].getCabeza();
                while (pAux != null) {
                    pAux.getInfo().adyacentes.eliminar(new Arista(sId, 0));
                    pAux = pAux.getSiguiente();
                }
            }
        }
        this.estaciones.eliminar(sId);
        this.iVertices--;
        return true;
    }

    /**
     * Agrega una conexión no dirigida entre dos estaciones con el peso indicado.
     * La arista se refleja en ambos sentidos. Ambas estaciones deben existir
     * previamente como vértices.
     *
     * @param sOrigen  identificador de la estación origen
     * @param sDestino identificador de la estación destino
     * @param iPeso    tiempo de traslado en minutos
     * @throws IllegalStateException si alguno de los vértices no existe
     */
    public void agregarArista(String sOrigen, String sDestino, int iPeso) {
        EntradaAdy eOrigen = buscarEntrada(sOrigen);
        EntradaAdy eDestino = buscarEntrada(sDestino);
        if (eOrigen == null || eDestino == null) {
            throw new IllegalStateException("Precondición violada: Ambas estaciones deben existir.");
        }
        if (!eOrigen.adyacentes.contiene(new Arista(sDestino, 0))) {
            eOrigen.adyacentes.agregarFinal(new Arista(sDestino, iPeso));
        }
        if (!eDestino.adyacentes.contiene(new Arista(sOrigen, 0))) {
            eDestino.adyacentes.agregarFinal(new Arista(sOrigen, iPeso));
        }
    }

    /**
     * Elimina la conexión no dirigida entre dos estaciones, retirándola de las
     * listas de adyacencia de ambos extremos.
     *
     * @param sOrigen  identificador de una de las estaciones
     * @param sDestino identificador de la otra estación
     * @return {@code true} si la conexión existía y fue eliminada
     */
    public boolean eliminarArista(String sOrigen, String sDestino) {
        EntradaAdy eOrigen = buscarEntrada(sOrigen);
        EntradaAdy eDestino = buscarEntrada(sDestino);
        if (eOrigen == null || eDestino == null) {
            return false;
        }
        boolean bA = eOrigen.adyacentes.eliminar(new Arista(sDestino, 0));
        boolean bB = eDestino.adyacentes.eliminar(new Arista(sOrigen, 0));
        return bA || bB;
    }

    /**
     * Retorna la lista de aristas salientes (vecinos directos) de una estación.
     * Es el método que consumen los algoritmos de recorrido (BFS/DFS) y de ruta
     * mínima (Dijkstra) del módulo de lógica.
     *
     * @param sId identificador de la estación
     * @return lista de aristas adyacentes, o {@code null} si el vértice no existe
     */
    public ListaPropia<Arista> getVecinos(String sId) {
        EntradaAdy entrada = buscarEntrada(sId);
        if (entrada == null) {
            return null;
        }
        return entrada.adyacentes;
    }

    /**
     * Calcula el grado de una estación, es decir, su número de conexiones
     * directas. Es la métrica base para la identificación de hubs de
     * transferencia (centralidad de grado).
     *
     * @param sId identificador de la estación
     * @return cantidad de conexiones directas, o -1 si el vértice no existe
     */
    public int getGrado(String sId) {
        EntradaAdy entrada = buscarEntrada(sId);
        if (entrada == null) {
            return -1;
        }
        return entrada.adyacentes.tamano();
    }

    /**
     * Recupera el objeto {@link Estacion} asociado a un identificador, delegando
     * la búsqueda en la {@link TablaHash}.
     *
     * @param sId identificador de la estación
     * @return la estación correspondiente, o {@code null} si no existe
     */
    public Estacion getEstacion(String sId) {
        return this.estaciones.obtener(sId);
    }

    /**
     * Indica si una estación está registrada como vértice del grafo.
     *
     * @param sId identificador a verificar
     * @return {@code true} si el vértice existe
     */
    public boolean existeEstacion(String sId) {
        return buscarEntrada(sId) != null;
    }

    /**
     * Retorna todos los identificadores de estación presentes en el grafo. Útil
     * para que los recorridos externos inicialicen sus estructuras de visitados.
     *
     * @return lista con los identificadores de todos los vértices
     */
    public ListaPropia<String> getIdsEstaciones() {
        ListaPropia<String> ids = new ListaPropia<>();
        for (int i = 0; i < this.iCapacidad; i++) {
            if (this.adyacencia[i] != null) {
                Nodo<EntradaAdy> pAux = this.adyacencia[i].getCabeza();
                while (pAux != null) {
                    ids.agregarFinal(pAux.getInfo().sId);
                    pAux = pAux.getSiguiente();
                }
            }
        }
        return ids;
    }

    /**
     * @return la cantidad de estaciones (vértices) del grafo
     */
    public int cantidadEstaciones() {
        return this.iVertices;
    }

    /**
     * @return la tabla hash de estaciones sobre la que se apoya el grafo
     */
    public TablaHash getTablaHash() {
        return this.estaciones;
    }
}
