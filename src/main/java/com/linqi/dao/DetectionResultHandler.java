package com.linqi.dao;

import com.linqi.entity.DetectionInfo;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetectionResultHandler implements ResultSetHandler<List<DetectionInfo>> {
    @Override
    public List<DetectionInfo> handle(ResultSet rs) throws SQLException {

        List<DetectionInfo> list = new ArrayList<>();
        int number = 1;
        while (rs.next()){
            DetectionInfo info = new DetectionInfo();
            info.setId(rs.getInt("id"));
            info.setMethod(rs.getString("method"));
            info.setCallPermission(rs.getString("call_permission"));
            info.setCallClass(rs.getString("call_class"));
            info.setCallTime(rs.getDate("call_time"));
            info.setDescription(rs.getString("desc"));
            String stack = rs.getString("stack_info");
            if (stack != null){
                String[] arr = stack.split("\r\n");
                info.setStack(Arrays.asList(arr));
            }
            info.setOrderIndex(rs.getInt("order_index"));
            list.add(info);
        }
        return list;
    }
}
