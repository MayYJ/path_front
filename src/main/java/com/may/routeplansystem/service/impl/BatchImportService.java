package com.may.routeplansystem.service.impl;

import com.may.routeplansystem.constant.ExceptionMessage;
import com.may.routeplansystem.constant.SessionMessage;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.exception.NodeTransferException;
import com.may.routeplansystem.exception.ServerException;
import com.may.routeplansystem.exception.VehicleTransferException;
import com.may.routeplansystem.util.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.BiFunction;

@Component
public class BatchImportService {

    public ResponseEntity batchImport(MultipartFile mFile, HttpServletRequest request,
                                      int questionId, BiFunction<Workbook, Integer, ResponseEntity> biFunction) {
        checkFile(mFile);
        String userId = (String) request.getSession().getAttribute(SessionMessage.USER_ID_KEY);
        String filePath = getFilePath();
        Path uploadDir = Paths.get(filePath + userId);
        InputStream tempFileInputStream = null;
        ResponseEntity responseEntity;
        try {
            if (!uploadDir.toFile().exists()) {
                Files.createDirectory(uploadDir);
            }
            Path tempFilePath = Paths.get(filePath + userId + File.separator + System.currentTimeMillis() + ".xlsx");
            Files.createFile(tempFilePath);
            mFile.transferTo(tempFilePath.toFile());
            tempFileInputStream = Files.newInputStream(tempFilePath);
            String fileName = mFile.getOriginalFilename();
            Workbook wb = getWordBook(fileName, tempFileInputStream);
            responseEntity = biFunction.apply(wb, questionId);
            Files.delete(tempFilePath);
        } catch (IOException e) {
            throw new ServerException("文件IO错误");
        } finally {
            try {
                Objects.requireNonNull(tempFileInputStream, ExceptionMessage.VEHOCLE_TRANSFER_FAIL);
                tempFileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseEntity;
    }

    private void checkFile(MultipartFile file) {
        if (file == null) {
            throw new NodeTransferException("文件不能为空");
        }
        String fileName = Objects.requireNonNull(file).getOriginalFilename();
        boolean fileValidateFLag = ExcelUtil.validateExcel(fileName);
        if (!fileValidateFLag){
            throw new NodeTransferException("文件类型不符合规范");
        }
    }

    private String getFilePath() {
        String filePath = System.getProperties().getProperty("user.dir");
        return filePath.replace("RoutePlanSystem", "");
    }

    private Workbook getWordBook(String fileName, InputStream is) throws IOException {
        Workbook wb;
        if (ExcelUtil.isExcel2003(fileName)) {
            wb = new HSSFWorkbook(is);
        } else if (ExcelUtil.isExcel2007(fileName)) {
            wb = new XSSFWorkbook(is);
        } else {
            throw new VehicleTransferException(ExceptionMessage.VEHOCLE_TRANSFER_FAIL);
        }
        return wb;
    }
}
