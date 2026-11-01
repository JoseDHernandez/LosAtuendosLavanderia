package com.lavanderia.vista.interfaces;

import javax.swing.*;

/**
 * Contrato que define un módulo o pantalla principal navegable dentro de la interfaz gráfica Swing.
 * Cada módulo provee un identificador único, un título legible, un ícono representativo
 * y el componente visual ({@link JComponent}) para ser alojado en la ventana principal.
 */
public interface IModuloVista {

    /**
     * Obtiene el identificador textual único del módulo (ej. "inicio", "clientes", "prendas").
     *
     * @return Clave única de navegación del módulo.
     */
    String getId();

    /**
     * Obtiene el título legible que se desplegará en la barra lateral o encabezado.
     *
     * @return Título del módulo.
     */
    String getTitulo();

    /**
     * Obtiene el ícono representativo del módulo para los botones de navegación.
     *
     * @return Instancia de {@link Icon}.
     */
    Icon getIcono();

    /**
     * Retorna el componente gráfico Swing contenedor de la interfaz de este módulo.
     *
     * @return Componente de tipo {@link JComponent} (usualmente un JPanel).
     */
    JComponent getVista();
}
