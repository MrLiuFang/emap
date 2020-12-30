package com.pepper.dao.emap.lift.impl;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.dao.emap.lift.LiftLogDaoEx;
import com.pepper.model.emap.lift.LiftLog;
import com.pepper.model.emap.lift.LiftRightVip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LiftLogDaoImpl implements LiftLogDaoEx {
    @Autowired
    private BaseDao<LiftLog> baseDao;

    public Pager<LiftLog> List(Pager<LiftLog> pager, String liftId, Date startDate,Date endDate) {
        StringBuffer jpql = new StringBuffer();
        Map<String,Object> searchParameter = new HashMap<String, Object>();
        jpql.append(" select t1 from  LiftLog t1 where 1=1 ");
        if (StringUtils.hasText(liftId)){
            jpql.append( " and t1.liftId = :liftId" );
            searchParameter.put("liftId",liftId);
        }
        if (Objects.nonNull(startDate)){
            jpql.append( " and t1.createDate >= :startDate" );
            searchParameter.put("startDate",startDate);
        }
        if (Objects.nonNull(endDate)){
            jpql.append( " and t1.createDate <= :endDate" );
            searchParameter.put("endDate",endDate);
        }
        return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
    }
}
