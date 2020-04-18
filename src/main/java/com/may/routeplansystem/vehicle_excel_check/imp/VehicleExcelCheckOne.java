package com.may.routeplansystem.vehicle_excel_check.imp;

import com.may.routeplansystem.constant.ExcelColumn;
import com.may.routeplansystem.exception.NodeTransferException;
import com.may.routeplansystem.vehicle_excel_check.VehicelExcelCheck;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@Slf4j
public class VehicleExcelCheckOne implements VehicelExcelCheck {

    @Override
    public void check(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);
        int cellNum = row.getPhysicalNumberOfCells();
        log.debug("cellNum 值为" + cellNum );
        IntStream.range(0, cellNum).forEach(i -> {
            String errorMessage = "请选择正确的表格";
            switch (i) {
                case 0:
                    if (!row.getCell(i).getStringCellValue().equals(ExcelColumn.VEHICLE_ONE)){
                        log.debug("第一列属性为" + row.getCell(i).getStringCellValue());
                        log.debug("第一列格式不对");
                        throw new NodeTransferException(errorMessage);
                    }
                    break;
                case 1:
                    if (!row.getCell(i).getStringCellValue().equals(ExcelColumn.VEHICLE_TWO)){
                        log.debug("第二列属性为" + row.getCell(i).getStringCellValue());
                        log.debug("第二列格式不对");
                        throw new NodeTransferException(errorMessage);
                    }
                    break;
                case 2:
                    if (!row.getCell(i).getStringCellValue().equals(ExcelColumn.VEHICLE_THREE)){
                        log.debug("第三列属性为" + row.getCell(i).getStringCellValue());
                        log.debug("第三列格式不对");
                        throw new NodeTransferException(errorMessage);
                    }
                    break;
                case 3:
                    if (!row.getCell(i).getStringCellValue().trim().equals(ExcelColumn.VEHICLE_FOUR)){
                        log.debug("第四列属性为" + row.getCell(i).getStringCellValue());
                        log.debug("第四列格式不对");
                        throw new NodeTransferException(errorMessage);
                    }
                    break;
                case 4:
                    if (!row.getCell(i).getStringCellValue().trim().equals(ExcelColumn.VEHICLE_FIVE)){
                        log.debug("第五列属性为" + row.getCell(i).getStringCellValue());
                        log.debug("第五列格式不对");
                        throw new NodeTransferException(errorMessage);
                    }
                    break;
                default:
                    log.debug("出现了多余的列");
                    throw new NodeTransferException(errorMessage);
            }
        });
    }
}
