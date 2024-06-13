package com.linqi.dao;

import com.linqi.entity.ProjectInfo;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectResultBeanListHandler extends ProjectResultHandler implements ResultSetHandler<List<ProjectInfo>> {

    @Override
    public List<ProjectInfo> handle(ResultSet rs) throws SQLException {
        List<ProjectInfo> list = new ArrayList<>();
        while (rs.next()){
            list.add(rsToObject(rs));
        }
        return  list;
    }
}
