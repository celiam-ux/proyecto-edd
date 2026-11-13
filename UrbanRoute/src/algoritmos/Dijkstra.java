/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algoritmos;

import estructuras.Arista;
import estructuras.Grafo;
import estructuras.ListaPropia;
import estructuras.Nodo;

/**
 * Algoritmo de Dijkstra para buscar la ruta de menor tiempo entre dos
 * estaciones de la red. Se trabaja con arreglos paralelos: en la posición i
 * de cada arreglo se guarda la información de la estación i.
 *
 * @author Jorge
 */
public class Dijkstra {
    private static final int INFINITO = Integer.MAX_VALUE;

    private Grafo grafo;
    private String[] ids;
    private int[] iDistancia;
    private int[] iPrevio;
    private boolean[] bVisitado;
    private int iTiempoTotal;

    /**
     * @param grafo red de transporte sobre la que se calculan las rutas
     */
    public Dijkstra(Grafo grafo) {
        this.grafo = grafo;
        this.iTiempoTotal = -1;
    }

    /**
     * Calcula la ruta de menor tiempo entre dos estaciones. Despues de llamar
     * a este metodo se puede pedir el tiempo con {@link #getTiempoTotal()}.
     *
     * @param sOrigen  id de la estacion de salida
     * @param sDestino id de la estacion de llegada
     * @return lista con los ids de las estaciones en orden desde el origen
     *         hasta el destino, o {@code null} si no hay ruta o alguna de las
     *         dos estaciones no existe
     */
    public ListaPropia<String> rutaMasRapida(String sOrigen, String sDestino) {
        this.iTiempoTotal = -1;
        if (!grafo.existeEstacion(sOrigen) || !grafo.existeEstacion(sDestino)) {
            return null;
        }
        inicializar();
        int iOrigen = buscarIndice(sOrigen);
        int iDestino = buscarIndice(sDestino);
        iDistancia[iOrigen] = 0;

        int iActual = menorNoVisitado();
        while (iActual != -1 && iActual != iDestino) {
            bVisitado[iActual] = true;
            Nodo<Arista> pAux = grafo.getVecinos(ids[iActual]).getCabeza();
            while (pAux != null) {
                Arista a = pAux.getInfo();
                int iVecino = buscarIndice(a.getDestino());
                if (iVecino != -1 && !bVisitado[iVecino]) {
                    int iNueva = iDistancia[iActual] + a.getPeso();
                    if (iNueva < iDistancia[iVecino]) {
                        iDistancia[iVecino] = iNueva;
                        iPrevio[iVecino] = iActual;
                    }
                }
                pAux = pAux.getSiguiente();
            }
            iActual = menorNoVisitado();
        }

        // si el destino se quedo en infinito es porque no se puede llegar
        if (iDistancia[iDestino] == INFINITO) {
            return null;
        }
        this.iTiempoTotal = iDistancia[iDestino];

        // se arma la ruta de atras hacia adelante usando los previos
        ListaPropia<String> ruta = new ListaPropia<>();
        int i = iDestino;
        while (i != -1) {
            ruta.agregarInicio(ids[i]);
            i = iPrevio[i];
        }
        return ruta;
    }

    /**
     * @return tiempo en minutos de la ultima ruta calculada, o -1 si no se
     *         encontro ruta
     */
    public int getTiempoTotal() {
        return iTiempoTotal;
    }

    /**
     * Pasa los ids del grafo a un arreglo y deja las distancias en infinito,
     * los previos en -1 y todas las estaciones sin visitar.
     */
    private void inicializar() {
        ListaPropia<String> lista = grafo.getIdsEstaciones();
        int iN = lista.tamano();
        ids = new String[iN];
        iDistancia = new int[iN];
        iPrevio = new int[iN];
        bVisitado = new boolean[iN];

        Nodo<String> pAux = lista.getCabeza();
        int i = 0;
        while (pAux != null) {
            ids[i] = pAux.getInfo();
            iDistancia[i] = INFINITO;
            iPrevio[i] = -1;
            bVisitado[i] = false;
            i++;
            pAux = pAux.getSiguiente();
        }
    }

    /**
     * Busca en que posicion del arreglo quedo una estacion.
     *
     * @param sId id de la estacion
     * @return la posicion, o -1 si no esta
     */
    private int buscarIndice(String sId) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(sId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Busca la estacion sin visitar que tiene la menor distancia acumulada.
     *
     * @return la posicion de esa estacion, o -1 si ya no queda ninguna a la
     *         que se pueda llegar
     */
    private int menorNoVisitado() {
        int iMenor = -1;
        for (int i = 0; i < ids.length; i++) {
            if (!bVisitado[i] && iDistancia[i] != INFINITO) {
                if (iMenor == -1 || iDistancia[i] < iDistancia[iMenor]) {
                    iMenor = i;
                }
            }
        }
        return iMenor;
    }
}
