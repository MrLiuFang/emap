package com.pepper.dao.emap.log;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.log.SystemLog;

import java.util.Date;

public interface SystemLogDao extends BaseDao<SystemLog> {
    public Integer deleteByCreateDateGreaterThanEqual(Date createDate);
}
