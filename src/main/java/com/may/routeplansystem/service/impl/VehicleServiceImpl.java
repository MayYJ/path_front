package com.may.routeplansystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.may.routeplansystem.constant.ProcessState;
import com.may.routeplansystem.dao.QuestionDao;
import com.may.routeplansystem.dao.VehicleDao;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.po.Question;
import com.may.routeplansystem.entity.po.VehicleMessage;
import com.may.routeplansystem.exception.ParameterException;
import com.may.routeplansystem.exception.ProcessException;
import com.may.routeplansystem.exception.ServerException;
import com.may.routeplansystem.service.VehicleService;
import com.may.routeplansystem.service.util.ServiceUtil;
import com.may.routeplansystem.util.BiTupple;
import com.may.routeplansystem.vehicle_excel_check.VehicelExcelCheck;
import com.may.routeplansystem.vehicle_excel_read.VehicelExcelRead;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.may.routeplansystem.constant.ExceptionMessage.*;
import static com.may.routeplansystem.service.util.ServiceUtil.checkSqlExecuted;


/**
 * @author 10587
 */
@Service
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    @Resource
    private VehicleDao vehicleDao;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private QuestionDao questionDao;

    @Resource
    private BatchImportService batchImportService;

    @Value("${excel.vehicle_check}")
    private String checkExcelVersion;

    @Value("${excel.vehicle_read}")
    private String readRuleVersion;

    @Override
    public void vehicleRegister(VehicleMessage vehicleMessage) {
        checkAlgorithmState(vehicleMessage.getQuestionId());
        Objects.requireNonNull(vehicleMessage, VEHICLE_CANNOT_NULL);
        boolean flag = vehicleDao.insertVehicle(vehicleMessage);
        ServiceUtil.checkSqlExecuted(flag);
        checkAlgorithmState(vehicleMessage.getQuestionId());
    }

    private void checkAlgorithmState(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        if (question == null) {
            throw new ParameterException("没有该Id的问题");
        }
        int processState = question.getProcessState();
        if (processState == ProcessState.SOME_ALGORITHM_PROCESSING) {
            throw new ProcessException("算法正在执行，不能添加或者删除基本数据");
        }
        if (processState < ProcessState.LOAD_NODE) {
            throw new ProcessException("请先导入点");
        }
        if (processState <= ProcessState.LOAD_VEHICLE) {
            questionDao.updateQuestionProcessState(ProcessState.LOAD_VEHICLE, questionId);
        }
    }

    @Override
    public List<VehicleMessage> userVehicleMessage(Integer questionId) {
        List<VehicleMessage> vehicleMessage = vehicleDao.searchVehicleByQuestionId(questionId);
        Objects.requireNonNull(vehicleMessage, VEHICLE_NULL_BY_USERID);
        return vehicleMessage;
    }

    @Override
    public VehicleMessage vehicleMessage(int vehicleId) {
        VehicleMessage vehicleMessage = vehicleDao.searchVehicleByVehicleId(vehicleId);
        Objects.requireNonNull(vehicleMessage, VEHICLE_NULL_BY_VEHICLEID);
        return vehicleMessage;
    }

    @Override
    public void deleteVehicleBatch(List<Integer> vehicleIdList) {
        if (vehicleIdList.isEmpty()) {
            throw new ParameterException(VEHICLE_ID_LIST_EMPTY);
        }
        boolean flag = vehicleDao.deleteVehicle((ArrayList) vehicleIdList);
        checkSqlExecuted(flag);
    }

    @Override
    public ResponseEntity vehicleBatchImport(MultipartFile mFile, HttpServletRequest request, int questionId) {
       return batchImportService.batchImport(mFile, request, questionId, this::readExcelToDatabase);
    }

    @Transactional(rollbackFor = ServerException.class)
    ResponseEntity readExcelToDatabase(Workbook wb, int questionId) {
        checkExcel(wb);
        VehicelExcelRead excelRead = (VehicelExcelRead) applicationContext.getAutowireCapableBeanFactory()
                .getBean(this.readRuleVersion);
        BiTupple<ResponseEntity, List<VehicleMessage>> tupple = excelRead.read(wb, questionId);
        ResponseEntity responseEntity = tupple.getT();
        List<VehicleMessage> vehicleMessages = tupple.getU();

        for (int i = 0; i < vehicleMessages.size(); i++) {
            boolean insertFlag = vehicleDao.insertVehicle(vehicleMessages.get(i));
            if (!insertFlag) {
                responseEntity.setStatus(5);
                responseEntity.setMsg("服务器错误, 请下次从第" + i+1 + "行数据开始导入" );
                return responseEntity;
            }
        }
        responseEntity = checkQuestionStateAndUpdate(questionId, responseEntity);
        return responseEntity;
    }

    private ResponseEntity checkQuestionStateAndUpdate(int questionId, ResponseEntity responseEntity) {
        Objects.requireNonNull(responseEntity);
        Question question = questionDao.findQuestionByQuestionId(questionId);
        if (question == null) {
            throw new ParameterException("没有该ID 的问题");
        }
        int processState = question.getProcessState();
        if (processState < ProcessState.LOAD_NODE) {
            responseEntity.setStatus(6);
            responseEntity.setMsg("请先导入点");
            return responseEntity;
        }
        if (processState <= ProcessState.LOAD_VEHICLE) {
            questionDao.updateQuestionProcessState(ProcessState.LOAD_VEHICLE, questionId);
        }
        return responseEntity;
    }

    private void checkExcel(Workbook workbook) {
        Objects.requireNonNull(workbook);
        VehicelExcelCheck excelCheckRule= (VehicelExcelCheck) applicationContext.getAutowireCapableBeanFactory()
                .getBean(this.checkExcelVersion);
        excelCheckRule.check(workbook);
    }

    @Override
    public void removeVehicleByQuestionId(int questionId) {
        vehicleDao.deleteVehicleByQuestionId(questionId);
    }

    @Override
    public void removeVehicleByVehicleId(int vehicleId) {
        boolean deleteFlag = vehicleDao.deleteVehicleByVehicleId(vehicleId);
        ServiceUtil.checkSqlExecuted(deleteFlag);
    }
}
