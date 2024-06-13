package com.linqi.dao;

import com.linqi.entity.ProjectInfo;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectResultBeanHandler extends ProjectResultHandler implements ResultSetHandler<ProjectInfo> {
    @Override
    public ProjectInfo handle(ResultSet rs) throws SQLException {
        if (rs.next()){
            return rsToObject(rs);
        }
        return null;
    }
}
