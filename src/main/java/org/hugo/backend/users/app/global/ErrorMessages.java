package org.hugo.backend.users.app.global;

public class ErrorMessages {
    public static final String USER_CREATION_ERROR_MSG = "Error en la creacion del usuario";
    public static final String USER_UPDATE_ERROR_MSG = "Error en la actualización del usuario";
    public static final String USER_NOT_FOUND_MSG = "Error al buscar el usuario";
    public static final String USER_PROFILE_UPDATE_ERROR_MSG = "Error en la actualización del perfil del usuario";
    public static final String USER_PERFIL_NOT_FOUND_MSG = "Error al obtener el perfil";
    public static final String USER_PASSWORD_UPDATE_ERROR_MSG = "Error al actualizar el password";

    public static final String ROLE_CREATION_ERROR_MSG = "Error en la creacion del role";
    public static final String ROLE_UPDATE_ERROR_MSG = "Error en la actualización del role";
    public static final String ROLE_NOT_FOUND_MSG = "Error al buscar el role";
    public static final String INVALID_ARGUMENT = "Argumento invalido";
    public static final String VALIDATION_ERROR_MSG = "Error en la validación";
    public static final String CONVERSION_ERROR_MSG = "No se puede convertir el valor";
    public static final String TOKEN_INVALID_ERROR_MSG = "El token no es valido";
    public static final String EXPIRED_TOKEN_ERROR_MSG = "El token ha expirado";
    public static final String DATA_CONVERSION_ERROR_MSG = "Error al convertir los datos. Revise los tipos de datos y vuelva a intentarlo";
    public static final String HTTP_BODY_READ_ERROR_MSG = "Error no puede leer o interpretar el cuerpo  de la solicitud HTTP entrante.";
    public static final String EMAIL_SENDING_ERROR_MSG = "Error al enviar el correo";
    public static final String INVALID_LIMIT_VALUE_ERROR_MSG ="El valor del campo limit es un argumento invalido";
    public static final String INVALID_PAGE_VALUE_ERROR_MSG = "El valor del campo page es un argumento invalido";
    public static final String INVALID_ORDER_VALUE_ERROR_MSG = "El valor del campo order es un argumento invalido";
    public static final String INVALID_SIZE_VALUE_ERROR_MSG = "El valor del campo size es un argumento invalido";
    public static final String DATA_ACCESS_ERROR_MSG = "Error en el acceso a datos";
    public static final String DUPLICATE_RECORD_ERROR_MSG = "Registro duplicado";
    public static final String DATA_INTEGRITY_VIOLATION_ERROR_MSG = "Violacion de restricciones de integridad de datos";
    public static final String UNEXPECTED_RESULT_ERROR_MSG = "El resultado no coinciden con lo esperado";
    public static final String INCORRECT_DATA_ACCESS_API_USAGE_ERROR_MSG = "Uso incorrecto de la API de acceso a datos de Spring";
    public static final String INCORRECT_RESOURCE_USAGE_ERROR_MSG = "Uso incorrecto de los recursos de acceso a datos";
    public static final String UNABLE_TO_OBTAIN_LOCK_ERROR_MSG = "No se puede obtener un bloqueo en una tabla o fila de la base de datos";
    public static final String TRANSACTION_SERIALIZATION_CONFLICT_ERROR_MSG = "La transacción no se puede serializar debido a un conflicto de concurrencia";
    public static final String CONCURRENT_UPDATE_DELETE_CONFLICT_ERROR_MSG = "Conflicto de concurrencia durante una operación de actualización o eliminación";
    public static final String AUTHENTICATION_FAILURE_LOGIN_ERROR_MSG = "Autenticación fallida. Inicia session porfavor.";
    public static final String ROUTE_ACCESS_DENIED_ERROR_MSG = "Acceso denegado para esta ruta";
    public static final String AUTHENTICATION_CREDENTIALS_ERROR_MESSAGE = "Error en la autenticacion, email o password incorrecto";
}
