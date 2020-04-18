package com.may.routeplansystem.node_excel_read;

import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.po.NodePojo;
import com.may.routeplansystem.util.BiTupple;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface NodeExcelRead {

    BiTupple<ResponseEntity, List<NodePojo>> read(Workbook wb, int questionId);
}
