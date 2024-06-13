package com.linqi.dao;

import com.linqi.entity.ApplicationInfo;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationResultHandler implements ResultSetHandler<List<ApplicationInfo>> {
    @Override
    public List<ApplicationInfo> handle(ResultSet rs) throws SQLException {
        List<ApplicationInfo> infos = new ArrayList<>();
        int number = 1;
        while (rs.next()){
            ApplicationInfo info = new ApplicationInfo();
            info.setId(rs.getInt("id"));
            info.setAppName(rs.getString("app_name"));
            info.setShowName(rs.getString("show_name"));
            info.setStartTime(rs.getTimestamp("start_time"));
            info.setIndex(number);
            infos   .add(info);
            number++;
        }
        return infos;
    }
}
