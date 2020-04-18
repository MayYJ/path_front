package com.may.routeplansystem.node_excel_check.imp;

import com.may.routeplansystem.constant.ExcelColumn;
import com.may.routeplansystem.exception.NodeTransferException;
import com.may.routeplansystem.node_excel_check.NodeExcelCheck;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

/**
 * @author 10587
 */
@Component
public class NodeExcelCheckOne implements NodeExcelCheck {

    @Override
    public void check(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);
        int cellNum = sheet.getRow(0).getPhysicalNumberOfCells();
        IntStream.range(0, cellNum).forEach(i -> {
            String errorMessage = "请选择正确的表格";
            switch (i) {
                case 0:
                    if (!row.getCell(i).getStringCellValue().equals(ExcelColumn.NODE_ONE)){
                        System.out.println(i);
                        throw new NodeTransferException(errorMessage);
                    }
                    break;
                case 1:
                    if (!row.getCell(i).getStringCellValue().equals(ExcelColumn.NODE_TWO)){
                        System.out.println(i);
                        throw new NodeTransferException(errorMessage);
                    }
                    break;
                case 2:
                    if (!row.getCell(i).getStringCellValue().equals(ExcelColumn.NODE_THREE)){
                        System.out.println(i);
                        throw new NodeTransferException(errorMessage);
                    }
                    break;
                case 3:
                    if (!row.getCell(i).getStringCellValue().equals(ExcelColumn.NODE_FOUR)){
                        System.out.println(i);
                        throw new NodeTransferException(errorMessage);
                    }
                    break;
                default:
                    throw new NodeTransferException(errorMessage);
            }
        });
    }
}
