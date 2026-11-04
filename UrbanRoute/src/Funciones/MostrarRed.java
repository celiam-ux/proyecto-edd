/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.LayoutRunner;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

/**
 * Proyecta las filas CSV en GraphStream para inspección visual. El visor es
 * independiente del grafo que resolverá las consultas de UrbanRoute.
 *
 * @author Eliam
 */
public class MostrarRed {

    private static final Logger REGISTRO_DISENO = Logger.getLogger(LayoutRunner.class.getSimpleName());
    private static final String ESTILO = """
            graph { fill-color: #f2f6fa; padding: 52px; }
            node { fill-color: #097b7a; size: 14px; stroke-mode: plain; stroke-color: #ffffff;
                   text-color: #142c46; text-style: bold; text-size: 13px; text-alignment: above; }
            edge { fill-color: #7a90a5; size: 2px; arrow-size: 8px, 5px;
                   text-color: #203a56; text-background-mode: rounded-box;
                   text-background-color: #ffffff; text-size: 11px; }
            """;

    /** Abre el visor en el hilo de Swing y libera sus recursos al regresar. */
    public void abrir(JFrame ventana, DatosCsv datos) {
        Graph red = this.crearRed(datos);
        // GraphStream registra el cierre normal del diseño en la consola.
        REGISTRO_DISENO.setLevel(Level.WARNING);
        SwingViewer visor = new SwingViewer(red, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        try {
            JDialog dialogo = new JDialog(ventana, "UrbanRoute · Mapa de la red", true);
            dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            ViewPanel vista = (ViewPanel) visor.addDefaultView(false);
            vista.setPreferredSize(new Dimension(820, 520));
            JButton regresar = new JButton("Regresar");
            regresar.addActionListener(evento -> dialogo.dispose());
            JPanel acciones = new JPanel();
            acciones.add(regresar);
            dialogo.add(vista, BorderLayout.CENTER);
            dialogo.add(acciones, BorderLayout.SOUTH);
            dialogo.pack();
            dialogo.setLocationRelativeTo(ventana);
            visor.enableAutoLayout();
            dialogo.setVisible(true);
        } finally {
            visor.close();
        }
    }

    /** Construye únicamente la representación que consume el visor. */
    Graph crearRed(DatosCsv datos) {
        Graph red = new MultiGraph("UrbanRoute");
        red.setAttribute("ui.stylesheet", ESTILO);
        red.setAttribute("ui.antialias");
        for (DatoEstacionCsv estacion : datos.getEstaciones()) {
            Node nodo = red.addNode(estacion.getId());
            nodo.setAttribute("ui.label", estacion.getId());
        }
        int indice = 0;
        for (DatoConexionCsv conexion : datos.getConexiones()) {
            Edge arista = red.addEdge("conexion-" + indice++, conexion.getIdOrigen(),
                    conexion.getIdDestino(), true);
            arista.setAttribute("ui.label", conexion.getTiempoMinutos() + " min");
        }
        return red;
    }
}
