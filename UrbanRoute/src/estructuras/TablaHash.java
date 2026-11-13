/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 * Tabla de dispersión propia que indexa objetos {@link Estacion} por su clave
 * (identificador o nombre), ofreciendo acceso promedio en O(1). Resuelve las
 * colisiones mediante encadenamiento separado: cada posición del arreglo guarda
 * una {@link ListaPropia} de entradas.
 *
 * <p><b>Función de dispersión.</b> Se emplea el método polinómico de Horner con
 * base 31 sobre los caracteres de la clave:</p>
 * <pre>
 *     h = 0
 *     para cada carácter c de la clave:
 *         h = (31 * h + c) mod M
 * </pre>
 * <p>donde {@code M} es el tamaño del arreglo. La base 31 es un primo pequeño
 * que distribuye bien las cadenas cortas (identificadores de estación) y permite
 * calcular el valor sin desbordes gracias al módulo progresivo. El factor de
 * carga se vigila y, al superar 0,75, la tabla se redimensiona al siguiente
 * número primo para conservar el rendimiento O(1) amortizado.</p>
 *
 * <p>No se utiliza el Java Collections Framework en ninguna parte de la
 * implementación.</p>
 *
 * @author Jorge
 */
public class TablaHash {

    /**
     * Par clave-valor almacenado en las cadenas de colisión.
     */
    private static class Entrada {
        private final String sClave;
        private Estacion valor;

        private Entrada(String sClave, Estacion valor) {
            this.sClave = sClave;
            this.valor = valor;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            Entrada otra = (Entrada) obj;
            return this.sClave.equals(otra.sClave);
        }

        @Override
        public int hashCode() {
            return this.sClave.hashCode();
        }
    }

    private static final int CAPACIDAD_INICIAL = 11;
    private static final double FACTOR_CARGA_MAX = 0.75;

    private ListaPropia<Entrada>[] tabla;
    private int iCapacidad;
    private int iN;

    /**
     * Construye una tabla hash vacía con la capacidad inicial por defecto.
     */
    @SuppressWarnings("unchecked")
    public TablaHash() {
        this.iCapacidad = CAPACIDAD_INICIAL;
        this.tabla = new ListaPropia[this.iCapacidad];
        this.iN = 0;
    }

    /**
     * Calcula el índice del arreglo para una clave dada aplicando el método de
     * Horner con base 31 y módulo sobre la capacidad actual.
     *
     * @param sClave clave a dispersar
     * @return índice no negativo dentro del rango de la tabla
     */
    private int dispersion(String sClave) {
        int iHash = 0;
        for (int i = 0; i < sClave.length(); i++) {
            iHash = (31 * iHash + sClave.charAt(i)) % this.iCapacidad;
        }
        if (iHash < 0) {
            iHash = iHash + this.iCapacidad;
        }
        return iHash;
    }

    /**
     * Inserta o actualiza la estación asociada a la clave indicada. Si la clave
     * ya existía, se sobrescribe su valor; en caso contrario se agrega una nueva
     * entrada y, de ser necesario, se redimensiona la tabla.
     *
     * @param sClave   clave de indexación (identificador de la estación)
     * @param estacion estación a almacenar
     */
    public void insertar(String sClave, Estacion estacion) {
        int iPos = dispersion(sClave);
        if (this.tabla[iPos] == null) {
            this.tabla[iPos] = new ListaPropia<>();
        }
        Nodo<Entrada> pAux = this.tabla[iPos].getCabeza();
        while (pAux != null) {
            if (pAux.getInfo().sClave.equals(sClave)) {
                pAux.getInfo().valor = estacion;
                return;
            }
            pAux = pAux.getSiguiente();
        }
        this.tabla[iPos].agregarInicio(new Entrada(sClave, estacion));
        this.iN++;
        if ((double) this.iN / this.iCapacidad > FACTOR_CARGA_MAX) {
            redimensionar();
        }
    }

