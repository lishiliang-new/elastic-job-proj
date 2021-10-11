package com.lishiliang.service;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobSettingsAPI;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobStatisticsAPI;
import com.dangdang.ddframe.job.lite.lifecycle.domain.JobBriefInfo;
import com.dangdang.ddframe.job.lite.lifecycle.domain.JobSettings;
import com.dangdang.ddframe.job.lite.lifecycle.internal.settings.JobSettingsAPIImpl;
import com.dangdang.ddframe.job.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.dangdang.ddframe.job.util.env.IpUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lishiliang.commons.Constant;
import com.lishiliang.commons.Enums;
import com.lishiliang.dao.ElasticJobConfigDao;
import com.lishiliang.handler.ElasticJobHandler;
import com.lishiliang.job.dynamic.DynamicJob;
import com.lishiliang.model.ElasticJobConfigDO;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lisl
 * @version 1.0
 * @desc : 对Job进行操作(注册,初始化....)
 */
@Service
public class ElasticJobService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticJobService.class);

    /**
     * ZK注册客户端
     */
    @Autowired
    private ZookeeperRegistryCenter registryCenter;

    @Autowired
    private ElasticJobHandler elasticJobHandler;


    @Autowired
    private ElasticJobConfigDao elasticJobConfigDao;

    @Value("${elastic.job.loadbalance.weight}")
    private String weight;

    @Value("${dubbo.protocol.port}")
    private String servicePort;

    /**
     * 当前运行环境
     */
    @Value("${spring.profiles.active}")
    private String profile;

    //本机地址
    private String serviceIp = IpUtils.getIp();

    /**
     * 删除任务
     * @param
     */
    public void deleteJob(String jobCode) {

        logger.info("开始进行{}剔除", jobCode);
        //关闭job
        elasticJobHandler.shutdownAllServiceJob(jobCode, null);
        //删除配置信息
        elasticJobHandler.removeJobConfiguration(jobCode);
        logger.info("ZK中定时任务{}已剔除", jobCode);
    }





    /**
     * @author lisl
     * @desc 扫描任务，并注册<br>
     * 流程如下：<br>1：:扫描数据库中定义的所有的任务<br>2：扫描ZK中是否有对应的任务注册（名称相同）<br>
     * 3：若存在ZK中注册信息，则以数据库中配置为准，更新ZK中配置并重新注册启动（删除注册之后重新注册）<br>
     * 4：若不存在ZK注册信息，则以数据库中配置为准，新增ZK注册
     */
    public void scanJob() {
        
        JobSettingsAPI jobSettingsAPI = new JobSettingsAPIImpl(registryCenter);
        JobStatisticsAPI jobStatisticsAPI = new JobStatisticsAPIImpl(registryCenter);
        
        List<ElasticJobConfigDO> jobConfigs = scanDataBaseJob();
        if(jobConfigs == null || jobConfigs.isEmpty()) {
            
            logger.info("未搜索到job定时任务配置。");
            return;
        }
        
        //解析数据库中配置任务
        Map<String, ElasticJobConfigDO> jobConfigMap = jobConfigs.stream().collect(Collectors.toMap(ElasticJobConfigDO :: getJobCode, t -> t));
        
        //新增或者更新任务
        saveOrUpdateRegistryCenterJob(jobStatisticsAPI, jobSettingsAPI, jobConfigMap);
        logger.info("Job定时动态任务已扫描并处理完成！");
    }
    
    /**
     * @author lisl
     * @desc 新增或者更新ZK任务配置信息
     * @param jobStatisticsAPI
     * @param jobSettingsAPI
     * @param jobConfigMap
     */
    private void saveOrUpdateRegistryCenterJob(JobStatisticsAPI jobStatisticsAPI, JobSettingsAPI jobSettingsAPI,
        Map<String, ElasticJobConfigDO> jobConfigMap) {
        
        //搜集所有任务
        logger.info("扫描搜集ZK中注册的任务信息。");
        Collection<JobBriefInfo> jobs = scanRegistryCenter(jobStatisticsAPI);
        logger.info("扫描ZK中注册的任务数量：{}。", jobs.size());
        
        //更新任务配置并搜索新增的任务
        logger.info("更新ZK已注册任务的配置信息。");
        List<ElasticJobConfigDO> newlyJobConfigs = updateRegistryCenterJob(jobs, jobConfigMap, jobSettingsAPI, jobStatisticsAPI);
        
        //新增任务
        logger.info("新增ZK注册任务。");
        saveRegistryCenterJob(newlyJobConfigs);
    }
    
    /**
     * @author lisl
     * @desc 新增任务
     * @param newlyJobConfigs
     */
    private void saveRegistryCenterJob(List<ElasticJobConfigDO> newlyJobConfigs) {
        
        if(newlyJobConfigs.isEmpty()) {
            logger.info("未搜索到job新增定时任务配置。");
            return;
        }
        
        for(ElasticJobConfigDO elasticJobConfig : newlyJobConfigs) {
            try {
                addJob(elasticJobConfig);
                logger.info("新增job任务：{}成功，配置信息：{}", elasticJobConfig.getJobCode(), JSON.toJSONString(newlyJobConfigs));
            } catch (Exception e) {
                logger.error("新增job任务："+ elasticJobConfig.getJobCode() +"失败，配置信息："+ JSON.toJSONString(newlyJobConfigs) +", 错误信息：" + e.getMessage(), e);
            }
        }
    }

    /**
     * @author lisl
     * @desc 新增任务
     * @param elasticJobConfig
     */
    private void addJob(ElasticJobConfigDO elasticJobConfig) {

        //分析任务参数
        String jobParameter = elasticJobConfig.getJobParameter();
        Map<String, String> jobParameterMap = Maps.newHashMap();
        Stream.of(jobParameter.split(Constant.JOB_PARAMETER_CONCATENATION)).forEach(parameter-> jobParameterMap.put(parameter.split("=")[0], parameter.split("=")[1]));

        //动态任务
        DynamicJob dynamicJob = new DynamicJob(elasticJobConfig.getJobCode() , elasticJobConfig.getDescription(), serviceIp + ":" + servicePort, weight);
        //注入服务
        dynamicJob.setElasticJobHandler(elasticJobHandler);

        //添加任务
        elasticJobHandler.addJob(elasticJobConfig.getJobCode(), dynamicJob, elasticJobConfig.getCron(), elasticJobConfig.getShardTotalCount(),
            elasticJobConfig.getShardingItemParameters(), jobParameter, elasticJobConfig.getEnable() == Enums.JobEnable.ENABLE.getCode(),
            elasticJobConfig.getDescription(), true);
    }

    /**
     * 修改任务
     */
    @Deprecated
    public void updateJob(ElasticJobConfigDO elasticJobConfigDO, DynamicJob dynamicJob) {

        logger.info("开始进行修改{}", elasticJobConfigDO.getJobCode());
        elasticJobHandler.updateJobConfig(elasticJobConfigDO.getJobCode(), dynamicJob, elasticJobConfigDO.getCron(), elasticJobConfigDO.getShardTotalCount(), elasticJobConfigDO.getShardingItemParameters(),
                elasticJobConfigDO.getJobParameter(),
                Enums.JobEnable.ENABLE.getCode() == elasticJobConfigDO.getEnable(), elasticJobConfigDO.getDescription(), true);
        elasticJobHandler.persistDisabledOrEnabledJob(elasticJobConfigDO.getJobCode(), null, Enums.JobEnable.DISABLE.getCode() == elasticJobConfigDO.getEnable());
    }

    /**
     * @author lisl
     * @desc 检查任务配置是否有更新，若有，则进行ZK配置更新
     * @param jobs ZK中的所有的任务
     * @param jobConfigMap 数据库中的任务
     * @param jobSettingsAPI ZK的任务配置操作API接口
     * @return 新增任务
     */
    private List<ElasticJobConfigDO> updateRegistryCenterJob(Collection<JobBriefInfo> jobs, Map<String, ElasticJobConfigDO> jobConfigMap,
        JobSettingsAPI jobSettingsAPI, JobStatisticsAPI jobStatisticsAPI) {
        
        //存储新增任务
        List<ElasticJobConfigDO> newlyJobList = Lists.newArrayList();
        Map<String, JobBriefInfo> zkJobMap = jobs.stream().collect(Collectors.toMap(JobBriefInfo :: getJobName, job -> job));
        
        //若任务找到，则判断配置信息是否更新，是的话则更新，否则不处理
        for(Entry<String, ElasticJobConfigDO> jobConfigEntry : jobConfigMap.entrySet()) {

            ElasticJobConfigDO elasticJobConfig = jobConfigEntry.getValue();
            if(!zkJobMap.containsKey(jobConfigEntry.getKey())) {
            	logger.info("当前任务未在注册中心找到：{}", jobConfigEntry.getKey());
                //数据库中的任务在ZK中没有注册，新任务
                if(Enums.JobDeleteFlag.DELETED.getCode() == elasticJobConfig.getDeleteFlag()
                    || Enums.JobEnable.DISABLE.getCode() == elasticJobConfig.getEnable()) {
                    //若该 任务是已删除、已停用，则不增加
                    logger.info("待新增的任务状态是停用/已删除，不新增该任务！");
                    continue;
                }
                newlyJobList.add(elasticJobConfig);
                continue;
            }

            //存在注册中心 则获取当前任务所有服务节点信息
            List<String> instancesNodePaths = elasticJobHandler.getInstancesNodePath(elasticJobConfig.getJobCode());
            //当前任务是否存在当前服务 没有->新增
            List<String> currentServerInstances = instancesNodePaths.stream().filter(instance -> serviceIp.equals(instance.split("@-@")[0])).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(currentServerInstances)) {
                logger.info("当前任务{} 未在当前服务{}找到 ->新增", jobConfigEntry.getKey(), serviceIp);
                if(Enums.JobDeleteFlag.DELETED.getCode() == elasticJobConfig.getDeleteFlag()
                        || Enums.JobEnable.DISABLE.getCode() == elasticJobConfig.getEnable()) {
                    //若该 任务是已删除、已停用，则不增加
                    logger.info("待新增的任务{}状态是停用/已删除，不新增该任务！", jobConfigEntry.getKey());
                    continue;
                }
                newlyJobList.add(elasticJobConfig);
                continue;
            }
            
            //同步任务状态
            logger.info("当前任务在注册中心找到：{}", jobConfigEntry.getKey());
            syncRegistryCenterJobStatus(zkJobMap, jobConfigEntry, elasticJobConfig, newlyJobList);
            
            //检查任务是否还在ZK中
            JobBriefInfo jobBriefInfo = jobStatisticsAPI.getJobBriefInfo(elasticJobConfig.getJobCode());
            if(jobBriefInfo != null) {
            	//该任务仍然在注册中心，则更新他的配置信息
            	JobSettings jobSettings = jobSettingsAPI.getJobSettings(elasticJobConfig.getJobCode());
                if(registryCenterJobChanged(jobSettings, elasticJobConfig)) {
                    
                    //任务配置有更新，进行ZK更新操作
                    logger.info("更新任务：{}在ZK中的配置信息：{}", elasticJobConfig.getJobCode(), JSON.toJSONString(jobSettings));
                    jobSettingsAPI.updateJobSettings(jobSettings);
                }
            }
        }
        return newlyJobList;
    }
    
    /**
     * @author lisl
     * @desc 将数据库中任务的状态同步到ZK注册中心任务的状态
     * @param zkJobMap ZK中的任务
     * @param jobConfigEntry 数据库中的任务
     * @param elasticJobConfig 当前需要同步的数据库的任务
     */
    private void syncRegistryCenterJobStatus(Map<String, JobBriefInfo> zkJobMap, Entry<String, ElasticJobConfigDO> jobConfigEntry,
        ElasticJobConfigDO elasticJobConfig, List<ElasticJobConfigDO> newlyJobList) {
        
        switch (zkJobMap.get(jobConfigEntry.getKey()).getStatus()) {
            case CRASHED: //ZK中任务已下线（整个服务重启）
                logger.info("任务：{}已下线，删除ZK中任务相关信息，停止任务！", elasticJobConfig.getJobCode());
                if(Enums.JobDeleteFlag.DELETED.getCode() == elasticJobConfig.getDeleteFlag()) {
                    //任务被删除
                    logger.info("任务：{}已被删除，删除ZK中任务相关信息，停止任务！", elasticJobConfig.getJobCode());
                    deleteJob(elasticJobConfig.getJobCode());
                } else if(Enums.JobEnable.ENABLE.getCode() == elasticJobConfig.getEnable()) {
                    //任务恢复正常
                    logger.info("任务：{}从下线状态恢复正常！", elasticJobConfig.getJobCode());
                    deleteJob(elasticJobConfig.getJobCode());
                    newlyJobList.add(elasticJobConfig);
                } else if(Enums.JobEnable.DISABLE.getCode() == elasticJobConfig.getEnable()) {
                    //任务已禁用
                    logger.info("任务：{}从下线状态转为停用，禁用ZK中任务相关信息，停止任务！", elasticJobConfig.getJobCode());
                    elasticJobHandler.persistDisabledOrEnabledJob(elasticJobConfig.getJobCode(), null, true);
                }
                break;
            case DISABLED: //ZK中任务已停用
                if(Enums.JobDeleteFlag.DELETED.getCode() == elasticJobConfig.getDeleteFlag()) {
                    //任务被删除
                    logger.info("任务：{}已被删除，删除ZK中任务相关信息，停止任务！", elasticJobConfig.getJobCode());
                    deleteJob(elasticJobConfig.getJobCode());                    
                } else if(Enums.JobEnable.ENABLE.getCode() == elasticJobConfig.getEnable()) {
                    //任务恢复正常
                    logger.info("任务：{}从禁用状态恢复正常！", elasticJobConfig.getJobCode());
                    elasticJobHandler.persistDisabledOrEnabledJob(elasticJobConfig.getJobCode(), null, false);
                }
                break;
            case OK: //ZK中任务状态为正常
            	if(Enums.JobDeleteFlag.DELETED.getCode() == elasticJobConfig.getDeleteFlag()) {
                    //任务被删除
                    logger.info("任务：{}已被删除，删除ZK中任务相关信息，停止任务！", elasticJobConfig.getJobCode());
                    deleteJob(elasticJobConfig.getJobCode());                    
                } else if(Enums.JobEnable.DISABLE.getCode() == elasticJobConfig.getEnable()) {
                    //任务已禁用
                    logger.info("任务：{}已被停用，禁用ZK中任务相关信息，停止任务！", elasticJobConfig.getJobCode());
                    elasticJobHandler.persistDisabledOrEnabledJob(elasticJobConfig.getJobCode(), null, true);
                }
                break;
            case SHARDING_FLAG: //分片中
                if(Enums.JobDeleteFlag.DELETED.getCode() == elasticJobConfig.getDeleteFlag()) {
                    //任务已删除
                    logger.info("任务：{}已被删除，删除ZK中任务相关信息，停止任务！", elasticJobConfig.getJobCode());
                    deleteJob(elasticJobConfig.getJobCode());                    
                }else if(Enums.JobEnable.DISABLE.getCode() == elasticJobConfig.getEnable()) {
                    //任务已禁用
                    logger.info("任务：{}已被停用，禁用ZK中任务相关信息，停止任务！", elasticJobConfig.getJobCode());
                    elasticJobHandler.persistDisabledOrEnabledJob(elasticJobConfig.getJobCode(), null, true);
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * @author
     * @desc 判断ZK中已存在的任务的配置信息是否已更新
     * @param jobsettings ZK中的配置信息，若存在变更，则将变更后的信息，存储在此对象中
     * @param elasticJobConfig 数据库中的配置信息
     * @return true: 有变更，并且变更信息已存储在入参对象中
     */
    private boolean registryCenterJobChanged(JobSettings jobsettings, ElasticJobConfigDO elasticJobConfig) {
        
        boolean changed = false;
        if(!jobsettings.getCron().equalsIgnoreCase(elasticJobConfig.getCron())) {
            //cron表达式有更新
            jobsettings.setCron(elasticJobConfig.getCron());
            changed = true;
        }
        
        //分片数默认1
        if(jobsettings.getShardingTotalCount() != Optional.ofNullable(elasticJobConfig.getShardTotalCount()).orElse(1)) {
            //分片数更新
            jobsettings.setShardingTotalCount(Optional.ofNullable(elasticJobConfig.getShardTotalCount()).orElse(1));
            changed = true;
        }
        
        //分片参数
        if(!jobsettings.getShardingItemParameters().equalsIgnoreCase(elasticJobConfig.getShardingItemParameters())) {
            jobsettings.setShardingItemParameters(elasticJobConfig.getShardingItemParameters());
            changed = true;
        }
        
        //额外参数
        if(!jobsettings.getJobParameter().equalsIgnoreCase(elasticJobConfig.getJobParameter())) {
            jobsettings.setJobParameter(elasticJobConfig.getJobParameter());
            changed = true;
        }
        
        //描述信息
        if(!jobsettings.getDescription().equalsIgnoreCase(elasticJobConfig.getDescription())) {
            jobsettings.setDescription(elasticJobConfig.getDescription());
            changed = true;
        }
        return changed;
    }
    
    /**
     * @author lisl
     * @desc 扫描注册中心
     * @return
     */
    private Collection<JobBriefInfo> scanRegistryCenter(JobStatisticsAPI jobStatisticsAPI){
        
        return jobStatisticsAPI.getAllJobsBriefInfo();
    }
    
    /**
     * @desc 扫描注数据库中的任务, 包括已禁用、已停用任务
     * @return
     */
    private List<ElasticJobConfigDO> scanDataBaseJob(){
        
        return elasticJobConfigDao.scanAllJobs();
    }

}
