package com.linqi.dao;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.linqi.utils.PathUtil;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.util.Properties;

public class DatabaseHelper {
    private static DataSource dataSource;
    public static DataSource getDataSouce() throws  Exception{
        if (dataSource == null){
            Properties properties = new Properties();
            properties.setProperty("url", "jdbc:sqlite://" + PathUtil.getDbFilePath());
            properties.setProperty("date_string_format", "yyyy-MM-dd HH:mm:ss");
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        }
        return dataSource;
    }

    public static void createTables() throws Exception{
        QueryRunner queryRunner = new QueryRunner(getDataSouce());
        String sql = "create table if not exists project(id integer not null primary key autoincrement," +
                "project_name varchar(200) not null, start_time datetime not null, type varchar(30) not null)";
        queryRunner.update(sql);
        sql = "create table if not exists app_detection(id integer not null primary key autoincrement," +
                "project_id integer not null,show_name varchar(100) not null," +
                " app_name varchar(100) not null, start_time datetime not null," +
                "stop_mode varchar(20) null, stop_time datetime null, unique_id varchar(36) null, " +
                "foreign key (project_id) references project(id))";
        queryRunner.update(sql);
        sql = "create table if not exists detection(id integer not null primary key autoincrement," +
                "main_id int not null, method varchar(100) null,call_permission varchar(100) null," +
                "call_class varchar(100) null,desc varchar(200) null," +
                "call_time datetime null,stack_info text null, order_index int not null, " +
                "foreign key (main_id) references app_detection(id))";
        queryRunner.update(sql);
        sql = "create table if not exists call_type(id integer not null primary key autoincrement," +
                "name varchar(30) not null,unique (name))";
        queryRunner.update(sql);

    }
}
