package com.may.routeplansystem.node_excel_read.imp;

import com.alibaba.fastjson.JSON;
import com.may.routeplansystem.constant.Constant;
import com.may.routeplansystem.constant.ExcelColumn;
import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.po.NodePojo;
import com.may.routeplansystem.entity.vo.NodeJsonResponse;
import com.may.routeplansystem.exception.NodeTransferException;
import com.may.routeplansystem.node_excel_read.NodeExcelRead;
import com.may.routeplansystem.util.BaiduMapApiUtil;
import com.may.routeplansystem.util.BiTupple;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class NodeExcelReadOne implements NodeExcelRead {

    @Override
    public BiTupple<ResponseEntity, List<NodePojo>> read(Workbook wb, int questionId) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setMsg(Response.SUCCESSFUL);

        Sheet sheet = wb.getSheetAt(0);
        int totalRow = sheet.getPhysicalNumberOfRows();
        int totalCell = sheet.getRow(1).getPhysicalNumberOfCells();
        List<NodePojo> nodes = new ArrayList<>(totalRow);
        for (int i = 1; i < totalRow; i++) {
            NodePojo node = new NodePojo();
            node.setQuestionId(questionId);
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
                            responseEntity.setMsg(getNullHintString(i, ExcelColumn.NODE_ONE));
                            break;
                        }
                        String name = cell.getStringCellValue();
                        if (name.length() > 50) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg(getNullHintString(i, ExcelColumn.NODE_TWO));
                        } else {
                            node.setNodeName(name);
                        }
                        break;
                    case 2:
                        try {
                            if (cell == null) {
                                responseEntity.setStatus(1);
                                responseEntity.setMsg(getNullHintString(i, ExcelColumn.NODE_THREE));
                                break;
                            }
                            String address = cell.getStringCellValue();
                            if (!matchChinese(address)) {
                                responseEntity.setStatus(1);
                                responseEntity.setMsg("第" + i+1 + "行的详细地址有误，只能是中文," + "请下次从第" + i+1 + "行开始传输数据");
                            }
                            node.setNodeAddress(address);
                            String jsonResponse = BaiduMapApiUtil.getJsonOfOneNode(address, Constant.BAIDUMAP_AK);
                            NodeJsonResponse nodeJsonResponse = JSON.parseObject(jsonResponse, NodeJsonResponse.class);
                            setNodeLatLng(nodeJsonResponse, node);
                            checkJsonObject(nodeJsonResponse, i);
                        } catch (IOException e) {
                            responseEntity.setStatus(3);
                            responseEntity.setMsg("百度地图服务获取失败, 请下次从" + i+1 + "行开始导入数据");
                        }
                        break;
                    case 3:
                        if (cell == null) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg(getNullHintString(i, ExcelColumn.NODE_FOUR));
                            break;
                        }
                        int isCenter = (int) cell.getNumericCellValue();
                        if (isCenter <0 || isCenter > 1) {
                            responseEntity.setStatus(1);
                            responseEntity.setMsg("第" + i + "行是否是中心点列只能为1或者0," + "请下次从第" + i+1 + "行开始传输数据");
                        }
                        node.setIsCenter(isCenter);
                        break;
                    default:
                        responseEntity.setStatus(5);
                        responseEntity.setMsg("服务器错误," + "请下次从第" + i+1 + "行开始传输数据");
                }
            }
            if (responseEntity.getStatus() == 0) {
                nodes.add(node);
            }
        }
        log.debug("从excel中找到" + nodes.size() + "个点");
        return new BiTupple<>(responseEntity, nodes);
    }

    private String getNullHintString(int i, String columnName) {
        return "编号为" + i + "的点数据" + columnName +
                "不能为空,请下次从第" + i + "行开始导入数据";
    }

    private void checkJsonObject(NodeJsonResponse nodeJsonResponse, int row) {
        int statu = nodeJsonResponse.getStatus();
        if (statu == 0){
            return;
        }
        if (statu == 1) {
            throw new NodeTransferException("请检查" + row + "行的详细地址是否正确");
        }else {
            throw new NodeTransferException("请求百度地图API 出现错误");
        }
    }

    private void setNodeLatLng(NodeJsonResponse nodeJsonResponse, NodePojo node){
        double lat = nodeJsonResponse.getResult().getLocation().getLat();
        double lng = nodeJsonResponse.getResult().getLocation().getLng();
        node.setLat(lat);
        node.setLng(lng);
    }

    private boolean matchChinese(String str) {
        String regex = "^[\u4E00-\u9FA5]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
