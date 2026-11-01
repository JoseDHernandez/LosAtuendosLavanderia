package com.lavanderia.conexion;

import com.lavanderia.dao.interfaces.*;

public interface DAOFactory {

    IPersonaDAO getPersonaDAO();

    IClienteDAO getClienteDAO();

    IEmpleadoDAO getEmpleadoDAO();

    IPrendaDAO getPrendaDAO();

    IDevolucionDAO getDevolucionDAO();

    IAlquilerDAO getAlquilerDAO();

    ILavanderiaDAO getLavanderiaDAO();
}