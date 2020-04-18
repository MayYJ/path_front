package com.may.routeplansystem.dao;

import com.may.routeplansystem.entity.po.Distance;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author May
 */
@Repository
public interface DistanceDao {

    boolean insertDis(Distance distance);

    boolean insertDistances(List<Distance> list);

    boolean removeDis(int distanceId);

    boolean removeDistanceByStartNodeIdAndEndNodeId(Distance distance);

    boolean removeDistanceByQuestionId(int questionId);

    boolean updateDisAndTime(Distance distance);

    List<Distance> findUpdateDistances(int questionId);

    Distance findDistanceByStartIdAndEndId(@Param("startId") int startId,
                                           @Param("endId") int endId);

    int countNotGetDisDistance(int questionId);
}
