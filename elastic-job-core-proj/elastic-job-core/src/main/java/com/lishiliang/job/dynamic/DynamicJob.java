package com.lishiliang.job.dynamic;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.lishiliang.commons.Constant;
import com.lishiliang.handler.ElasticJobHandler;
import com.lishiliang.loadbalance.RandomLoadBalance;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.configcenter.DynamicConfiguration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


/**
 * 自定义动态job
 */
public class DynamicJob implements SimpleJob {

    private Logger logger =  LoggerFactory.getLogger(DynamicJob.class);
    @Nullable
    //jobCode
    private String jobCode;
    //描述信息
    private String description;
    //当前服务所在IP和端口
    private String currentService;

    //获取权重列表 动态配置的节点
    private String weightKey = "elastic.job.loadbalance.weight";

    //权重列表
    private final String serverWeight;

    /**
     * 定时任务管理
     */
    private ElasticJobHandler elasticJobHandler;


    @Override
    public void execute(ShardingContext shardingContext) {

        //当前分片
        int item = shardingContext.getShardingItem();
        //定位分片
        int shard  = getShardingItem();

        if (shard != item) {
            logger.info("当前任务{}, 当前分片{}-定位分片{}, 未定位到当前服务{} 任务执行结束. ", jobCode, item, shard, currentService);
            return;
        }

        try {
            //解析任务参数
            parseParameter(shardingContext.getJobParameter());

            logger.info("当前分片{}-定位分片{} - 当前服务{} | - 执行{}开始：{} 任务参数: {}", item, shard, currentService, jobCode, DateTime.now().toString(com.lishiliang.core.utils.Constant.DATE_TIME_YYYY_MM_DD_HH_MM_SS), shardingContext.getJobParameter());
            logger.info("任务：{}执行完成：{}", jobCode, DateTime.now().toString(com.lishiliang.core.utils.Constant.DATE_TIME_YYYY_MM_DD_HH_MM_SS));
        } catch(Exception e) {

            logger.error("任务：" + jobCode + "\t执行过程中出现异常！" + e.getMessage(), e);
        }
    }

    /**
     * 获取向下运行的分片项
     * @return
     */
    private int getShardingItem() {

        RandomLoadBalance randomLoadBalance = new RandomLoadBalance();
        Map<String, Set<Integer>> zkServerShadingItem = elasticJobHandler.zkServerShadingInfo(jobCode);
        //优先获取 zk节点 /dubbo/config/dubbo/kettle-job/dynamic.properties 的动态配置的权重信息 没有则在本地配置文件获取
        String weight = Optional.ofNullable(DynamicConfiguration.getDynamicConfiguration().getConfig("kettle-job/dynamic.properties", "dubbo")).orElse(serverWeight);
        ConcurrentHashMap<String, Integer> serversWeightMap = randomLoadBalance.getServersWeightMap(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(StringUtils.defaultString(weight, "")), zkServerShadingItem);
        return randomLoadBalance.doSelect(jobCode, serversWeightMap, zkServerShadingItem);
    }


    /**
     * 解析任务参数
     * @param jobParameter
     */
    private void parseParameter(String jobParameter) {
        Map<String, String> jobParameterMap = Maps.newHashMap();
        Stream.of(jobParameter.split(Constant.JOB_PARAMETER_CONCATENATION)).forEach(parameter-> jobParameterMap.put(parameter.split("=")[0], parameter.split("=")[1]));
    }

    /**
     * @desc 构造函数
     * @param jobCode
     * @param description
     * @param currentService
     * @param serverWeight
     */
    public DynamicJob(String jobCode, String description, String currentService, String serverWeight) {
        this.jobCode = jobCode;
        this.description = description;
        this.currentService = currentService;
        this.serverWeight = serverWeight;
    }

    /**
     * @author lisl
     * @desc 构造函数
     * @param jobCode
     * @param description
     * @param currentService
     * @return
     */
    public DynamicJob reBuild (String jobCode, String description, String currentService) {
        this.jobCode = jobCode;
        this.description = description;
        this.currentService = currentService;
        return this;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }


    public void setElasticJobHandler(ElasticJobHandler elasticJobHandler) {
        this.elasticJobHandler = elasticJobHandler;
    }


    
}