package com.lishiliang.handler;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.config.LiteJobConfigurationGsonFactory;
import com.dangdang.ddframe.job.lite.internal.schedule.JobRegistry;
import com.dangdang.ddframe.job.lite.internal.schedule.JobScheduleController;
import com.dangdang.ddframe.job.lite.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.common.base.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author lisl
 * @version 1.0
 * @desc : 动态增删改ElasticJob的处理类
 */
@Component
public class ElasticJobHandler {

    private static final Logger logger = LoggerFactory.getLogger(ElasticJobHandler.class);

    @Autowired
    private ApplicationContext ctx;
    
    @Autowired
    private ZookeeperRegistryCenter registryCenter;

    /**
     * 构建定时任务配置信息
     * @param jobName
     * @param jobClass
     * @param shardTotalCount
     * @param cron
     * @param jobParameter
     * @param description
     * @return
     */
    private static LiteJobConfiguration.Builder simpleJobConfigBuilder(String jobName,
        Class<? extends SimpleJob> jobClass, int shardTotalCount, String cron, String shardingItemParameters,
        String jobParameter, String description) {
        
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder(jobName, cron, shardTotalCount)
            .jobParameter(jobParameter).shardingItemParameters(shardingItemParameters).description(description)
            .failover(true)
            // 错过时间重试(misfire)
            .misfire(true).build();
        return LiteJobConfiguration
            .newBuilder(new SimpleJobConfiguration(jobCoreConfiguration, jobClass.getCanonicalName()));
    }

    /**
     * 构建jobConfig
     * @return
     */
    public LiteJobConfiguration buildLiteJobConfiguration(String jobCode, SimpleJob jobInstance, String cron, Integer shardTotalCount, 
        String shardingItemParameters, String jobParameter, boolean enable, String description, boolean overwrite) {

        return simpleJobConfigBuilder(jobCode, jobInstance.getClass(), shardTotalCount, cron, shardingItemParameters, jobParameter, description)
                //本地配置是否可覆盖注册中心配置 如果可覆盖，每次启动作业都以本地配置为准
                .overwrite(true)
                //添加完成后是否禁用
                .disabled(!enable)
                //onitorExecution  TODO 如果任务调度频繁 可关闭
                .monitorExecution(false)
                .build();
    }

    /**
     * 添加一个定时任务 并执行
     */
    public void addJob(String jobCode, SimpleJob jobInstance, String cron, Integer shardTotalCount, String shardingItemParameters, 
        String jobParameter, boolean enable, String description, boolean overwrite) {

        LiteJobConfiguration jobConfig = buildLiteJobConfiguration(jobCode,jobInstance,cron,shardTotalCount,shardingItemParameters,jobParameter,enable,description,overwrite );
        
        //构建监听
        List<BeanDefinition> elasticJobListeners = getTargetElasticJobListeners();
        // 构建SpringJobScheduler对象来初始化任务
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
        factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        
        //获取上下文
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)ctx.getAutowireCapableBeanFactory();
        //构造函数注入任务执行类
        factory.addConstructorArgValue(jobInstance);
        factory.addConstructorArgValue(registryCenter);
        factory.addConstructorArgValue(jobConfig);
        factory.addConstructorArgValue(elasticJobListeners);
        
        defaultListableBeanFactory.registerBeanDefinition("SpringJobScheduler", factory.getBeanDefinition());
        SpringJobScheduler springJobScheduler = (SpringJobScheduler) ctx.getBean("SpringJobScheduler");
        springJobScheduler.init();

