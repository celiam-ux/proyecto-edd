/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funciones;

import estructuras.Arista;
import estructuras.Estacion;
import estructuras.Grafo;
import estructuras.ListaPropia;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseWheelEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.LayoutRunner;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;

/**
 * Proyecta en GraphStream la misma red que usan las consultas de UrbanRoute.
 *
 * @author Eliam
 */
public class MostrarRed {

    private static final Logger REGISTRO_DISENO = Logger.getLogger(LayoutRunner.class.getSimpleName());
    private static final String[] PALETA = {
        "#1f5aa6", "#b44620", "#00796b", "#79379b", "#936000", "#b63067"
    };
    private static final String COLOR_TRANSBORDO = "#535d6b";
    private static final String ESTILO = """
            graph { fill-color: #f2f6fa; padding: 72px, 48px; }
            node { size: 13px; stroke-mode: plain; stroke-color: #ffffff; stroke-width: 2px;
                   text-color: #142c46; text-style: bold; text-size: 12px;
                   text-alignment: above; text-padding: 2px, 1px;
                   text-background-mode: rounded-box; text-background-color: #f2f6fa; }
            edge { size: 2px;
                   text-color: #203a56; text-background-mode: rounded-box;
                   text-background-color: #ffffff; text-padding: 2px, 1px; text-size: 11px; }
            """;

