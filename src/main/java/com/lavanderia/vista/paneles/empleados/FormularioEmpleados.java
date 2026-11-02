
package com.lavanderia.vista.paneles.empleados;

import com.lavanderia.modelo.interfaces.IEmpleado;
import com.lavanderia.servicio.interfaces.IGestorPersonas;
import com.lavanderia.validacion.ResultadoValidacion;
import com.lavanderia.validacion.ValidacionException;
import com.lavanderia.validacion.persona.IPersonaValidator;
import com.lavanderia.vista.componentes.CampoTexto;
import com.lavanderia.vista.paneles.comunes.PanelFormulario;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

import static com.lavanderia.vista.estilos.EstilosUI.*;

/** Recoge los datos del formulario de empleados y coordina las acciones de esa pantalla. */
public class FormularioEmpleados extends JDialog {

    private final IGestorPersonas gestorPersonas;
    private final IPersonaValidator validador;
    private final Runnable alGuardar;

    private final IEmpleado empleadoOriginal;
    private final String identificacionOriginal;

    private final CampoTexto campoIdentificacion;
    private final CampoTexto campoNombre;
    private final CampoTexto campoDireccion;
    private final CampoTexto campoTelefono;
    private final CampoTexto campoCorreo;
    private final CampoTexto campoCargo;

    private final PanelFormulario formulario;

    // ==========================================
    // CONSTRUCTOR DE REGISTRO
    // ==========================================

    public FormularioEmpleados(
            Window propietario,
            IGestorPersonas gestorPersonas,
            IPersonaValidator validador,
            Runnable alGuardar) {

        this(
                propietario,
                gestorPersonas,
                validador,
                null,
                alGuardar
        );
    }

    // ==========================================
    // CONSTRUCTOR DE REGISTRO Y EDICIÓN
    // ==========================================

    public FormularioEmpleados(
            Window propietario,
            IGestorPersonas gestorPersonas,
            IPersonaValidator validador,
            IEmpleado empleado,
            Runnable alGuardar) {

        super(
                propietario,
                empleado == null
                        ? "Registrar empleado"
                        : "Editar empleado",
                ModalityType.APPLICATION_MODAL
        );

        this.gestorPersonas = Objects.requireNonNull(
                gestorPersonas,
                "El gestor de personas es obligatorio."
        );

        this.validador = Objects.requireNonNull(
                validador,
                "El validador de empleados es obligatorio."
        );

        this.alGuardar = alGuardar;
        this.empleadoOriginal = empleado;

        this.identificacionOriginal = empleado == null
                ? null
                : empleado.getIdentificacion();

        this.campoIdentificacion = new CampoTexto(
                "Identificación",
                "Ingrese el número de identificación"
        );

        this.campoNombre = new CampoTexto(
                "Nombre completo",
                "Ingrese el nombre completo"
        );

        this.campoDireccion = new CampoTexto(
                "Dirección",
                "Ingrese la dirección"
        );

        this.campoTelefono = new CampoTexto(
                "Teléfono",
                "Ingrese el teléfono"
        );

        this.campoCorreo = new CampoTexto(
                "Correo electrónico",
                "Ingrese el correo electrónico"
        );

        this.campoCargo = new CampoTexto(
                "Cargo",
                "Ingrese el cargo del empleado"
        );

        this.formulario = new PanelFormulario();

        inicializarComponentes();

        if (esEdicion()) {
            cargarDatosEmpleado();
        }
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    private void inicializarComponentes() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(
                new BorderLayout(0, ESPACIADO_LG)
        );

        panelPrincipal.setBackground(SUPERFICIE);
        panelPrincipal.setBorder(MARGEN_PANEL_ESTANDAR);

        JLabel titulo = crearEtiqueta(
                esEdicion()
                        ? "Editar información del empleado"
                        : "Información del empleado",
                FUENTE_SUBTITULO,
                TEXTO
        );

        JLabel descripcion = crearEtiqueta(
                esEdicion()
                        ? "Modifique los datos del empleado seleccionado."
                        : "Complete los datos para registrar un nuevo empleado.",
                FUENTE_NORMAL,
                TEXTO_SECUNDARIO
        );

        JPanel panelEncabezado = new JPanel();

        panelEncabezado.setLayout(
                new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS)
        );

        panelEncabezado.setOpaque(false);

        panelEncabezado.add(titulo);
        panelEncabezado.add(
                Box.createVerticalStrut(ESPACIADO_XS)
        );
        panelEncabezado.add(descripcion);

        // Campos del formulario.
        formulario.agregarCampo(campoIdentificacion, 0, 0, 2);
        formulario.agregarCampo(campoNombre, 1, 0, 2);
        formulario.agregarCampo(campoDireccion, 2, 0, 2);
        formulario.agregarCampo(campoTelefono, 3, 0);
        formulario.agregarCampo(campoCorreo, 3, 1);
        formulario.agregarCampo(campoCargo, 4, 0, 2);

        formulario.setAccionGuardar(e -> guardarEmpleado());
        formulario.setAccionCancelar(e -> dispose());

        panelPrincipal.add(
                panelEncabezado,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                formulario,
                BorderLayout.CENTER
        );

        setContentPane(panelPrincipal);

        pack();

        setMinimumSize(
                new Dimension(ANCHO_DIALOGO + 180, 600)
        );

