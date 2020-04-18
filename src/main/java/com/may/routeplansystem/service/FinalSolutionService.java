package com.may.routeplansystem.service;

import com.may.routeplansystem.entity.po.FinalSolution;
import com.may.routeplansystem.entity.po.Solution;
import com.may.routeplansystem.entity.vo.FinalSolutionVo;

import java.util.List;

public interface FinalSolutionService {

    FinalSolutionVo getFinalSolution(int finalSolutionId);

    List<FinalSolutionVo> getAllFinalSolutionOrdered(int questionId);

    void removeFinalSolution(int finalSolutionId);

    void removeAllFinalSolutionByQuestionId(int questionId);

    void removeOneVersionFinalSoltuion(int questionId, int version);

    List<FinalSolutionVo> getOneVersionFinalSolution(int questionId, int versionId);

    int getMaxVersionOfFinalSolution(int questionId);

    List<Integer> getAllVersion(int questionId);

    void updateFinalSolutionState(int finalSolutionId);
}
