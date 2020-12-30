package com.pepper.dao.emap.lift.impl;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.dao.emap.lift.LiftRightVipDaoEx;
import com.pepper.model.emap.lift.LiftRightVip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LiftRightVipDaoImpl implements LiftRightVipDaoEx {
    @Autowired
    private BaseDao<LiftRightVip> baseDao;

    public Pager<LiftRightVip> List(Pager<LiftRightVip> pager, String staffId) {
        StringBuffer jpql = new StringBuffer();
        Map<String,Object> searchParameter = new HashMap<String, Object>();
        jpql.append(" select t1 from  LiftRightVip t1 join Staff t2 on t1.staffId = t2.id join Lift t3 on t1.liftId = t3.id where 1=1 ");
        if (StringUtils.hasText(staffId)){
            jpql.append( " and t2.id = :staffId" );
            searchParameter.put("staffId",staffId);
        }
        return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
    }
}
