package com.lavanderia.vista.estilos;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.Icon;
import java.awt.Color;

/**
 * Catálogo y fábrica centralizada de iconos vectoriales para la interfaz Swing.
 * Utiliza la librería Ikonli con el conjunto de glifos Material Design 2.
 * Todos los métodos retornan instancias de {@link Icon} configuradas con tamaño y paleta corporativa.
 */
public final class IconosUI {

    /** Color estándar para la iconografía de la aplicación (Gris pizarra / Slate 600). */
    private static final Color COLOR_ICONO = new Color(71, 85, 105);

    /**
     * Constructor privado para prevenir instanciación de clase de utilidades.
     */
    private IconosUI() {}

    /**
     * Construye un icono con el color por defecto del sistema.
     *
     * @param icono Glifo de Ikonli.
     * @param tamano Tamaño en píxeles.
     * @return Icono generado.
     */
    public static Icon obtener(Ikon icono, int tamano) {
        return obtener(icono, tamano, COLOR_ICONO);
    }

    /**
     * Construye un icono con tamaño y color personalizados.
     *
     * @param icono Glifo de Ikonli.
     * @param tamano Dimensión en píxeles.
     * @param color Color del icono.
     * @return Icono vectorial Swing.
     */
    public static Icon obtener(
            Ikon icono,
            int tamano,
            Color color
    ) {
        FontIcon fontIcon = FontIcon.of(icono, tamano);
        fontIcon.setIconColor(color);
        return fontIcon;
    }

    // ==========================================
    // ICONOS DE MÓDULOS
    // ==========================================

    /**
     * Ícono representativo del módulo de Prendas.
     *
     * @return {@link Icon} de camiseta.
     */
    public static Icon prendas() {
        return obtener(MaterialDesignT.TSHIRT_CREW, 20);
    }

    /**
     * Ícono representativo del módulo de Clientes.
     *
     * @return {@link Icon} de grupo de cuentas.
     */
    public static Icon clientes() {
        return obtener(MaterialDesignA.ACCOUNT_GROUP, 20);
    }

    /**
     * Ícono representativo del módulo de Empleados.
     *
     * @return {@link Icon} de maletín ejecutivo.
     */
    public static Icon empleados() {
        return obtener(MaterialDesignB.BRIEFCASE, 20);
    }

    /**
     * Ícono representativo del módulo de Alquileres.
     *
     * @return {@link Icon} de calendario mensual.
     */
    public static Icon alquileres() {
        return obtener(MaterialDesignC.CALENDAR_MONTH, 20);
    }

    /**
     * Ícono representativo del módulo de Lavandería.
     *
     * @return {@link Icon} de lavadora.
     */
    public static Icon lavanderia() {
        return obtener(MaterialDesignW.WASHING_MACHINE, 20);
    }

    /**
     * Ícono representativo del módulo de Inicio / Dashboard.
     *
     * @return {@link Icon} de casa / inicio.
     */
    public static Icon inicio() {
        return obtener(MaterialDesignH.HOME, 20);
    }

    // ==========================================
    // ICONOS DE ACCIONES
    // ==========================================

    /**
     * Ícono para la acción de búsqueda o filtro.
     *
     * @return {@link Icon} de lupa.
     */
    public static Icon buscar() {
        return obtener(MaterialDesignM.MAGNIFY, 18);
    }

    /**
     * Ícono para la acción de añadir o crear registro.
     *
     * @return {@link Icon} de signo más (+).
     */
    public static Icon agregar() {
        return obtener(MaterialDesignP.PLUS, 18);
    }

    /**
     * Ícono para la acción de modificar o editar registro.
     *
     * @return {@link Icon} de lápiz.
     */
    public static Icon editar() {
        return obtener(MaterialDesignP.PENCIL, 18);
    }

    /**
     * Ícono para la acción de eliminar o descartar registro.
     *
     * @return {@link Icon} de papelera.
     */
    public static Icon eliminar() {
        return obtener(MaterialDesignD.DELETE, 18);
    }

    /**
     * Ícono para la acción de recargar o refrescar datos.
     *
     * @return {@link Icon} de flechas de recarga.
     */
    public static Icon actualizar() {
        return obtener(MaterialDesignR.REFRESH, 18);
    }

    /**
     * Ícono para la acción de guardar cambios.
     *
     * @return {@link Icon} de disco de guardado.
     */
    public static Icon guardar() {
        return obtener(MaterialDesignC.CONTENT_SAVE, 18);
    }

    /**
     * Ícono para la acción de cerrar o cancelar operación.
     *
     * @return {@link Icon} de equis / cerrar.
     */
    public static Icon cancelar() {
        return obtener(MaterialDesignC.CLOSE, 18);
    }
}
