package com.lavanderia.vista.estilos;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.Icon;
import java.awt.Color;

public final class IconosUI {

    private static final Color COLOR_ICONO = new Color(71, 85, 105);

    private IconosUI() {}

    // Icono con color predeterminado
    public static Icon obtener(Ikon icono, int tamano) {
        return obtener(icono, tamano, COLOR_ICONO);
    }

    // Icono con color personalizado
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

    public static Icon prendas() {
        return obtener(MaterialDesignT.TSHIRT_CREW, 20);
    }

    public static Icon clientes() {
        return obtener(MaterialDesignA.ACCOUNT_GROUP, 20);
    }

    public static Icon empleados() {
        return obtener(MaterialDesignB.BRIEFCASE, 20);
    }

    public static Icon alquileres() {
        return obtener(MaterialDesignC.CALENDAR_MONTH, 20);
    }

    public static Icon lavanderia() {
        return obtener(MaterialDesignW.WASHING_MACHINE, 20);
    }

    public static Icon inicio() {
        return obtener(MaterialDesignH.HOME, 20);
    }
    // ==========================================
    // ICONOS DE ACCIONES
    // ==========================================

    public static Icon buscar() {
        return obtener(MaterialDesignM.MAGNIFY, 18);
    }

    public static Icon agregar() {
        return obtener(MaterialDesignP.PLUS, 18);
    }

    public static Icon editar() {
        return obtener(MaterialDesignP.PENCIL, 18);
    }

    public static Icon eliminar() {
        return obtener(MaterialDesignD.DELETE, 18);
    }

    public static Icon actualizar() {
        return obtener(MaterialDesignR.REFRESH, 18);
    }

    public static Icon guardar() {
        return obtener(MaterialDesignC.CONTENT_SAVE, 18);
    }

    public static Icon cancelar() {
        return obtener(MaterialDesignC.CLOSE, 18);
    }
}