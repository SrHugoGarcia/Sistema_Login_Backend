package org.hugo.backend.users.app.global;

import org.hugo.backend.users.app.utils.ResponseError;
import org.springframework.dao.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class DataBaseExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleDataAccessException(DataAccessException ex) {
        return new ResponseEntity<>(new ResponseError("fail",
                "Error en el acceso a datos",
                ex.getMostSpecificCause().getMessage())
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleDuplicateKeyException(DuplicateKeyException ex) {
        return new ResponseEntity<>(new ResponseError("fail",
                "Registro duplicado",
                ex.getMostSpecificCause().getMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
        return new ResponseEntity<>(new ResponseError("fail",
                "Violacion de restricciones de integridad de datos",
                ex.getMostSpecificCause().getMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleIncorrectResultSizeDataAccessException(IncorrectResultSizeDataAccessException ex){
        return new ResponseEntity<>(new ResponseError("fail",
                "El resultado no coinciden con lo esperado",
                ex.getMostSpecificCause().getMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex){
        return new ResponseEntity<>(new ResponseError("fail",
                "Uso incorrecto de la API de acceso a datos de Spring",
                ex.getMostSpecificCause().getMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleInvalidDataAccessResourceUsageException(InvalidDataAccessResourceUsageException ex){
        return new ResponseEntity<>(new ResponseError("fail",
                "Uso incorrecto de los recursos de acceso a datos",
                ex.getMostSpecificCause().getMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CannotAcquireLockException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleCannotAcquireLockException(CannotAcquireLockException ex){
        return new ResponseEntity<>(new ResponseError("fail",
                "No se puede obtener un bloqueo en una tabla o fila de la base de datos",
                ex.getMostSpecificCause().getMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CannotSerializeTransactionException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleCannotSerializeTransactionException(CannotSerializeTransactionException ex){
        return new ResponseEntity<>(new ResponseError("fail",
                "La transacción no se puede serializar debido a un conflicto de concurrencia",
                ex.getMostSpecificCause().getMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleConcurrencyFailureException(ConcurrencyFailureException ex){
        return new ResponseEntity<>(new ResponseError("fail",
                "Conflicto de concurrencia durante una operación de actualización o eliminación",
                ex.getMostSpecificCause().getMessage())
                , HttpStatus.BAD_REQUEST);
    }

/*
-DataAccessException: Excepción base para errores relacionados con el acceso a datos.

-DataIntegrityViolationException: Lanzada cuando se violan restricciones de integridad de datos, como claves duplicadas o violaciones de restricciones de clave foránea.

-DuplicateKeyException: Lanzada cuando se intenta insertar un registro con una clave primaria duplicada en la base de datos.

-IncorrectResultSizeDataAccessException: Lanzada cuando los resultados de una consulta no coinciden con lo esperado, como obtener más o menos filas de las esperadas.

-InvalidDataAccessApiUsageException: Lanzada cuando se utiliza incorrectamente la API de acceso a datos de Spring.

-InvalidDataAccessResourceUsageException: Lanzada cuando se producen errores relacionados con el uso incorrecto de los recursos de acceso a datos.

-CannotAcquireLockException: Lanzada cuando no se puede obtener un bloqueo en una tabla o fila de la base de datos.

-CannotSerializeTransactionException: Lanzada cuando una transacción no se puede serializar debido a un conflicto de concurrencia.

-ConcurrencyFailureException: Lanzada cuando se produce un conflicto de concurrencia durante una operación de actualización o eliminación.
 */
}
