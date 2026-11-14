/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algoritmos;

import estructuras.Grafo;
import estructuras.ListaPropia;
import estructuras.Nodo;

/**
 * Identifica los hubs de transferencia de la red por centralidad de grado,
 * es decir, las estaciones que tienen mas conexiones directas.
 *
 * @author Jorge
 */
public class AnalisisHubs {
    private Grafo grafo;

    /**
     * @param grafo red de transporte que se va a analizar
     */
    public AnalisisHubs(Grafo grafo) {
        this.grafo = grafo;
    }

    /**
     * @return el mayor grado que tiene alguna estacion de la red, 0 si la red
     *         esta vacia
     */
    public int gradoMaximo() {
        int iMax = 0;
        Nodo<String> pAux = grafo.getIdsEstaciones().getCabeza();
        while (pAux != null) {
            int iGrado = grafo.getGrado(pAux.getInfo());
            if (iGrado > iMax) {
                iMax = iGrado;
            }
            pAux = pAux.getSiguiente();
        }
        return iMax;
    }

    /**
     * Busca las estaciones con el grado maximo. Puede haber mas de una si
     * estan empatadas.
     *
     * @return lista con los ids de los hubs principales
     */
    public ListaPropia<String> hubsPrincipales() {
        ListaPropia<String> hubs = new ListaPropia<>();
        int iMax = gradoMaximo();
        if (iMax == 0) {
            return hubs;
        }
        Nodo<String> pAux = grafo.getIdsEstaciones().getCabeza();
        while (pAux != null) {
            if (grafo.getGrado(pAux.getInfo()) == iMax) {
                hubs.agregarFinal(pAux.getInfo());
            }
            pAux = pAux.getSiguiente();
        }
        return hubs;
    }

    /**
     * Ordena todas las estaciones de mayor a menor grado. Se pasan a un
     * arreglo y se ordenan por seleccion.
     *
     * @return lista con los ids ordenados, primero la mas conectada
     */
    public ListaPropia<String> estacionesPorGrado() {
        ListaPropia<String> lista = grafo.getIdsEstaciones();
        int iN = lista.tamano();
        String[] ids = new String[iN];
        int[] iGrados = new int[iN];

        Nodo<String> pAux = lista.getCabeza();
        int k = 0;
        while (pAux != null) {
            ids[k] = pAux.getInfo();
            iGrados[k] = grafo.getGrado(ids[k]);
            k++;
            pAux = pAux.getSiguiente();
        }

        for (int i = 0; i < iN - 1; i++) {
            int iMayor = i;
            for (int j = i + 1; j < iN; j++) {
                if (iGrados[j] > iGrados[iMayor]) {
                    iMayor = j;
                }
            }
            if (iMayor != i) {
                int iAux = iGrados[i];
                iGrados[i] = iGrados[iMayor];
                iGrados[iMayor] = iAux;
                String sAux = ids[i];
                ids[i] = ids[iMayor];
                ids[iMayor] = sAux;
            }
        }

        ListaPropia<String> ordenadas = new ListaPropia<>();
        for (int i = 0; i < iN; i++) {
            ordenadas.agregarFinal(ids[i]);
        }
        return ordenadas;
    }

    /**
     * Devuelve las primeras estaciones del ranking por grado.
     *
     * @param iCantidad cuantas estaciones se quieren
     * @return lista con los ids de las estaciones mas conectadas, si se piden
     *         mas de las que hay se devuelven todas
     */
    public ListaPropia<String> topHubs(int iCantidad) {
        ListaPropia<String> top = new ListaPropia<>();
        Nodo<String> pAux = estacionesPorGrado().getCabeza();
        int i = 0;
        while (pAux != null && i < iCantidad) {
            top.agregarFinal(pAux.getInfo());
            i++;
            pAux = pAux.getSiguiente();
        }
        return top;
    }
}
