/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto.urbanroute;

import Interfaces.Iniciar;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la aplicacion UrbanRoute.
 *
 * @author Eliam
 */
public class Main {

    /**
     * Inicia la interfaz grafica en el hilo de eventos de Swing.
     *
     * @param args argumentos de inicio; no se utilizan
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Iniciar inicio = new Iniciar();
            inicio.setVisible(true);
        });
    }
}
