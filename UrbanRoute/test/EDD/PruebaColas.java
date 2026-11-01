/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author Eliam
 */
public class PruebaColas {

    private int verificaciones;

    public static void main(String[] args) {
        PruebaColas pruebas = new PruebaColas();
        pruebas.ejecutar();
    }

    private void ejecutar() {
        this.probarFifo();
        this.probarVaciado();
        this.probarPrioridadesDesordenadas();
        this.probarEmpates();
        this.probarPrioridadesInvalidas();
        this.probarVaciadoPrioridad();
        System.out.println("Pruebas de Cola y ColaPrioridad: " + this.verificaciones
                + " verificaciones correctas.");
    }

    private void probarFifo() {
        Cola cola = new Cola();
        this.verificarCola(cola);
        this.verificar(cola.desencolar() == null, "Desencolar vacia");
        this.verificarCola(cola);
        this.verificar("null".equals(cola.toString()), "Representacion vacia");
        cola.encolar("A");
        this.verificarCola(cola, "A");
        cola.encolar(null);
        this.verificarCola(cola, "A", null);
        cola.encolar("B");
        this.verificarCola(cola, "A", null, "B");
        this.verificar("A -> null -> B -> null".equals(cola.toString()),
                "Representacion FIFO");
        Nodo eliminado = cola.getCabeza();
        this.verificar("A".equals(cola.desencolar()), "FIFO extrae el primero");
        this.verificar(eliminado.getpNext() == null, "Nodo extraido separado");
        this.verificarCola(cola, null, "B");
        cola.encolar("C");
        this.verificarCola(cola, null, "B", "C");
        this.verificar(cola.desencolar() == null, "Extraer dato null");
        this.verificarCola(cola, "B", "C");
        this.verificar("B".equals(cola.desencolar()), "FIFO conserva B");
        this.verificarCola(cola, "C");
        this.verificar("C".equals(cola.desencolar()), "Extraer ultimo");
        this.verificarCola(cola);
        this.verificar(cola.desencolar() == null, "Extraccion completa no resta de mas");
        this.verificarCola(cola);
        cola.encolar("Nueva");
        this.verificarCola(cola, "Nueva");
        this.verificar("Nueva".equals(cola.desencolar()), "Reutilizar cola vaciada");
        this.verificarCola(cola);
    }

    private void probarVaciado() {
        Cola cola = new Cola();
        cola.destruir();
        this.verificarCola(cola);
        cola.encolar("A");
        cola.encolar("B");
        Nodo primero = cola.getCabeza();
        Nodo ultimo = cola.getCola();
        Cola independiente = new Cola();
        independiente.encolar("Otra");
        cola.destruir();
        this.verificarCola(cola);
        this.verificar(primero.getpNext() == null && ultimo.getpNext() == null,
                "Destruir separa los enlaces");
        this.verificarCola(independiente, "Otra");
        cola.destruir();
        this.verificarCola(cola);
        cola.encolar("Reutilizada");
        this.verificarCola(cola, "Reutilizada");
        cola.destruir();
        this.verificarCola(cola);
    }

    private void probarPrioridadesDesordenadas() {
        ColaPrioridad cola = new ColaPrioridad();
        this.verificarPrioridad(cola, new double[0]);
        this.verificar(cola.desencolar() == null, "Extraer prioridad vacia");
        this.verificarPrioridad(cola, new double[0]);
        this.verificar("null".equals(cola.toString()), "Prioridad vacia representada");
        cola.encolar("Lejana", 9.5);
        this.verificarPrioridad(cola, new double[]{9.5}, "Lejana");
        cola.encolar("Cercana", 1.25);
        this.verificarPrioridad(cola, new double[]{1.25, 9.5}, "Cercana", "Lejana");
        cola.encolar("Media", 4);
        this.verificarPrioridad(cola, new double[]{1.25, 4, 9.5},
                "Cercana", "Media", "Lejana");
        cola.encolar("Ultima", Double.MAX_VALUE);
        cola.encolar("Primera", -Double.MAX_VALUE);
        cola.encolar(null, 0);
        double[] prioridades = {-Double.MAX_VALUE, 0, 1.25, 4, 9.5, Double.MAX_VALUE};
        Object[] esperados = {"Primera", null, "Cercana", "Media", "Lejana", "Ultima"};
        this.verificarPrioridad(cola, prioridades, esperados);
        this.verificar(cola.toString().contains("Cercana (1.25) -> Media (4.0)"),
                "Representacion muestra prioridades en orden");
        for (int indice = 0; indice < esperados.length; indice++) {
            NodoPrioridad eliminado = cola.getCabeza();
            this.verificar(eliminado.getPrioridad() == prioridades[indice],
                    "Prioridad minima antes de extraer");
            this.verificarDato(esperados[indice], cola.desencolar());
            this.verificar(eliminado.getpNext() == null, "Separar nodo de prioridad");
            this.verificar(cola.getSize() == esperados.length - indice - 1,
                    "Tamano despues de cada extraccion");
        }
        this.verificarPrioridad(cola, new double[0]);
        this.verificar(cola.desencolar() == null, "Extraer despues de agotarla");
        this.verificarPrioridad(cola, new double[0]);
        cola.encolar("Nueva", 2);
        this.verificarPrioridad(cola, new double[]{2}, "Nueva");
        this.verificarDato("Nueva", cola.desencolar());
        this.verificarPrioridad(cola, new double[0]);
    }

