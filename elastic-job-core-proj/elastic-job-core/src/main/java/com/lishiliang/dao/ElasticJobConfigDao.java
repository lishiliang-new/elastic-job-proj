package com.lishiliang.dao;

import com.lishiliang.db.dao.BaseDao;
import com.lishiliang.db.rowmapper.LocalRowMapper;
import com.lishiliang.model.ElasticJobConfigDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lisl
 * @desc :
 */
@Repository
public class ElasticJobConfigDao extends BaseDao {

    /**
     * 任务配置信息
     */
    private static final String JOB_CONFIG_TABLE = "db_manage.t_job_config";
    private static final String JOB_CONFIG_TABLE_COLS = "ID, job_code, job_name, business_source, cron, shard_total_count,"
            + "sharding_item_parameters, job_parameter, description, extra_parameter, enable, delete_flag,"
            + " CREATE_OPER, CREATE_TIME, UPDATE_OPER, UPDATE_TIME";


    /**
     * @author lisl
     * @desc 扫描数据库所有任务配置
     * @return 若无记录，返回NULL
     */
    public List<ElasticJobConfigDO> scanAllJobs(){

        StringBuilder sql = new StringBuilder();
        sql.append(" select ").append(JOB_CONFIG_TABLE_COLS).append(" from ").append(JOB_CONFIG_TABLE);

        return queryForListWithMapperInSlave(sql.toString(), new LocalRowMapper<>(ElasticJobConfigDO.class));
    }
}
