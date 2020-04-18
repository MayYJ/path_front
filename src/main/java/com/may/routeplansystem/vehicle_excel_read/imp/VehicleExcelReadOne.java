package com.may.routeplansystem.vehicle_excel_read.imp;

import com.may.routeplansystem.constant.ExcelColumn;
import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.po.VehicleMessage;
import com.may.routeplansystem.util.BiTupple;
import com.may.routeplansystem.vehicle_excel_read.VehicelExcelRead;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 10587
 */
@Component
@Slf4j
public class VehicleExcelReadOne implements VehicelExcelRead {

    @Override
    public BiTupple<ResponseEntity, List<VehicleMessage>> read(Workbook wb, int questionId) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setMsg(Response.SUCCESSFUL);

        Sheet sheet = wb.getSheetAt(0);
        int totalRow = sheet.getPhysicalNumberOfRows();
        int totalCell = sheet.getRow(1).getPhysicalNumberOfCells();
        List<VehicleMessage> vehicleMessages = new ArrayList<>(totalRow);
        for (int i = 1; i < totalRow; i++) {
            if (responseEntity.getStatus() != 0) {
                break;
            }
            VehicleMessage vehicleMessage = new VehicleMessage();
            vehicleMessage.setQuestionId(questionId);
            Row row = sheet.getRow(i);
            for (int c = 0; c < totalCell; c++) {
                if (responseEntity.getStatus() != 0) {
                    break;
                }
                Cell cell = row.getCell(c);
                switch (c) {
                    case 0:
                        break;
                    case 1:
                        if (cell == null) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg(getNullHintString(i, ExcelColumn.VEHICLE_TWO));
                            break;
                        }
                        String type = cell.getStringCellValue();
                        if (type.length() > 10) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg("第" + (i+1) + "行的车辆类型过长, 请下次从第" + (i+1) + "行开始传输数据");
                        } else {
                            vehicleMessage.setType(type);
                        }
                        break;
                    case 2:
                        if (cell == null) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg(getNullHintString(i, ExcelColumn.VEHICLE_THREE));
                            break;
                        }
                        float capacity = (float) cell.getNumericCellValue();
                        if (capacity == 0) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg("第" + (i+1) + "行的运钞量数据有问题, 请下次从第" + (i+1) + "行开始传输数据");
                        } else {
                            vehicleMessage.setCapacity(capacity);
                        }
                        break;
                    case 3:
                        if (cell == null) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg(getNullHintString(i, ExcelColumn.VEHICLE_FOUR));
                            break;
                        }
                        float oil = (float) cell.getNumericCellValue();
                        if (oil == 0) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg("第" + (i+1) + "行的耗油量数据有问题, 请下次从第" + (i+1) + "行开始传输数据");
                        } else {
                            vehicleMessage.setOil(oil);
                        }
                        break;
                    case 4:
                        if (cell == null) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg(getNullHintString(i, ExcelColumn.VEHICLE_FIVE));
                            break;
                        }
                        float price = (float) cell.getNumericCellValue();
                        if (price == 0) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg("第" + (i+1) + "行的运费数据有问题, 请下次从第" + (i+1) + "行开始传输数据");
                        } else {
                            vehicleMessage.setPrice(price);
                        }
                        break;
                    default:
                        responseEntity.setStatus(5);
                        responseEntity.setMsg("第" +(i+1) + "行上传失败, 请下次从第" + (i+1) + "行开始传输数据");
                }
            }
            if (responseEntity.getStatus() == 0) {
                vehicleMessages.add(vehicleMessage);
            }
        }
        log.debug("查找了" + vehicleMessages.size() + "辆车的数据");
        return new BiTupple<>(responseEntity, vehicleMessages);
    }

    private String getNullHintString(int i, String columnName) {
        return "编号为" + i + "的车辆数据" + columnName +
                "不能为空,请下次从第" + i + "行开始导入数据";
    }
}
