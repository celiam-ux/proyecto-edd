/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algoritmos;

import estructuras.Arista;
import estructuras.ColaPropia;
import estructuras.Grafo;
import estructuras.ListaPropia;
import estructuras.Nodo;

/**
 * Detecta las subredes (componentes conexos) de la red usando un recorrido
 * en anchura (BFS). Sirve para saber que zonas quedan incomunicadas cuando
 * se cierra una estacion.
 *
 * @author Jorge
 */
public class Conectividad {
    private Grafo grafo;

    /**
     * @param grafo red de transporte que se va a analizar
     */
    public Conectividad(Grafo grafo) {
        this.grafo = grafo;
    }

    /**
     * Busca todas las subredes de la red tal como esta en memoria.
     *
     * @return lista de subredes, cada subred es una lista con los ids de las
     *         estaciones que estan conectadas entre si
     */
    public ListaPropia<ListaPropia<String>> obtenerSubredes() {
        return buscarSubredes(null);
    }

    /**
     * Simula el cierre de una estacion y dice como quedaria dividida la red.
     * No modifica el grafo, solo se ignora la estacion durante el recorrido.
     *
     * @param sCerrada id de la estacion que se cierra
     * @return lista de subredes que quedarian sin esa estacion
     */
    public ListaPropia<ListaPropia<String>> subredesSinEstacion(String sCerrada) {
        return buscarSubredes(sCerrada);
    }

    /**
     * @return cantidad de subredes que tiene la red actualmente
     */
    public int contarSubredes() {
        return obtenerSubredes().tamano();
    }

    /**
     * @return {@code true} si desde cualquier estacion se puede llegar a
     *         todas las demas (hay una sola subred)
     */
    public boolean esConexa() {
        return contarSubredes() <= 1;
    }

    /**
     * Recorre todas las estaciones y por cada una que no se haya visitado
     * lanza un BFS, lo que alcanza ese BFS es una subred.
     *
     * @param sCerrada estacion que no se toma en cuenta, o {@code null} para
     *                 usar la red completa
     * @return lista de subredes encontradas
     */
    private ListaPropia<ListaPropia<String>> buscarSubredes(String sCerrada) {
        ListaPropia<ListaPropia<String>> subredes = new ListaPropia<>();
        ListaPropia<String> visitados = new ListaPropia<>();

        Nodo<String> pAux = grafo.getIdsEstaciones().getCabeza();
        while (pAux != null) {
            String sId = pAux.getInfo();
            if (!sId.equals(sCerrada) && !visitados.contiene(sId)) {
                subredes.agregarFinal(bfs(sId, visitados, sCerrada));
            }
            pAux = pAux.getSiguiente();
        }
        return subredes;
    }

    /**
     * Recorrido en anchura desde una estacion.
     *
     * @param sInicio   estacion donde arranca el recorrido
     * @param visitados estaciones ya visitadas, aqui se van agregando las
     *                  nuevas
     * @param sCerrada  estacion por la que no se puede pasar (puede ser null)
     * @return ids de todas las estaciones alcanzadas desde sInicio
     */
    private ListaPropia<String> bfs(String sInicio, ListaPropia<String> visitados, String sCerrada) {
        ListaPropia<String> subred = new ListaPropia<>();
        ColaPropia<String> cola = new ColaPropia<>();

        cola.encolar(sInicio);
        visitados.agregarFinal(sInicio);

        while (!cola.esVacia()) {
            String sActual = cola.desencolar();
            subred.agregarFinal(sActual);

            Nodo<Arista> pAux = grafo.getVecinos(sActual).getCabeza();
            while (pAux != null) {
                String sVecino = pAux.getInfo().getDestino();
                if (!sVecino.equals(sCerrada) && !visitados.contiene(sVecino)) {
                    visitados.agregarFinal(sVecino);
                    cola.encolar(sVecino);
                }
                pAux = pAux.getSiguiente();
            }
        }
        return subred;
    }
}
