package com.may.routeplansystem.vehicle_excel_read;

import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.po.VehicleMessage;
import com.may.routeplansystem.util.BiTupple;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface VehicelExcelRead {

    BiTupple<ResponseEntity, List<VehicleMessage>> read(Workbook wb, int questionId);

}