        setLocationRelativeTo(getOwner());
    }

    // ==========================================
    // CARGAR DATOS PARA EDICIÓN
    // ==========================================

    private void cargarDatosEmpleado() {

        campoIdentificacion.setText(
                empleadoOriginal.getIdentificacion()
        );

        campoNombre.setText(
                empleadoOriginal.getNombre()
        );

        campoDireccion.setText(
                empleadoOriginal.getDireccion()
        );

        campoTelefono.setText(
                empleadoOriginal.getTelefono()
        );

        campoCorreo.setText(
                empleadoOriginal.getCorreo()
        );

        campoCargo.setText(
                empleadoOriginal.getCargo()
        );
    }

    // ==========================================
    // GUARDAR EMPLEADO
    // ==========================================

    private void guardarEmpleado() {

        limpiarErrores();

        try {

            Map<String, String> datos = obtenerDatosFormulario();

            // Validación visual y normalización.
            ResultadoValidacion<Map<String, String>> resultado =
                    validador.validar(datos);

            if (!resultado.esValido()) {
                mostrarErroresCampos(resultado.getErrores());
                return;
            }

            Map<String, String> datosValidados =
                    resultado.obtenerValor();

            IEmpleado empleadoGuardado;

            if (esEdicion()) {

                empleadoGuardado = gestorPersonas.actualizarEmpleado(
                        identificacionOriginal,
                        datosValidados
                );

            } else {

                empleadoGuardado = gestorPersonas.registrarEmpleado(
                        datosValidados
                );
            }

            if (empleadoGuardado == null) {
                throw new IllegalStateException(
                        "La operación no devolvió un empleado."
                );
            }

            mostrarMensajeExito(empleadoGuardado);

            if (alGuardar != null) {
                alGuardar.run();
            }

            dispose();

        } catch (ValidacionException ex) {

            if (ex.tieneErroresPorCampo()) {
                mostrarErroresCampos(ex.getErrores());
            } else {
                mostrarErrorValidacion(ex.getMessage());
            }

        } catch (RuntimeException ex) {

            // Imprimir la excepción completa en la consola de errores.
            ex.printStackTrace(System.err);

            // Construir el detalle de todas las causas.
            StringBuilder detalle = new StringBuilder();

            Throwable causa = ex;
            int nivel = 1;

            while (causa != null) {

                detalle.append("Causa ").append(nivel).append(":\n");
                detalle.append(causa.getClass().getName()).append("\n");
                detalle.append(causa.getMessage()).append("\n\n");

                causa = causa.getCause();
                nivel++;
            }

            // Mostrar el error completo en una ventana.
            JTextArea area = new JTextArea(detalle.toString());

            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setCaretPosition(0);

            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(550, 300));

            JOptionPane.showMessageDialog(
                    this,
                    scroll,
                    "Detalle del error al registrar empleado",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==========================================
    // OBTENER DATOS DEL FORMULARIO
    // ==========================================

    private Map<String, String> obtenerDatosFormulario() {

        return Map.of(
                "identificacion", campoIdentificacion.getText(),
                "nombre", campoNombre.getText(),
                "direccion", campoDireccion.getText(),
                "telefono", campoTelefono.getText(),
                "correo", campoCorreo.getText(),
                "cargo", campoCargo.getText()
        );
    }

    // ==========================================
    // MENSAJE DE ÉXITO
    // ==========================================

    private void mostrarMensajeExito(IEmpleado empleado) {

        String mensaje = esEdicion()
                ? "El empleado " + empleado.getNombre()
                + " fue actualizado correctamente."
                : "El empleado " + empleado.getNombre()
                + " fue registrado correctamente.";

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                esEdicion()
                        ? "Actualización exitosa"
                        : "Registro exitoso",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // VISUALIZACIÓN DE ERRORES
    // ==========================================

    private void mostrarErroresCampos(
            Map<String, String> errores) {

        boolean hayErrorGeneral = false;

        for (Map.Entry<String, String> error : errores.entrySet()) {

            String campo = error.getKey();
            String mensaje = error.getValue();

            switch (campo) {

                case "identificacion" ->
                        campoIdentificacion.mostrarError(mensaje);

                case "nombre" ->
                        campoNombre.mostrarError(mensaje);

                case "direccion" ->
                        campoDireccion.mostrarError(mensaje);

                case "telefono" ->
                        campoTelefono.mostrarError(mensaje);

                case "correo" ->
                        campoCorreo.mostrarError(mensaje);

                case "cargo" ->
                        campoCargo.mostrarError(mensaje);

                default -> hayErrorGeneral = true;
            }
        }

        if (hayErrorGeneral) {

            JOptionPane.showMessageDialog(
                    this,
                    errores.getOrDefault(
                            "general",
                            "Se encontraron errores en los datos."
                    ),
                    "Datos inválidos",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void limpiarErrores() {

        campoIdentificacion.limpiarError();
        campoNombre.limpiarError();
        campoDireccion.limpiarError();
        campoTelefono.limpiarError();
        campoCorreo.limpiarError();
        campoCargo.limpiarError();
    }

    private void mostrarErrorValidacion(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Datos inválidos",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // ==========================================
    // UTILIDADES
    // ==========================================

    private boolean esEdicion() {
        return empleadoOriginal != null;
    }

    public void mostrar() {
        setVisible(true);
    }
}
