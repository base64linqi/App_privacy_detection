package com.linqi.dao;

import com.linqi.entity.ProjectInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectResultHandler{
    protected ProjectInfo rsToObject(ResultSet rs) throws SQLException{
        ProjectInfo p = new ProjectInfo();
        p.setId(rs.getInt("id"));
        p.setProjectName(rs.getString("project_name"));
        p.setStartTime(rs.getDate("start_time"));
        p.setType(rs.getString("type"));
        return  p;
    }
}
