package com.may.routeplansystem.service.util;

import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.constant.ResponseStatu;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.exception.ServerException;

import java.util.function.Consumer;

public class ServiceUtil {

    public static ResponseEntity checkServerExecuteFialResponseEntity(boolean flag) {
        if (!flag) {
            return new ResponseEntity<>(ResponseStatu.SERVER_EXCUTE_FAIL, "服务器错误", null);
        }
        return new ResponseEntity<>( ResponseStatu.SUCCESS, Response.SUCCESSFUL,null);
    }

    public static ResponseEntity checkThreeServiceFialResponseEntity(boolean flag, String msg){
        if (!flag) {
            return new ResponseEntity<>(ResponseStatu.THREE_SERVICE_FILE, msg, null);
        }
        return new ResponseEntity<>( ResponseStatu.SUCCESS, Response.SUCCESSFUL,null);
    }

    public static ResponseEntity checkAuthentationFialResponseEntity(boolean flag, String msg) {
        if (!flag) {
            return new ResponseEntity<>(ResponseStatu.AUTHENTATION_FIAL, msg, null);
        }
        return new ResponseEntity<>( ResponseStatu.SUCCESS, Response.SUCCESSFUL,null);
    }

    public static ResponseEntity checkuserOperationFialResponseEntity(boolean flag, String msg) {
        if (!flag) {
            return new ResponseEntity<>(ResponseStatu.USER_OPERATION_ERROR, msg, null);
        }
        return new ResponseEntity<>( ResponseStatu.SUCCESS, Response.SUCCESSFUL,null);
    }

    public static ResponseEntity serverExecuteFialResponseEntity() {
        return new ResponseEntity<>(ResponseStatu.SERVER_EXCUTE_FAIL, "服务器错误", null);
    }

    public static ResponseEntity threeServiceFialResponseEntity(String msg){
        return new ResponseEntity<>(ResponseStatu.THREE_SERVICE_FILE, msg, null);
    }

    public static ResponseEntity authentationFialResponseEntity(String msg) {
        return new ResponseEntity<>(ResponseStatu.AUTHENTATION_FIAL, msg, null);
    }

    public static ResponseEntity userOperationFialResponseEntity(String msg) {
        return new ResponseEntity<>(ResponseStatu.USER_OPERATION_ERROR, msg, null);
    }

    public static void checkSqlExecuted(boolean flag) {
        if (!flag) {
            throw new ServerException("服务器错误");
        }
    }

    public static <T> void checkSqlExecuted(boolean flag, T t, Consumer<T> consumer) {
        if (!flag) {
            consumer.accept(t);
            throw new ServerException("服务器错误");
        }
    }

}
