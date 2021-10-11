package com.lishiliang.job.scan;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.util.env.IpUtils;
import com.lishiliang.core.utils.Constant;
import com.lishiliang.handler.ElasticJobHandler;
import com.lishiliang.service.ElasticJobService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lisl
 * @version 1.0
 * @desc :  扫描注册任务
 * @date 2021-1-18 16:44
 */
@ElasticJobConf(name = "registJob")
public class RegistJob implements SimpleJob {

    private Logger logger = LoggerFactory.getLogger(RegistJob.class);

    public static final String REGIST_JOB_CODE = "registJob";

    @Autowired
    private ElasticJobService elasticJobService;

    /**
     * 任务管理
     */
    @Autowired
    private ElasticJobHandler elasticJobHandler;

    @Override
    public void execute(ShardingContext shardingContext) {
        logger.info("定时注册任务开始：{}", DateTime.now().toString(Constant.DATE_TIME_YYYY_MM_DD_HH_MM_SS));

        try {
            //注册任务存在多分片的情况下 使只在一个分片上注册 避免一个服务对应任务注册多个监听器
            List<String> items = elasticJobHandler.getShardingItems(REGIST_JOB_CODE, IpUtils.getIp());
            if (!String.valueOf(shardingContext.getShardingItem()).equals(items.get(0))) {
                return;
            }

            elasticJobService.scanJob();

            logger.info("定时注册任务完成：{}", DateTime.now().toString(Constant.DATE_TIME_YYYY_MM_DD_HH_MM_SS));
        } catch (Exception e) {

            logger.error("定时注册任务出现异常！" + e.getMessage(), e);
        }
    }
}
