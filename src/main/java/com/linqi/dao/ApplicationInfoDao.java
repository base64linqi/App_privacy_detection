package com.linqi.dao;

import com.linqi.constant.TimeConstant;
import com.linqi.entity.ApplicationInfo;
import com.linqi.entity.DetectionInfo;
import com.linqi.entity.ProjectInfo;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.text.SimpleDateFormat;
import java.util.List;

public class ApplicationInfoDao {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeConstant.DATETIME_FORMATTER_ALL);
    public int insert(ApplicationInfo applicationInfo) throws Exception{
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        String sql ="";
        if (applicationInfo.getId() != null){
            sql = "delete from app_detection where id = " + applicationInfo.getId();
            queryRunner.update(sql);
        }
        sql = "insert into app_detection(project_id, app_name,show_name,start_time) values(?,?,?,?)";

        int id = queryRunner.insert(sql,
                new ScalarHandler<>(), applicationInfo.getProjectId(), applicationInfo.getAppName(),
                applicationInfo.getShowName(),
                simpleDateFormat.format(applicationInfo.getStartTime()));
        applicationInfo.setId(id);
        return  id;
    }

    public void setAllChildInfo(ProjectInfo projectInfo) throws  Exception{
        // 查找所有对应app信息
        List<ApplicationInfo> allAppInfoList = getAllAppInfoList(projectInfo.getId());
        if (allAppInfoList != null && allAppInfoList.size() > 0) {
            for (ApplicationInfo applicationInfo : allAppInfoList) {
                applicationInfo.setAllDetectionInfos(getDetections(applicationInfo.getId()));
                applicationInfo.setProjectId(projectInfo.getId());
            }
            projectInfo.setApplicationInfoList(allAppInfoList);
        }
    }

    public void insertDetection(int appId, DetectionInfo detectionInfo) throws Exception{
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        String sql = "insert into detection(main_id,method,call_permission,call_class,desc,call_time,stack_info,order_index)" +
                "values(?,?,?,?,?,?,?,?)";
        try {
            queryRunner.update(sql, appId, detectionInfo.getMethod(),
                    detectionInfo.getCallPermission(),
                    detectionInfo.getCallClass(), detectionInfo.getDescription(),
                    simpleDateFormat.format(detectionInfo.getCallTime()),
                    String.join("\r\n", detectionInfo.getStack()),
                    detectionInfo.getOrderIndex());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public List<ApplicationInfo> getAllAppInfoList(Integer projectId) throws Exception {
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        String sql = String.format("select * from app_detection where project_id= %d order by start_time", projectId);
        List<ApplicationInfo> list = queryRunner.query(sql,new ApplicationResultHandler());
        return list;

    }

    public void delete(Integer id) throws Exception {
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        queryRunner.update("delete from detection where main_id=?", id);
        queryRunner.update("delete from app_detection where id=?", id);
    }

    public List<DetectionInfo> getDetections(Integer id) throws Exception {
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        String sql = "select * from detection where main_id=" + id;
        return queryRunner.query(sql, new DetectionResultHandler());
    }

    public Integer insertProject(ProjectInfo projectInfo) throws Exception{
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        String sql = "insert into project(project_name,start_time, type) values(?,?,?)";

        int id = queryRunner.insert(sql, new ScalarHandler<>(), projectInfo.getProjectName(),
                simpleDateFormat.format(projectInfo.getStartTime()), projectInfo.getType());
        projectInfo.setId(id);
        return  id;
    }

    public Integer updateProject(ProjectInfo projectInfo) throws Exception{
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        String sql = "update project set project_name=? where id= " + projectInfo.getId();

        return queryRunner.update(sql);
    }

    public void clearProject(ProjectInfo projectInfo)  throws Exception{
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        queryRunner.update("delete from detection where main_id in(select id from app_detection where project_id=?)", projectInfo.getId());
        queryRunner.update("delete from app_detection where project_id =?", projectInfo.getId());
    }


    /**
     * 获取一个解决方案
     * @param id
     * @return
     */
    public ProjectInfo getProject(Integer id) throws Exception{
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        String sql = "select * from project where id=" + id;
        return queryRunner.query(sql, new ProjectResultBeanHandler());

    }

    public void deleteProject(Integer id) throws Exception {
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        queryRunner.update("delete from detection where main_id in (select id from app_detection where project_id="
                + id + ")");
        queryRunner.update("delete from app_detection where project_id=?", id);
        queryRunner.update("delete from project where id=" + id);
    }

    public List<ProjectInfo> getAllProject() throws Exception{
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        String sql = "select * from project order by start_time desc";
        List<ProjectInfo> list = queryRunner.query(sql, new ProjectResultBeanListHandler());
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setIndex(i + 1);
        }
        return  list;
    }

    /**
     * 删除检测信息
     * @param id
     */
    public void deleteDetection(Integer id) throws  Exception{

        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        queryRunner.update("delete from detection where id=" + id);
    }

    /**
     *
     * @param currentApplicationInfo
     * @param showName
     */
    public void reNameShow(ApplicationInfo currentApplicationInfo, String showName) throws Exception{
        QueryRunner queryRunner = new QueryRunner(DatabaseHelper.getDataSouce());
        queryRunner.update("update app_detection set show_name= ? where id= ?", showName, currentApplicationInfo.getId());

    }
}