    /**
     * Abre un mapa modal con los datos vigentes y libera el visor al regresar.
     *
     * @param ventana ventana principal que recupera el control al cerrar el mapa
     * @param grafo red vigente de la sesión, incluso si hay cambios por guardar
     */
    public void abrir(JFrame ventana, Grafo grafo) {
        Graph red = this.crearRed(grafo);
        JDialog dialogo = new JDialog(ventana, "UrbanRoute · Mapa de la red", true);
        // GraphStream registra el cierre normal del diseño en la consola.
        REGISTRO_DISENO.setLevel(Level.WARNING);
        SwingViewer visor = new SwingViewer(red, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD) {
            @Override
            public void close() {
                // La versión 2.0 retira el listener, pero deja el temporizador activo.
                this.timer.stop();
                super.close();
            }
        };
        try {
            dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            ViewPanel vista = (ViewPanel) visor.addDefaultView(false);
            vista.setPreferredSize(new Dimension(1020, 650));
            vista.setFocusable(true);
            dialogo.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
                @Override
                public Component getDefaultComponent(Container contenedor) {
                    return vista;
                }
            });
            vista.addMouseWheelListener(evento -> this.acercarAlCursor(vista, evento));
            dialogo.add(vista, BorderLayout.CENTER);
            dialogo.add(this.crearLeyenda(red.getAttribute("lineas.csv", String[].class)), BorderLayout.EAST);
            dialogo.add(this.crearAcciones(dialogo, vista), BorderLayout.SOUTH);
            dialogo.pack();
            Dimension pantalla = ventana.getGraphicsConfiguration().getBounds().getSize();
            dialogo.setSize(Math.min(dialogo.getWidth(), pantalla.width - 48),
                    Math.min(dialogo.getHeight(), pantalla.height - 96));
            dialogo.setLocationRelativeTo(ventana);
            visor.enableAutoLayout();
            dialogo.setVisible(true);
        } finally {
            dialogo.dispose();
            visor.close();
        }
    }

    /**
     * @param grafo red ponderada no dirigida que se va a dibujar
     * @return representación para GraphStream
     */
    Graph crearRed(Grafo grafo) {
        Graph red = new SingleGraph("UrbanRoute");
        ListaPropia<String> ids = grafo.getIdsEstaciones();
        String[] lineas = new String[ids.tamano()];
        StringBuilder estilo = new StringBuilder(ESTILO);
        estilo.append("edge.transbordo { fill-color: ").append(COLOR_TRANSBORDO).append("; }\n");
        red.setAttribute("ui.antialias");
        for (estructuras.Nodo<String> actual = ids.getCabeza(); actual != null; actual = actual.getSiguiente()) {
            Estacion estacion = grafo.getEstacion(actual.getInfo());
            int indiceLinea = this.indiceDeLinea(lineas, estacion.getLinea());
            String clase = "linea" + indiceLinea;
            if (lineas[indiceLinea] == null) {
                lineas[indiceLinea] = estacion.getLinea();
                String color = this.colorLinea(indiceLinea);
                estilo.append("node.").append(clase).append(" { fill-color: ").append(color).append("; }\n");
                estilo.append("edge.").append(clase).append(" { fill-color: ").append(color).append("; }\n");
            }
            Node nodo = red.addNode(estacion.getId());
            nodo.setAttribute("ui.label", estacion.getNombre());
            nodo.setAttribute("ui.class", clase);
        }
        int indice = 0;
        for (estructuras.Nodo<String> actual = ids.getCabeza(); actual != null; actual = actual.getSiguiente()) {
            String origen = actual.getInfo();
            for (estructuras.Nodo<Arista> vecino = grafo.getVecinos(origen).getCabeza();
                    vecino != null; vecino = vecino.getSiguiente()) {
                Arista tramo = vecino.getInfo();
                // Cada tramo está en la lista de sus dos extremos.
                if (origen.compareTo(tramo.getDestino()) < 0) {
                    Edge arista = red.addEdge("conexion-" + indice++, origen, tramo.getDestino());
                    String lineaOrigen = arista.getSourceNode().getAttribute("ui.class", String.class);
                    String lineaDestino = arista.getTargetNode().getAttribute("ui.class", String.class);
                    arista.setAttribute("ui.class", lineaOrigen.equals(lineaDestino) ? lineaOrigen : "transbordo");
                    arista.setAttribute("ui.label", tramo.getPeso() + " min");
                }
            }
        }
        red.setAttribute("ui.stylesheet", estilo.toString());
        red.setAttribute("lineas.csv", (Object) lineas);
        return red;
    }

    private int indiceDeLinea(String[] lineas, String linea) {
        int indice = 0;
        while (lineas[indice] != null && !lineas[indice].equals(linea)) {
            indice++;
        }
        return indice;
    }

    private String colorLinea(int indice) {
        if (indice < PALETA.length) {
            return PALETA[indice];
        }
        float tono = (float) ((indice * 0.61803398875) % 1);
        return String.format("#%06x", Color.HSBtoRGB(tono, 0.68f, 0.62f) & 0xffffff);
    }

    private JPanel crearLeyenda(String[] lineas) {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        for (int indice = 0; indice < lineas.length && lineas[indice] != null; indice++) {
            contenido.add(this.crearMuestra(lineas[indice], this.colorLinea(indice)));
        }
        contenido.add(this.crearMuestra("Transbordo", COLOR_TRANSBORDO));
        JScrollPane desplazamiento = new JScrollPane(contenido);
        desplazamiento.setBorder(null);
        JPanel leyenda = new JPanel(new BorderLayout());
        leyenda.setBorder(BorderFactory.createTitledBorder("Líneas"));
        leyenda.setPreferredSize(new Dimension(170, 0));
        leyenda.add(desplazamiento, BorderLayout.CENTER);
        return leyenda;
    }

    private JPanel crearMuestra(String texto, String color) {
        JLabel muestra = new JLabel("●");
        muestra.setForeground(Color.decode(color));
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        fila.add(muestra);
        fila.add(new JLabel(texto));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, fila.getPreferredSize().height));
        return fila;
    }

    private JPanel crearAcciones(JDialog dialogo, ViewPanel vista) {
        JButton acercar = new JButton("Acercar");
        acercar.addActionListener(evento -> this.cambiarZoom(vista, 0.8));
        JButton alejar = new JButton("Alejar");
        alejar.addActionListener(evento -> this.cambiarZoom(vista, 1.25));
        JButton ajustar = new JButton("Ver toda la red");
        ajustar.addActionListener(evento -> {
            vista.getCamera().resetView();
            vista.requestFocusInWindow();
            vista.repaint();
        });
        JButton regresar = new JButton("Regresar");
        regresar.addActionListener(evento -> dialogo.dispose());
        JPanel acciones = new JPanel();
        acciones.add(new JLabel("Rueda para ampliar · Flechas para mover"));
        acciones.add(acercar);
        acciones.add(alejar);
        acciones.add(ajustar);
        acciones.add(regresar);
        return acciones;
    }

    private void cambiarZoom(ViewPanel vista, double factor) {
        Camera camara = vista.getCamera();
        camara.setViewPercent(this.limitarZoom(camara.getViewPercent() * factor));
        vista.requestFocusInWindow();
        vista.repaint();
    }

    private void acercarAlCursor(ViewPanel vista, MouseWheelEvent evento) {
        Camera camara = vista.getCamera();
        double zoomAnterior = camara.getViewPercent();
        double zoomNuevo = this.limitarZoom(zoomAnterior * Math.pow(1.2, evento.getPreciseWheelRotation()));
        Point3 punto = camara.transformPxToGu(evento.getX(), evento.getY());
        Point3 centro = camara.getViewCenter();
        // Compensa el cambio de escala para mantener el punto bajo el cursor.
        double proporcion = zoomNuevo / zoomAnterior;
        camara.setViewCenter(punto.x + (centro.x - punto.x) * proporcion,
                punto.y + (centro.y - punto.y) * proporcion, centro.z);
        camara.setViewPercent(zoomNuevo);
        vista.requestFocusInWindow();
        vista.repaint();
    }

    private double limitarZoom(double zoom) {
        return Math.max(0.12, Math.min(1.5, zoom));
    }
}
