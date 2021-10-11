package com.lishiliang.dao;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lishiliang.model.JobConfigDO;
import com.lishiliang.model.JobConfigVO;
import com.lishiliang.core.utils.BuildUtils;
import com.lishiliang.core.utils.Constant;
import com.lishiliang.db.dao.BaseDao;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Repository
public class JobConfigDao extends BaseDao {

    private static String JOB_CONFIG_TABLE = " db_manage.t_job_config ";

    private static String JOB_CONFIG_TABLE_COLS = " ID, JOB_CODE ,JOB_NAME, BUSINESS_SOURCE ,CRON , SHARD_TOTAL_COUNT , SHARDING_ITEM_PARAMETERS , JOB_PARAMETER , " +
            "DESCRIPTION , EXTRA_PARAMETER ,ENABLE ,PROFILES, DELETE_FLAG , CREATE_TIME , UPDATE_TIME , CREATE_OPER , UPDATE_OPER, " +
            " JOB_CYCLE,JOB_PERIOD,JOB_CAN_SUBSCRIBE,JOB_ORG_NO,JOB_DEPENDENCES,JOB_FIRST_DATE,JOB_FIRST_TIME ";

    /**
     * 新增job配置
     * @param jobConfigDO
     */
    public void add(JobConfigDO jobConfigDO) {
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(JOB_CONFIG_TABLE).append("(").append(JOB_CONFIG_TABLE_COLS).append(")");
        sql.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

        DateTime now = DateTime.now();

        updateForOne(sql.toString(), new Object[]{jobConfigDO.getId(), jobConfigDO.getJobCode(),
                jobConfigDO.getJobName(), jobConfigDO.getBusinessSource(),
                jobConfigDO.getCron(), jobConfigDO.getShardTotalCount(), jobConfigDO.getShardingItemParameters(),
                jobConfigDO.getJobParameter(), jobConfigDO.getDescription(), jobConfigDO.getExtraParameter(),
                jobConfigDO.getEnable(), jobConfigDO.getProfiles(), false, now.toString(Constant.DATE_TIME_YYYY_MM_DD_HH_MM_SS),
                now.toString(Constant.DATE_TIME_YYYY_MM_DD_HH_MM_SS), jobConfigDO.getCreateOper(), jobConfigDO.getCreateOper(),
                jobConfigDO.getJobCycle(), jobConfigDO.getJobPeriod(), jobConfigDO.getJobCanSubscribe(),
                jobConfigDO.getJobOrgNo(), jobConfigDO.getJobDependences(),
                jobConfigDO.getJobFirstDate(), jobConfigDO.getJobFirstTime()});
    }

    /**
     * 修改job配置
     * @param jobConfigDO
     */
    public void update(JobConfigDO jobConfigDO) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("update ").append(JOB_CONFIG_TABLE).append(" set ");
        sql.append(" job_code = ?, ");
        params.add(jobConfigDO.getJobCode());
        sql.append(" job_name = ?, ");
        params.add(jobConfigDO.getJobName());
        sql.append(" cron = ?, ");
        params.add(jobConfigDO.getCron());
        sql.append(" SHARD_TOTAL_COUNT = ?, ");
        params.add(jobConfigDO.getShardTotalCount());
        sql.append(" SHARDING_ITEM_PARAMETERS = ?, ");
        params.add(jobConfigDO.getShardingItemParameters());
        sql.append(" JOB_PARAMETER = ?, ");
        params.add(jobConfigDO.getJobParameter());
        sql.append(" DESCRIPTION = ?, ");
        params.add(jobConfigDO.getDescription());
        sql.append(" EXTRA_PARAMETER = ?, ");
        params.add(jobConfigDO.getExtraParameter());
        sql.append(" ENABLE = ?, ");
        params.add(jobConfigDO.getEnable());
        sql.append(" PROFILES = ?, ");
        params.add(jobConfigDO.getProfiles());
        sql.append(" DELETE_FLAG = ?, ");
        params.add(jobConfigDO.getDeleteFlag());
        sql.append(" UPDATE_TIME = ?, ");
        params.add(jobConfigDO.getUpdateTime());
        sql.append(" UPDATE_OPER = ?, ");
        params.add(jobConfigDO.getUpdateOper());
        sql.append(" job_cycle = ?, ");
        params.add(jobConfigDO.getJobCycle());
        sql.append(" job_period = ?, ");
        params.add(jobConfigDO.getJobPeriod());
        sql.append(" job_can_subscribe = ?, ");
        params.add(jobConfigDO.getJobCanSubscribe());
        sql.append(" job_org_no = ?, ");
        params.add(jobConfigDO.getJobOrgNo());
        sql.append(" job_dependences = ?, ");
        params.add(jobConfigDO.getJobDependences());
        sql.append(" job_first_date = ?, ");
        params.add(jobConfigDO.getJobFirstDate());
        sql.append(" job_first_time = ? ");
        params.add(jobConfigDO.getJobFirstTime());
        sql.append(" where id = ? ");
        params.add(jobConfigDO.getId());
        updateForOne(sql.toString(), params.toArray());
    }

    @Deprecated
    public void delete(JobConfigDO jobConfigDO) {

        StringBuilder sql = new StringBuilder("delete from ");
        sql.append(JOB_CONFIG_TABLE);
        sql.append(" where id = " + jobConfigDO.getId());
        primaryJdbcTemplate.execute(sql.toString());
    }

    public PageInfo<JobConfigVO> pageInfo(JobConfigDO jobConfigDO, int page, int limit) {
        return PageHelper.startPage(page, limit).doSelectPageInfo(() -> primarySqlSessionTemplate.selectList("com.lishiliang.mapper.queryJobConfig", jobConfigDO));
    }

    public List<JobConfigVO> all(JobConfigDO jobConfigDO) {
        return primarySqlSessionTemplate.selectList("com.lishiliang.mapper.queryJobConfig", jobConfigDO);
    }


    public void batchEnbleOrDisable(List<Long> ids, boolean enable) {

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("update ").append(JOB_CONFIG_TABLE).append(" set ");
        sql.append(" enable = ? ");
        params.add(enable);
        sql.append(" where business_source = 'kettle_job'");
        sql.append(BuildUtils.SqlBuildUtils.buildInSql(ids, "id", params, true));
        primaryJdbcTemplate.update(sql.toString(), params.toArray());
    }
}
