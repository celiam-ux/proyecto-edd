/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaces;

import Funciones.DatoConexionCsv;
import Funciones.DatoEstacionCsv;
import Funciones.DatosCsv;
import Funciones.ExcepcionArchivoCsv;
import Funciones.MostrarRed;
import Funciones.SesionCsv;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana de trabajo de UrbanRoute. Presenta la sesión CSV, protege los cambios
 * sin guardar y muestra los resultados de cada operación mediante Swing.
 *
 * @author Eliam
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    private final SesionCsv sesion = new SesionCsv();
    private boolean ocupada;

    /** Construye el formulario en el hilo de eventos de Swing. */
    public VentanaPrincipal() {
        this.initComponents();
        this.configurarTabla(this.tablaEstaciones);
        this.configurarTabla(this.tablaConexiones);
        this.actualizarVista();
        this.actualizarConsultasDelGrafo();
        this.setMinimumSize(this.getSize());
        this.setLocationRelativeTo(null);
    }

    private void configurarTabla(JTable tabla) {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                this.actualizarAcciones();
            }
        });
    }

    private void cargarArchivos(ActionEvent evento) {//GEN-FIRST:event_cargarArchivos
        JFileChooser selector = new JFileChooser();
        selector.setAcceptAllFileFilterUsed(false);
        selector.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));
        selector.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (this.sesion.getArchivoEstaciones() != null) {
            selector.setCurrentDirectory(this.sesion.getArchivoEstaciones().getParentFile());
        }
        File estaciones = this.elegirCsv(selector, "Selecciona el CSV de estaciones");
        if (estaciones == null) {
            return;
        }
        File conexiones = this.elegirCsv(selector, "Selecciona el CSV de conexiones");
        if (conexiones == null) {
            return;
        }
        if (this.sesion.getArchivoEstaciones() != null) {
            String mensaje = "¿Quieres reemplazar los datos de la sesión?";
            if (this.sesion.tieneCambios()) {
                mensaje = "Hay cambios sin guardar. Si los nuevos CSV son válidos, perderás esos cambios."
                        + "\nPuedes cancelar y guardar primero.\n" + mensaje;
            }
            if (!this.confirmar("Reemplazar datos", mensaje, "Reemplazar")) {
                return;
            }
        }
        this.ejecutarArchivo("Cargando archivos CSV…", () -> this.sesion.cargar(estaciones, conexiones));
    }//GEN-LAST:event_cargarArchivos

    private File elegirCsv(JFileChooser selector, String titulo) {
        selector.setDialogTitle(titulo);
        selector.setSelectedFile(null);
        int resultado = selector.showDialog(this, "Seleccionar CSV");
        if (resultado == JFileChooser.ERROR_OPTION) {
            this.mostrarError("No se pudo abrir el selector de archivos.");
        }
        return resultado == JFileChooser.APPROVE_OPTION ? selector.getSelectedFile() : null;
    }

    private void guardarArchivos(ActionEvent evento) {//GEN-FIRST:event_guardarArchivos
        this.ejecutarArchivo("Guardando cambios…", this.sesion::guardar);
    }//GEN-LAST:event_guardarArchivos

    private void ejecutarArchivo(String mensaje, AccionCsv accion) {
        this.ocupada = true;
        this.estado.setText(mensaje);
        this.actualizarAcciones();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws ExcepcionArchivoCsv {
                accion.ejecutar();
                return null;
            }

            @Override
            protected void done() {
                String error = null;
                try {
                    this.get();
                } catch (InterruptedException excepcion) {
                    Thread.currentThread().interrupt();
                    error = "Se interrumpió la operación con los archivos.";
                } catch (ExecutionException excepcion) {
                    Throwable causa = excepcion.getCause();
                    error = causa instanceof ExcepcionArchivoCsv ? causa.getMessage()
                            : "No se pudo completar la operación con los archivos.";
                } finally {
                    VentanaPrincipal.this.ocupada = false;
                    VentanaPrincipal.this.actualizarVista();
                }
                if (error != null) {
                    VentanaPrincipal.this.mostrarError(error);
                }
            }
        }.execute();
    }

    private void actualizarVista() {
        DatosCsv datos = this.sesion.getDatos();
        DatoEstacionCsv[] estaciones = datos.getEstaciones();
        Object[][] filasEstaciones = new Object[estaciones.length][];
        for (int indice = 0; indice < estaciones.length; indice++) {
            DatoEstacionCsv estacion = estaciones[indice];
            filasEstaciones[indice] = new Object[]{estacion.getId(), estacion.getNombre(),
                estacion.getLinea(), estacion.getZona(), estacion.getCapacidadPasajerosHora()};
        }
        DatoConexionCsv[] conexiones = datos.getConexiones();
        Object[][] filasConexiones = new Object[conexiones.length][];
        for (int indice = 0; indice < conexiones.length; indice++) {
            DatoConexionCsv conexion = conexiones[indice];
            filasConexiones[indice] = new Object[]{conexion.getIdOrigen(),
                conexion.getIdDestino(), conexion.getTiempoMinutos()};
        }
        this.tablaEstaciones.setModel(this.crearModelo(filasEstaciones,
                "ID", "Nombre", "Línea", "Zona", "Pasajeros por hora"));
        this.tablaConexiones.setModel(this.crearModelo(filasConexiones,
                "Estación de origen", "Estación de destino", "Tiempo (min)"));
        this.actualizarEstado();
        this.actualizarAcciones();
    }

    private DefaultTableModel crearModelo(Object[][] filas, String... columnas) {
        return new DefaultTableModel(filas, columnas) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                return columna == columnas.length - 1 ? Integer.class : String.class;
            }
        };
    }

    private void actualizarEstado() {
        File estaciones = this.sesion.getArchivoEstaciones();
        if (estaciones == null) {
            this.archivos.setText("Ningún archivo seleccionado");
            this.estado.setText("Carga un CSV de estaciones y otro de conexiones para comenzar.");
            return;
        }
        File conexiones = this.sesion.getArchivoConexiones();
        this.archivos.setText("Estaciones: " + estaciones.getName()
                + "   ·   Conexiones: " + conexiones.getName());
        DatosCsv datos = this.sesion.getDatos();
        this.estado.setText(datos.getCantidadEstaciones() + " estaciones   ·   "
                + datos.getCantidadConexiones() + " conexiones   ·   "
                + (this.sesion.tieneCambios() ? "Cambios sin guardar" : "Archivos al día"));
    }

    private void actualizarAcciones() {
        this.cargar.setEnabled(!this.ocupada);
        boolean haySesion = !this.ocupada && this.sesion.getArchivoEstaciones() != null;
        this.guardar.setEnabled(haySesion && this.sesion.tieneCambios());
        this.agregarEstacion.setEnabled(haySesion);
        boolean estacionSeleccionada = haySesion && this.tablaEstaciones.getSelectedRow() >= 0;
        this.editarEstacion.setEnabled(estacionSeleccionada);
        this.eliminarEstacion.setEnabled(estacionSeleccionada);
        this.agregarConexion.setEnabled(haySesion && this.tablaEstaciones.getRowCount() >= 2);
        boolean conexionSeleccionada = haySesion && this.tablaConexiones.getSelectedRow() >= 0;
        this.editarConexion.setEnabled(conexionSeleccionada);
        this.eliminarConexion.setEnabled(conexionSeleccionada);
        this.visualizar.setEnabled(haySesion && this.tablaEstaciones.getRowCount() > 0);
    }

    // Integración con el equipo: habilitar las consultas al conectar el grafo
    // del proyecto y presentar aquí sus resultados. El visor usa la sesión CSV.
    private void actualizarConsultasDelGrafo() {
        this.consultarRuta.setEnabled(false);
        this.consultarSubred.setEnabled(false);
        this.consultarHubs.setEnabled(false);
    }

    private void agregarEstacion(ActionEvent evento) {//GEN-FIRST:event_agregarEstacion
        this.pedirEstacion(-1);
    }//GEN-LAST:event_agregarEstacion

    private void editarEstacion(ActionEvent evento) {//GEN-FIRST:event_editarEstacion
        this.pedirEstacion(this.filaSeleccionada(this.tablaEstaciones));
    }//GEN-LAST:event_editarEstacion

    private void eliminarEstacion(ActionEvent evento) {//GEN-FIRST:event_eliminarEstacion
        int fila = this.filaSeleccionada(this.tablaEstaciones);
        if (this.confirmar("Eliminar estación", "¿Eliminar la estación seleccionada?", "Eliminar")) {
            this.aplicarEdicion(() -> this.sesion.editarEstacion(fila, null));
        }
    }//GEN-LAST:event_eliminarEstacion

    private void pedirEstacion(int fila) {
        DatoEstacionCsv actual = fila < 0 ? null : this.sesion.getDatos().getEstaciones()[fila];
        JTextField id = new JTextField(actual == null ? "" : actual.getId());
        id.setEditable(actual == null);
        JTextField nombre = new JTextField(actual == null ? "" : actual.getNombre());
        JTextField linea = new JTextField(actual == null ? "" : actual.getLinea());
        JTextField zona = new JTextField(actual == null ? "" : actual.getZona());
        JTextField capacidad = new JTextField(actual == null ? "" : "" + actual.getCapacidadPasajerosHora());
        this.pedirCampos(actual == null ? "Agregar estación" : "Editar estación",
                new String[]{"ID", "Nombre", "Línea", "Zona", "Pasajeros por hora"},
                new JTextField[]{id, nombre, linea, zona, capacidad},
                () -> this.sesion.editarEstacion(fila, new DatoEstacionCsv(id.getText(),
                        nombre.getText(), linea.getText(), zona.getText(),
                        this.leerEntero(capacidad.getText(), "La capacidad"))));
    }

    private void agregarConexion(ActionEvent evento) {//GEN-FIRST:event_agregarConexion
        this.pedirConexion(-1);
    }//GEN-LAST:event_agregarConexion

    private void editarConexion(ActionEvent evento) {//GEN-FIRST:event_editarConexion
        this.pedirConexion(this.filaSeleccionada(this.tablaConexiones));
    }//GEN-LAST:event_editarConexion

    private void eliminarConexion(ActionEvent evento) {//GEN-FIRST:event_eliminarConexion
        int fila = this.filaSeleccionada(this.tablaConexiones);
        if (this.confirmar("Eliminar conexión", "¿Eliminar la conexión seleccionada?", "Eliminar")) {
            this.aplicarEdicion(() -> this.sesion.editarConexion(fila, null));
        }
    }//GEN-LAST:event_eliminarConexion

    private void pedirConexion(int fila) {
        DatoConexionCsv actual = fila < 0 ? null : this.sesion.getDatos().getConexiones()[fila];
        DatoEstacionCsv[] estaciones = this.sesion.getDatos().getEstaciones();
        String[] ids = new String[estaciones.length];
        for (int indice = 0; indice < ids.length; indice++) {
            ids[indice] = estaciones[indice].getId();
        }
        javax.swing.JComboBox<String> origen = new javax.swing.JComboBox<>(ids);
        javax.swing.JComboBox<String> destino = new javax.swing.JComboBox<>(ids);
        origen.setSelectedItem(actual == null ? null : actual.getIdOrigen());
        destino.setSelectedItem(actual == null ? null : actual.getIdDestino());
        JTextField tiempo = new JTextField(actual == null ? "" : "" + actual.getTiempoMinutos());
        this.pedirCampos(actual == null ? "Agregar conexión" : "Editar conexión",
                new String[]{"Estación de origen", "Estación de destino", "Tiempo (minutos)"},
                new JComponent[]{origen, destino, tiempo},
                () -> this.sesion.editarConexion(fila, new DatoConexionCsv(
                        (String) origen.getSelectedItem(), (String) destino.getSelectedItem(),
                        this.leerEntero(tiempo.getText(), "El tiempo"))));
    }

    private void pedirCampos(String titulo, String[] etiquetas, JComponent[] campos, AccionCsv accion) {
        JPanel formulario = new JPanel(new GridLayout(0, 1, 0, 4));
        formulario.setPreferredSize(new Dimension(360, campos.length * 55));
        for (int indice = 0; indice < campos.length; indice++) {
            JLabel etiqueta = new JLabel(etiquetas[indice]);
            etiqueta.setLabelFor(campos[indice]);
            formulario.add(etiqueta);
            formulario.add(campos[indice]);
        }
        Object[] opciones = {"Aplicar", "Cancelar"};
        while (JOptionPane.showOptionDialog(this, formulario, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, opciones, opciones[0]) == JOptionPane.OK_OPTION) {
            if (this.aplicarEdicion(accion)) {
                return;
            }
        }
    }

    private boolean aplicarEdicion(AccionCsv accion) {
        try {
            accion.ejecutar();
            this.actualizarVista();
            return true;
        } catch (ExcepcionArchivoCsv | IllegalArgumentException excepcion) {
            this.mostrarError(excepcion.getMessage());
            return false;
        }
    }

    private int leerEntero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException excepcion) {
            throw new IllegalArgumentException(campo + " debe ser un número entero válido.", excepcion);
        }
    }

    private int filaSeleccionada(JTable tabla) {
        return tabla.convertRowIndexToModel(tabla.getSelectedRow());
    }

    private void abrirVisualizacion(ActionEvent evento) {//GEN-FIRST:event_abrirVisualizacion
        try {
            new MostrarRed().abrir(this, this.sesion.getDatos());
        } catch (RuntimeException | LinkageError excepcion) {
            this.mostrarError("No se pudo abrir la visualización de la red.");
        }
    }//GEN-LAST:event_abrirVisualizacion

    private void cerrarVentana(WindowEvent evento) {//GEN-FIRST:event_cerrarVentana
        if (!this.ocupada && (!this.sesion.tieneCambios() || this.confirmar("Cambios sin guardar",
                "Hay cambios sin guardar. ¿Quieres cerrar la aplicación?", "Cerrar"))) {
            this.dispose();
        }
    }//GEN-LAST:event_cerrarVentana

    private boolean confirmar(String titulo, String mensaje, String aceptar) {
        Object[] opciones = {aceptar, "Cancelar"};
        return JOptionPane.showOptionDialog(this, mensaje, titulo, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, opciones, opciones[1]) == JOptionPane.OK_OPTION;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "UrbanRoute", JOptionPane.ERROR_MESSAGE);
    }

    /** Inicia el formulario con Nimbus cuando está disponible. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new javax.swing.plaf.nimbus.NimbusLookAndFeel());
            } catch (UnsupportedLookAndFeelException excepcion) {
                // Swing conserva el tema disponible.
            }
            new VentanaPrincipal().setVisible(true);
        });
    }

    @FunctionalInterface
    private interface AccionCsv {

        void ejecutar() throws ExcepcionArchivoCsv;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        contenido = new javax.swing.JPanel();
        encabezado = new javax.swing.JPanel();
        marca = new javax.swing.JPanel();
        titulo = new javax.swing.JLabel();
        subtitulo = new javax.swing.JLabel();
        archivosAcciones = new javax.swing.JPanel();
        cargar = new javax.swing.JButton();
        guardar = new javax.swing.JButton();
        archivos = new javax.swing.JLabel();
        secciones = new javax.swing.JTabbedPane();
        estaciones = new javax.swing.JPanel();
        desplazamientoEstaciones = new javax.swing.JScrollPane();
        tablaEstaciones = new javax.swing.JTable();
        accionesEstaciones = new javax.swing.JPanel();
        agregarEstacion = new javax.swing.JButton();
        editarEstacion = new javax.swing.JButton();
        eliminarEstacion = new javax.swing.JButton();
        conexiones = new javax.swing.JPanel();
        desplazamientoConexiones = new javax.swing.JScrollPane();
        tablaConexiones = new javax.swing.JTable();
        accionesConexiones = new javax.swing.JPanel();
        agregarConexion = new javax.swing.JButton();
        editarConexion = new javax.swing.JButton();
        eliminarConexion = new javax.swing.JButton();
        pie = new javax.swing.JPanel();
        consultas = new javax.swing.JPanel();
        visualizar = new javax.swing.JButton();
        consultarRuta = new javax.swing.JButton();
        consultarSubred = new javax.swing.JButton();
        consultarHubs = new javax.swing.JButton();
        avisoConsultas = new javax.swing.JLabel();
        estado = new javax.swing.JLabel();

        setDefaultCloseOperation(0);
        setTitle("UrbanRoute · Red de transporte");
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                cerrarVentana(evt);
            }
        });
        getContentPane().setLayout(new java.awt.BorderLayout(0, 0));

        contenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 18, 14, 18));
        contenido.setLayout(new java.awt.BorderLayout(0, 12));

        encabezado.setLayout(new java.awt.BorderLayout(0, 10));

        marca.setBackground(new java.awt.Color(20, 44, 70));
        marca.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 18, 16, 18));
        marca.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        titulo.setText("UrbanRoute");
        titulo.setForeground(new java.awt.Color(255, 255, 255));
        titulo.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 30));
        marca.add(titulo);

        subtitulo.setText("RED DE TRANSPORTE  /  ESTACIONES Y CONEXIONES");
        subtitulo.setForeground(new java.awt.Color(184, 230, 229));
        subtitulo.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
        marca.add(subtitulo);
        encabezado.add(marca, java.awt.BorderLayout.NORTH);

        archivosAcciones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));

        cargar.setText("Cargar archivos CSV");
        cargar.setEnabled(false);
        cargar.addActionListener(this::cargarArchivos);
        archivosAcciones.add(cargar);

        guardar.setText("Guardar cambios");
        guardar.setEnabled(false);
        guardar.addActionListener(this::guardarArchivos);
        archivosAcciones.add(guardar);

        archivos.setText("Ningún archivo seleccionado");
        archivosAcciones.add(archivos);
        encabezado.add(archivosAcciones, java.awt.BorderLayout.CENTER);
        contenido.add(encabezado, java.awt.BorderLayout.NORTH);


        estaciones.setLayout(new java.awt.BorderLayout(0, 0));


        tablaEstaciones.setRowHeight(26);
        tablaEstaciones.setPreferredScrollableViewportSize(new java.awt.Dimension(840, 320));
        tablaEstaciones.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[]{"ID", "Nombre", "Línea", "Zona", "Pasajeros por hora"}));
        desplazamientoEstaciones.setViewportView(tablaEstaciones);
        estaciones.add(desplazamientoEstaciones, java.awt.BorderLayout.CENTER);

        accionesEstaciones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));

        agregarEstacion.setText("Agregar estación");
        agregarEstacion.setEnabled(false);
        agregarEstacion.addActionListener(this::agregarEstacion);
        accionesEstaciones.add(agregarEstacion);

        editarEstacion.setText("Editar");
        editarEstacion.setEnabled(false);
        editarEstacion.addActionListener(this::editarEstacion);
        accionesEstaciones.add(editarEstacion);

        eliminarEstacion.setText("Eliminar");
        eliminarEstacion.setEnabled(false);
        eliminarEstacion.addActionListener(this::eliminarEstacion);
        accionesEstaciones.add(eliminarEstacion);
        estaciones.add(accionesEstaciones, java.awt.BorderLayout.SOUTH);
        secciones.addTab("Estaciones", estaciones);

        conexiones.setLayout(new java.awt.BorderLayout(0, 0));


        tablaConexiones.setRowHeight(26);
        tablaConexiones.setPreferredScrollableViewportSize(new java.awt.Dimension(840, 320));
        tablaConexiones.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[]{"Estación de origen", "Estación de destino", "Tiempo (min)"}));
        desplazamientoConexiones.setViewportView(tablaConexiones);
        conexiones.add(desplazamientoConexiones, java.awt.BorderLayout.CENTER);

        accionesConexiones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));

        agregarConexion.setText("Agregar conexión");
        agregarConexion.setEnabled(false);
        agregarConexion.addActionListener(this::agregarConexion);
        accionesConexiones.add(agregarConexion);

        editarConexion.setText("Editar");
        editarConexion.setEnabled(false);
        editarConexion.addActionListener(this::editarConexion);
        accionesConexiones.add(editarConexion);

        eliminarConexion.setText("Eliminar");
        eliminarConexion.setEnabled(false);
        eliminarConexion.addActionListener(this::eliminarConexion);
        accionesConexiones.add(eliminarConexion);
        conexiones.add(accionesConexiones, java.awt.BorderLayout.SOUTH);
        secciones.addTab("Conexiones", conexiones);
        contenido.add(secciones, java.awt.BorderLayout.CENTER);

        pie.setLayout(new java.awt.BorderLayout(0, 8));

        consultas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));

        visualizar.setText("Abrir mapa de la red");
        visualizar.setEnabled(false);
        visualizar.addActionListener(this::abrirVisualizacion);
        consultas.add(visualizar);

        consultarRuta.setText("Rutas");
        consultarRuta.setToolTipText("Pendiente de integrar el grafo del equipo");
        consultarRuta.setEnabled(false);
        consultas.add(consultarRuta);

        consultarSubred.setText("Subredes");
        consultarSubred.setToolTipText("Pendiente de integrar el grafo del equipo");
        consultarSubred.setEnabled(false);
        consultas.add(consultarSubred);

        consultarHubs.setText("Hubs");
        consultarHubs.setToolTipText("Pendiente de integrar el grafo del equipo");
        consultarHubs.setEnabled(false);
        consultas.add(consultarHubs);

        avisoConsultas.setText("Consultas pendientes de integración");
        consultas.add(avisoConsultas);
        pie.add(consultas, java.awt.BorderLayout.NORTH);

        estado.setText("Carga un CSV de estaciones y otro de conexiones para comenzar.");
        estado.setBackground(new java.awt.Color(224, 238, 244));
        estado.setForeground(new java.awt.Color(20, 44, 70));
        estado.setOpaque(true);
        estado.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        estado.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        pie.add(estado, java.awt.BorderLayout.SOUTH);
        contenido.add(pie, java.awt.BorderLayout.SOUTH);
        getContentPane().add(contenido, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel accionesConexiones;
    private javax.swing.JPanel accionesEstaciones;
    private javax.swing.JButton agregarConexion;
    private javax.swing.JButton agregarEstacion;
    private javax.swing.JLabel archivos;
    private javax.swing.JPanel archivosAcciones;
    private javax.swing.JLabel avisoConsultas;
    private javax.swing.JButton cargar;
    private javax.swing.JPanel conexiones;
    private javax.swing.JButton consultarHubs;
    private javax.swing.JButton consultarRuta;
    private javax.swing.JButton consultarSubred;
    private javax.swing.JPanel consultas;
    private javax.swing.JPanel contenido;
    private javax.swing.JScrollPane desplazamientoConexiones;
    private javax.swing.JScrollPane desplazamientoEstaciones;
    private javax.swing.JButton editarConexion;
    private javax.swing.JButton editarEstacion;
    private javax.swing.JButton eliminarConexion;
    private javax.swing.JButton eliminarEstacion;
    private javax.swing.JPanel encabezado;
    private javax.swing.JPanel estaciones;
    private javax.swing.JLabel estado;
    private javax.swing.JButton guardar;
    private javax.swing.JPanel marca;
    private javax.swing.JPanel pie;
    private javax.swing.JTabbedPane secciones;
    private javax.swing.JLabel subtitulo;
    private javax.swing.JTable tablaConexiones;
    private javax.swing.JTable tablaEstaciones;
    private javax.swing.JLabel titulo;
    private javax.swing.JButton visualizar;
    // End of variables declaration//GEN-END:variables
}
