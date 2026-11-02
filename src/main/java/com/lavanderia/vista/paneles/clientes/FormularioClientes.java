
package com.lavanderia.vista.paneles.clientes;

import com.lavanderia.modelo.interfaces.ICliente;
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

/** Recoge los datos del formulario de clientes y coordina las acciones de esa pantalla. */
public class FormularioClientes extends JDialog {

    private final IGestorPersonas gestorPersonas;
    private final IPersonaValidator validador;
    private final Runnable alGuardar;

    private final ICliente clienteOriginal;
    private final String identificacionOriginal;

    private final CampoTexto campoIdentificacion;
    private final CampoTexto campoNombre;
    private final CampoTexto campoDireccion;
    private final CampoTexto campoTelefono;
    private final CampoTexto campoCorreo;

    private final PanelFormulario formulario;

    // ==========================================
    // CONSTRUCTOR DE REGISTRO
    // ==========================================

    public FormularioClientes(
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

    public FormularioClientes(
            Window propietario,
            IGestorPersonas gestorPersonas,
            IPersonaValidator validador,
            ICliente cliente,
            Runnable alGuardar) {

        super(
                propietario,
                cliente == null
                        ? "Registrar cliente"
                        : "Editar cliente",
                ModalityType.APPLICATION_MODAL
        );

        this.gestorPersonas = Objects.requireNonNull(
                gestorPersonas,
                "El gestor de personas es obligatorio."
        );

        this.validador = Objects.requireNonNull(
                validador,
                "El validador de personas es obligatorio."
        );

        this.alGuardar = alGuardar;
        this.clienteOriginal = cliente;

        this.identificacionOriginal = cliente == null
                ? null
                : cliente.getIdentificacion();

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

        this.formulario = new PanelFormulario();

        inicializarComponentes();

        if (esEdicion()) {
            cargarDatosCliente();
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
                        ? "Editar información del cliente"
                        : "Información del cliente",
                FUENTE_SUBTITULO,
                TEXTO
        );

        JLabel descripcion = crearEtiqueta(
                esEdicion()
                        ? "Modifique los datos del cliente seleccionado."
                        : "Complete los datos para registrar un nuevo cliente.",
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

        // Campos.
        formulario.agregarCampo(campoIdentificacion, 0, 0, 2);
        formulario.agregarCampo(campoNombre, 1, 0, 2);
        formulario.agregarCampo(campoDireccion, 2, 0, 2);
        formulario.agregarCampo(campoTelefono, 3, 0);
        formulario.agregarCampo(campoCorreo, 3, 1);

        // Acciones.
        formulario.setAccionGuardar(e -> guardarCliente());
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
                new Dimension(ANCHO_DIALOGO + 180, 520)
        );

        setLocationRelativeTo(getOwner());
    }

    // ==========================================
    // CARGAR DATOS PARA EDICIÓN
    // ==========================================

    private void cargarDatosCliente() {

        campoIdentificacion.setText(
                clienteOriginal.getIdentificacion()
        );

        campoNombre.setText(
                clienteOriginal.getNombre()
        );

        campoDireccion.setText(
                clienteOriginal.getDireccion()
        );

        campoTelefono.setText(
                clienteOriginal.getTelefono()
        );

        campoCorreo.setText(
                clienteOriginal.getCorreo()
        );
    }

    // ==========================================
    // GUARDAR CLIENTE
    // ==========================================

    private void guardarCliente() {

        limpiarErrores();

        try {

            Map<String, String> datos = obtenerDatosFormulario();

            // Validar y normalizar los datos.
            ResultadoValidacion<Map<String, String>> resultado =
                    validador.validar(datos);

            if (!resultado.esValido()) {
                mostrarErroresCampos(resultado.getErrores());
                return;
            }

            Map<String, String> datosValidados =
                    resultado.obtenerValor();

            // Ejecutar la operación correspondiente.
            ICliente clienteGuardado = esEdicion()
                    ? gestorPersonas.actualizarCliente(
                    identificacionOriginal,
                    datosValidados
            )
                    : gestorPersonas.registrarCliente(
                    datosValidados
            );

            if (clienteGuardado == null) {
                throw new IllegalStateException(
                        "La operación no devolvió un cliente."
                );
            }

            mostrarMensajeExito(clienteGuardado);

            // Cerrar el formulario antes de actualizar la vista.
            dispose();

            if (alGuardar != null) {
                try {
                    alGuardar.run();
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(
                            getOwner(),
                            "El cliente se guardó correctamente, "
                                    + "pero no fue posible actualizar "
                                    + "la lista.\n"
                                    + ex.getMessage(),
                            "Actualización de la vista",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }

        } catch (ValidacionException ex) {

            if (ex.tieneErroresPorCampo()) {
                mostrarErroresCampos(ex.getErrores());
            } else {
                mostrarErrorValidacion(ex.getMessage());
            }

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible "
                            + (esEdicion() ? "actualizar" : "registrar")
                            + " el cliente.\n"
                            + ex.getMessage(),
                    "Error en la operación",
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
                "correo", campoCorreo.getText()
        );
    }

    // ==========================================
    // MENSAJE DE ÉXITO
    // ==========================================

    private void mostrarMensajeExito(ICliente cliente) {

        String mensaje = esEdicion()
                ? "El cliente " + cliente.getNombre()
                + " fue actualizado correctamente."
                : "El cliente " + cliente.getNombre()
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
        return clienteOriginal != null;
    }

    public void mostrar() {
        setVisible(true);
    }
}
