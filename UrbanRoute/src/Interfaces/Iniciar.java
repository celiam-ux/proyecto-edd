/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaces;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Ventana inicial de UrbanRoute.
 *
 * @author Eliam
 */
public class Iniciar extends JFrame {

    private final JLabel titulo;

    /**
     * Construye la ventana inicial de la aplicacion.
     */
    public Iniciar() {
        this.titulo = new JLabel("UrbanRoute", SwingConstants.CENTER);
        this.configurarVentana();
    }

    private void configurarVentana() {
        this.setTitle("UrbanRoute");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(640, 400));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.add(this.titulo, BorderLayout.CENTER);
    }
}
