package com.may.routeplansystem.exception;

import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.constant.ResponseStatu;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;


/**
 * @author 10587
 */
@RestControllerAdvice
@Slf4j
public class GlobleExceptionHandler {

    @ExceptionHandler(AuthentationException.class)
    public ResponseEntity authentationExceptionHandler(AuthentationException e) {
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.AUTHENTATION_FIAL, e.getMessage(), null);
    }

    @ExceptionHandler(ThreeServiceException.class)
    public ResponseEntity baiduMapException(ThreeServiceException e) {
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.THREE_SERVICE_FILE, e.getMessage(), null);
    }

    @ExceptionHandler(FinalSolutionUserChoiceException.class)
    public ResponseEntity finalSolutionUserChoiceExceptionHandler(FinalSolutionUserChoiceException e) {
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.SERVER_EXCUTE_FAIL, e.getMessage(), null);
    }

    @ExceptionHandler(NodeTransferException.class)
    public ResponseEntity nodeTransferExceptionHandler(NodeTransferException e) {
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.FILE_TRANSFER_FIAL, e.getMessage(), null);
    }

    @ExceptionHandler(ParameterException.class)
    public ResponseEntity parameterExceptionHandler(ParameterException e) {
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.PARMETER_INVALIATE, e.getMessage(), null);
    }

    @ExceptionHandler(ProcessException.class)
    public ResponseEntity processExceptionHandler(ProcessException e) {
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.USER_OPERATION_ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity sqlExecutedExceptionHandler(ServerException e) {
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.SERVER_EXCUTE_FAIL, e.getMessage(), null);
    }

    @ExceptionHandler(VehicleTransferException.class)
    public ResponseEntity vehicleTransferException(VehicleTransferException e) {
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.FILE_TRANSFER_FIAL, e.getMessage(), null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity constraintViolationExceptionHandler(ConstraintViolationException e){
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.PARMETER_INVALIATE, e.getMessage(), null);
    }

    @ExceptionHandler(StopException.class)
    public ResponseEntity stopExceptionHandler(StopException e) {
        log.error(e.toString());
        return new ResponseEntity<>(0, e.getMessage(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity IllegalArgumentException(IllegalArgumentException e) {
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.SERVER_EXCUTE_FAIL, "请检查Url 是否正确", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity vehicleTransferException(Exception e) {
        log.error(e.toString());
        return new ResponseEntity<>(ResponseStatu.SERVER_EXCUTE_FAIL, "服务器异常", null);
    }

}