    private void probarEmpates() {
        ColaPrioridad cola = new ColaPrioridad();
        Object repetido = new Object();
        cola.encolar("A", 2);
        cola.encolar("B", 2);
        cola.encolar(repetido, 5);
        cola.encolar("C", 2);
        cola.encolar(repetido, 1);
        cola.encolar("D", 5);
        cola.encolar("Cero negativo", -0.0);
        cola.encolar("Cero positivo", 0.0);
        Object[] esperados = {"Cero negativo", "Cero positivo", repetido,
            "A", "B", "C", repetido, "D"};
        this.verificarPrioridad(cola, new double[]{0, 0, 1, 2, 2, 2, 5, 5}, esperados);
        for (Object esperado : esperados) {
            this.verificarDato(esperado, cola.desencolar());
        }
        this.verificarPrioridad(cola, new double[0]);

        cola.encolar("Primera", 7);
        cola.encolar("Segunda", 7);
        this.verificarDato("Primera", cola.desencolar());
        cola.encolar("Tercera", 7);
        this.verificarPrioridad(cola, new double[]{7, 7}, "Segunda", "Tercera");
        this.verificarDato("Segunda", cola.desencolar());
        this.verificarDato("Tercera", cola.desencolar());
        this.verificarPrioridad(cola, new double[0]);
    }

    private void probarPrioridadesInvalidas() {
        ColaPrioridad cola = new ColaPrioridad();
        double[] invalidas = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (double prioridad : invalidas) {
            this.verificarPrioridadInvalida(cola, prioridad);
            this.verificarPrioridad(cola, new double[0]);
        }
        cola.encolar("Valida", 3);
        for (double prioridad : invalidas) {
            this.verificarPrioridadInvalida(cola, prioridad);
            this.verificarPrioridad(cola, new double[]{3}, "Valida");
        }
    }

    private void probarVaciadoPrioridad() {
        ColaPrioridad cola = new ColaPrioridad();
        cola.destruir();
        this.verificarPrioridad(cola, new double[0]);
        cola.encolar("B", 2);
        cola.encolar("A", 1);
        NodoPrioridad primero = cola.getCabeza();
        NodoPrioridad ultimo = cola.getCola();
        ColaPrioridad independiente = new ColaPrioridad();
        independiente.encolar("Otra", 4);
        cola.destruir();
        this.verificarPrioridad(cola, new double[0]);
        this.verificar(primero.getpNext() == null && ultimo.getpNext() == null,
                "Destruir prioridad separa los enlaces");
        this.verificarPrioridad(independiente, new double[]{4}, "Otra");
        cola.destruir();
        this.verificarPrioridad(cola, new double[0]);
        cola.encolar("Reutilizada", 0);
        this.verificarPrioridad(cola, new double[]{0}, "Reutilizada");
        cola.destruir();
        this.verificarPrioridad(cola, new double[0]);
    }

    // Los esperados limitan el recorrido para detectar nodos sobrantes y ciclos.
    private void verificarCola(Cola cola, Object... esperados) {
        this.verificar(cola.getSize() == esperados.length, "Tamano FIFO exacto");
        this.verificar(cola.isEmpty() == (esperados.length == 0), "Vacio FIFO coherente");
        this.verificar((cola.getCabeza() == null) == (esperados.length == 0),
                "Cabeza FIFO coherente");
        Nodo temp = cola.getCabeza();
        Nodo ultimo = null;
        for (Object esperado : esperados) {
            this.verificar(temp != null, "Enlace FIFO presente");
            this.verificarDato(esperado, temp.getDato());
            ultimo = temp;
            temp = temp.getpNext();
        }
        this.verificar(temp == null, "FIFO sin nodos sobrantes ni ciclos");
        this.verificar(cola.getCola() == ultimo, "Cola FIFO apunta al ultimo o null");
    }

    private void verificarPrioridad(ColaPrioridad cola, double[] prioridades,
            Object... esperados) {
        this.verificar(prioridades.length == esperados.length, "Caso de prueba completo");
        this.verificar(cola.getSize() == esperados.length, "Tamano de prioridad exacto");
        this.verificar(cola.isEmpty() == (esperados.length == 0), "Vacio de prioridad coherente");
        this.verificar((cola.getCabeza() == null) == (esperados.length == 0),
                "Cabeza de prioridad coherente");
        NodoPrioridad temp = cola.getCabeza();
        NodoPrioridad ultimo = null;
        for (int indice = 0; indice < esperados.length; indice++) {
            this.verificar(temp != null, "Enlace de prioridad presente");
            this.verificarDato(esperados[indice], temp.getDato());
            this.verificar(temp.getPrioridad() == prioridades[indice], "Prioridad esperada");
            ultimo = temp;
            temp = temp.getpNext();
        }
        this.verificar(temp == null, "Prioridad sin nodos sobrantes ni ciclos");
        this.verificar(cola.getCola() == ultimo, "Cola de prioridad apunta al ultimo o null");
    }

    private void verificarPrioridadInvalida(ColaPrioridad cola, double prioridad) {
        try {
            cola.encolar("Invalida", prioridad);
        } catch (IllegalArgumentException excepcion) {
            this.verificar(true, "Prioridad no finita rechazada");
            return;
        }
        throw new AssertionError("Se acepto una prioridad no finita");
    }

    private void verificarDato(Object esperado, Object obtenido) {
        this.verificar(esperado == null ? obtenido == null : esperado.equals(obtenido),
                "Dato extraido o enlazado en orden");
    }

    private void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
        this.verificaciones++;
    }
}
