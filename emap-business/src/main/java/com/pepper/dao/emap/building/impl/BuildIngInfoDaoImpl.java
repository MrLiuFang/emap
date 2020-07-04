package com.pepper.dao.emap.building.impl;

import com.pepper.core.base.BaseDao;
import com.pepper.dao.emap.building.BuildIngInfoDaoEx;
import com.pepper.model.emap.building.BuildingInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-07-04 08:50
 */
public class BuildIngInfoDaoImpl implements BuildIngInfoDaoEx {

    @Autowired
    private BaseDao<BuildingInfo> baseDao;

}
