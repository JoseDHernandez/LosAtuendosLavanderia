package com.lavanderia.dao.interfaces;

import com.lavanderia.modelo.interfaces.IPersona;

import java.util.Optional;

/**
 * Contrato de acceso a datos (DAO) para validaciones y consultas transversales de personas.
 * Permite verificar unicidad de identificación y buscar personas independientemente
 * de si su rol específico es Cliente o Empleado.
 */
public interface IPersonaDAO {

    /**
     * Comprueba si un número de identificación ya se encuentra registrado en el sistema.
     *
     * @param identificacion Documento de identidad a verificar.
     * @return {@code true} si la identificación ya existe, {@code false} si está disponible.
     */
    boolean existeIdentificacion(String identificacion);

    /**
     * Comprueba si un número de identificación ya existe en otra persona, excluyendo la que se está editando.
     * Esencial durante operaciones de actualización para evitar duplicados.
     *
     * @param identificacion Nueva identificación a validar.
     * @param identificacionExcluida Identificación actual de la persona que se excluye de la búsqueda.
     * @return {@code true} si otro registro ya utiliza esa identificación, {@code false} de lo contrario.
     */
    boolean existeIdentificacion(
            String identificacion,
            String identificacionExcluida
    );

    /**
     * Busca cualquier persona registrada por su número de identificación.
     *
     * @param identificacion Documento de identidad a consultar.
     * @return Un {@link Optional} con la persona encontrada (Cliente o Empleado), o vacío.
     */
    Optional<IPersona> buscarPorIdentificacion(String identificacion);
}