    /**
     * Recupera la estación asociada a la clave indicada en O(1) promedio.
     *
     * @param sClave clave buscada
     * @return la estación asociada, o {@code null} si la clave no existe
     */
    public Estacion obtener(String sClave) {
        int iPos = dispersion(sClave);
        if (this.tabla[iPos] == null) {
            return null;
        }
        Nodo<Entrada> pAux = this.tabla[iPos].getCabeza();
        while (pAux != null) {
            if (pAux.getInfo().sClave.equals(sClave)) {
                return pAux.getInfo().valor;
            }
            pAux = pAux.getSiguiente();
        }
        return null;
    }

    /**
     * Indica si existe una estación registrada bajo la clave indicada.
     *
     * @param sClave clave a verificar
     * @return {@code true} si la clave está presente en la tabla
     */
    public boolean contiene(String sClave) {
        return obtener(sClave) != null;
    }

    /**
     * Elimina la entrada asociada a la clave indicada.
     *
     * @param sClave clave a eliminar
     * @return {@code true} si existía y fue eliminada
     */
    public boolean eliminar(String sClave) {
        int iPos = dispersion(sClave);
        if (this.tabla[iPos] == null) {
            return false;
        }
        boolean bEliminada = this.tabla[iPos].eliminar(new Entrada(sClave, null));
        if (bEliminada) {
            this.iN--;
        }
        return bEliminada;
    }

    /**
     * @return la cantidad de estaciones almacenadas en la tabla
     */
    public int tamano() {
        return this.iN;
    }

    /**
     * @return {@code true} si la tabla no contiene estaciones
     */
    public boolean esVacia() {
        return this.iN == 0;
    }

    /**
     * Retorna todas las estaciones almacenadas en una lista propia. Es el punto
     * de entrada que utilizan el {@link Grafo} y los módulos de lógica para
     * recorrer el universo completo de estaciones sin depender de colecciones de
     * la biblioteca estándar.
     *
     * @return lista con todas las estaciones registradas
     */
    public ListaPropia<Estacion> obtenerTodas() {
        ListaPropia<Estacion> resultado = new ListaPropia<>();
        for (int i = 0; i < this.iCapacidad; i++) {
            if (this.tabla[i] != null) {
                Nodo<Entrada> pAux = this.tabla[i].getCabeza();
                while (pAux != null) {
                    resultado.agregarFinal(pAux.getInfo().valor);
                    pAux = pAux.getSiguiente();
                }
            }
        }
        return resultado;
    }

    /**
     * Duplica aproximadamente la capacidad al siguiente número primo y reubica
     * todas las entradas aplicando de nuevo la función de dispersión.
     */
    @SuppressWarnings("unchecked")
    private void redimensionar() {
        ListaPropia<Entrada>[] anterior = this.tabla;
        int iCapacidadVieja = this.iCapacidad;
        this.iCapacidad = siguientePrimo(this.iCapacidad * 2);
        this.tabla = new ListaPropia[this.iCapacidad];
        this.iN = 0;
        for (int i = 0; i < iCapacidadVieja; i++) {
            if (anterior[i] != null) {
                Nodo<Entrada> pAux = anterior[i].getCabeza();
                while (pAux != null) {
                    insertar(pAux.getInfo().sClave, pAux.getInfo().valor);
                    pAux = pAux.getSiguiente();
                }
            }
        }
    }

    /**
     * Retorna el menor número primo mayor o igual al valor indicado. Usar
     * tamaños primos reduce los patrones de colisión de la función de dispersión.
     *
     * @param iDesde valor mínimo buscado
     * @return el siguiente primo mayor o igual a {@code iDesde}
     */
    private int siguientePrimo(int iDesde) {
        int iCandidato = iDesde;
        while (!esPrimo(iCandidato)) {
            iCandidato++;
        }
        return iCandidato;
    }

    /**
     * Verifica si un número es primo mediante divisiones de prueba.
     *
     * @param iNum número a evaluar
     * @return {@code true} si {@code iNum} es primo
     */
    private boolean esPrimo(int iNum) {
        if (iNum < 2) {
            return false;
        }
        for (int i = 2; (long) i * i <= iNum; i++) {
            if (iNum % i == 0) {
                return false;
            }
        }
        return true;
    }
}