        logger.info("【{}】\t{}\"\tinit success", jobCode, jobInstance.getClass().getName());
    }

    /**
     * @desc 处理监听
     * @return
     */
    private List<BeanDefinition> getTargetElasticJobListeners() {
        List<BeanDefinition> result = new ManagedList<>(2);
        //默认监听接口
        String listeners = "com.lishiliang.listener.CommonJobListener";
        if (StringUtils.hasText(listeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            result.add(factory.getBeanDefinition());
            //TODO 直接结束不添加分布式监听接口 会导致多服务下任务下线在上线后分片信息不更新导致任务无法执行
            return result;
        }
        
        //默认分布式监听接口
        String distributedListeners = "com.lishiliang.listener.CommonDistributeOnceElasticJobListener";
        long startedTimeoutMilliseconds = 0L;
        long completedTimeoutMilliseconds = 0L;
        
        if (StringUtils.hasText(distributedListeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            factory.addConstructorArgValue(startedTimeoutMilliseconds);
            factory.addConstructorArgValue(completedTimeoutMilliseconds);
            result.add(factory.getBeanDefinition());
        }
        return result;
    }

    /**
     * 更新定时任务
     * @param jobCode
     * @param cron
     */
    public void rescheduleJob(String jobCode, String cron) {

        JobScheduleController jobScheduleController = JobRegistry.getInstance().getJobScheduleController(jobCode);
        try {
            jobScheduleController.rescheduleJob(cron);
        } catch (Exception e){
            jobScheduleController.scheduleJob(cron);
        }
    }

    /**
     * 修改job配置
     * @param jobCode
     * @param jobInstance
     * @param cron
     * @param shardTotalCount
     * @param shardingItemParameters
     * @param jobParameter
     * @param enable
     * @param description
     * @param overwrite
     */
    public void updateJobConfig(String jobCode, SimpleJob jobInstance, String cron, Integer shardTotalCount, String shardingItemParameters, String jobParameter, boolean enable, String description, boolean overwrite) {

        LiteJobConfiguration jobConfig = buildLiteJobConfiguration(jobCode,jobInstance,cron,shardTotalCount,shardingItemParameters,jobParameter,enable,description,overwrite );
        registryCenter.update(new JobNodePath(jobCode).getConfigNodePath(), LiteJobConfigurationGsonFactory.toJsonForObject(jobConfig));
    }

    /**
     * 停止当前服务的定时任务并删除当前服务器当前job实例 并会转移到其他服务运行（相当于当前服务器失效job转移到其他服务运行）
     * @param jobName
     */
    public void shutdownJob(String jobName){
        JobScheduleController jobScheduleController = JobRegistry.getInstance().getJobScheduleController(jobName);
        jobScheduleController.shutdown();
    }


    /**
     * 停止所有服务的当前job
     * @param jobName
     * @param serverIp
     */
    public void shutdownAllServiceJob(String jobName, String serverIp){

        if (null != jobName && null != serverIp) {
            JobNodePath jobNodePath = new JobNodePath(jobName);
            for (String each : registryCenter.getChildrenKeys(jobNodePath.getInstancesNodePath())) {
                if (serverIp.equals(each.split("@-@")[0])) {
                    registryCenter.remove(jobNodePath.getInstanceNodePath(each));
                }
            }
        } else if (null != jobName) {
            JobNodePath jobNodePath = new JobNodePath(jobName);
            for (String each : registryCenter.getChildrenKeys(jobNodePath.getInstancesNodePath())) {
                registryCenter.remove(jobNodePath.getInstanceNodePath(each));
            }
        } else {
            List<String> jobNames = registryCenter.getChildrenKeys("/");
            for (String job : jobNames) {
                JobNodePath jobNodePath = new JobNodePath(job);
                List<String> instances = registryCenter.getChildrenKeys(jobNodePath.getInstancesNodePath());
                for (String each : instances) {
                    if (serverIp.equals(each.split("@-@")[0])) {
                        registryCenter.remove(jobNodePath.getInstanceNodePath(each));
                    }
                }
            }
        }
    }

    /**
     * 等效console的终止按钮效果 会停止job并删除所有服务的当前job的实例
     * @param jobName
     * @param serverIp
     */
    public void remove(final String jobName, final String serverIp) {
        shutdownAllServiceJob(jobName, serverIp);
        if (null != jobName && null != serverIp) {
            registryCenter.remove(new JobNodePath(jobName).getServerNodePath(serverIp));
        } else if (null != jobName) {
            JobNodePath jobNodePath = new JobNodePath(jobName);
            List<String> servers = registryCenter.getChildrenKeys(jobNodePath.getServerNodePath());
            for (String each : servers) {
                registryCenter.remove(jobNodePath.getServerNodePath(each));
            }
        } else if (null != serverIp) {
            List<String> jobNames = registryCenter.getChildrenKeys("/");
            for (String each : jobNames) {
                registryCenter.remove(new JobNodePath(each).getServerNodePath(serverIp));
            }
        }
    }

    /**
     * 更新对应job对应服务分片信息
     * @param jobName
     */
    public void updateShardingNodePath(final String jobName, final String serverIp) {
        String shardingRootPath = new JobNodePath(jobName).getShardingNodePath();
        List<String> items = registryCenter.getChildrenKeys(shardingRootPath);

        for (String each : items) {
            if (serverIp == null) {
                registryCenter.remove(String.format("%s/%s", shardingRootPath, each));
            } else {
                JobNodePath jobNodePath = new JobNodePath(jobName);
                String oldInstanceId = registryCenter.get(jobNodePath.getShardingNodePath(each, "instance"));
                if (null != oldInstanceId && serverIp.equals(oldInstanceId.split("@-@")[0])) {
                    String newInstanceId = registryCenter.getChildrenKeys(jobNodePath.getInstancesNodePath()).stream().filter(path->serverIp.equals(path.split("@-@")[0])).findFirst().orElse("");
                    registryCenter.update(jobNodePath.getShardingNodePath(each, "instance"), newInstanceId);
                }
            }
        }

    }

    /**
     * 获取对应job对应服务的所有分片项
     * @param jobName
     * @param serverIp
     * @return
     */
    public List<String> getShardingItems(final String jobName, final String serverIp) {
        List<String> result = new ArrayList<>();
        String shardingRootPath = new JobNodePath(jobName).getShardingNodePath();
        List<String> items = registryCenter.getChildrenKeys(shardingRootPath);

        for (String each : items) {
            if (serverIp == null) {
                result.add(each);
            } else {
                JobNodePath jobNodePath = new JobNodePath(jobName);
                String oldInstanceId = registryCenter.get(jobNodePath.getShardingNodePath(each, "instance"));
                if (null != oldInstanceId && serverIp.equals(oldInstanceId.split("@-@")[0])) {
                    result.add(each);
                }
            }
        }
        return result;
    }

    /**
     * 获取对应job所有服务的所有分片项
     * @param jobName
     * @return key=服务 value=该服务所获取的分片项
     */
    public Map<String, Set<Integer>> zkServerShadingInfo(final String jobName) {

        Map<String, Set<Integer>> resultMap = new HashMap();
        String shardingRootPath = new JobNodePath(jobName).getShardingNodePath();
        List<String> items = registryCenter.getChildrenKeys(shardingRootPath);
        for (String each : items) {
            JobNodePath jobNodePath = new JobNodePath(jobName);
            String instance = registryCenter.get(jobNodePath.getShardingNodePath(each, "instance"));
            String server = instance.split("@-@")[0];
            Set<Integer> instanceItems = resultMap.getOrDefault(server, new HashSet<>());
            instanceItems.add(Integer.valueOf(each));
            resultMap.put(server, instanceItems);
        }
        return resultMap;
    }

    /**
     * 获取对应job所有服务注册的实例信息
     * @param jobName
     */
    public List<String> getInstancesNodePath(String jobName){
        return this.registryCenter.getChildrenKeys(new JobNodePath(jobName).getInstancesNodePath());
    }

    /**
     * 删除分片信息
     * @param jobName
     */
    public void removeShardingNodePath(final String jobName) {
        String shardingRootPath = new JobNodePath(jobName).getShardingNodePath();
        List<String> items = registryCenter.getChildrenKeys(shardingRootPath);
        for (String each : items) {
            registryCenter.remove(String.format("%s/%s", shardingRootPath, each));
        }
    }

    /**
     * 删除配置信息 等效console终止后的删除按钮效果
     * @param jobName
     */
    public void removeJobConfiguration(final String jobName) {
        registryCenter.remove("/" + jobName);
    }

    /**
     * 获取当前实例个数
     * @param jobName
     * @return
     */
    public int getJobInstanceCount(String jobName) {
        return registryCenter.getChildrenKeys(new JobNodePath(jobName).getInstancesNodePath()).size();
    }

    /**
     * 手动触发当前服务器的任务
     * @param jobName
     */
    public void triggerJob(String jobName){
        JobRegistry.getInstance().getJobScheduleController(jobName).triggerJob();
    }

    /**
     * 手动触发所有服务器的任务
     * @param jobName
     * @param serverIp
     */
    public void triggerJob(Optional<String> jobName, Optional<String> serverIp) {
        if (jobName.isPresent()) {
            JobNodePath jobNodePath = new JobNodePath((String)jobName.get());
            Iterator<String> iterator = this.registryCenter.getChildrenKeys(jobNodePath.getInstancesNodePath()).iterator();

            while(iterator.hasNext()) {
                String each = (String)iterator.next();
                this.registryCenter.persist(jobNodePath.getInstanceNodePath(each), "TRIGGER");
            }
        }
    }


    /**
     * 获取当前服务注册的JobInstanceId
     * @param jobName
     */
    public String currentServiceJobInstanceId(String jobName){
        JobRegistry.getInstance().getJobScheduleController(jobName).pauseJob();
        return JobRegistry.getInstance().getJobInstance(jobName).getJobInstanceId();
    }



    /**
     *  等效conlose生效失效效果 disabled（true-禁用 false-启用）
     * @param jobName
     * @param serverIp
     * @param disabled （true-禁用 false-启用）
     */
    public void persistDisabledOrEnabledJob(String jobName, String serverIp, boolean disabled) {
        if (null != jobName && serverIp == null) {
            JobNodePath jobNodePath = new JobNodePath(jobName);
            for (String each : registryCenter.getChildrenKeys(jobNodePath.getServerNodePath())) {
                if (disabled) {
                    registryCenter.persist(jobNodePath.getServerNodePath(each), "DISABLED");
                } else {
                    registryCenter.persist(jobNodePath.getServerNodePath(each), "ENABLED");
                }
            }
        } else {
            List<String> jobNames = registryCenter.getChildrenKeys("/");
            for (String each : jobNames) {
                if (registryCenter.isExisted(new JobNodePath(each).getServerNodePath(serverIp))) {
                    persistDisabledOrEnabledJob(each, serverIp, disabled);
                }
            }
        }
    }
}
