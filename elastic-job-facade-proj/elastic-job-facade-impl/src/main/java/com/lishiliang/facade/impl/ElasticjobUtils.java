

package com.lishiliang.facade.impl;


import com.dangdang.ddframe.job.lite.lifecycle.api.JobOperateAPI;
import com.dangdang.ddframe.job.lite.lifecycle.domain.JobBriefInfo;
import com.dangdang.ddframe.job.lite.lifecycle.domain.ShardingInfo;
import com.dangdang.ddframe.job.lite.lifecycle.internal.operate.JobOperateAPIImpl;
import com.dangdang.ddframe.job.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl;
import com.dangdang.ddframe.job.lite.lifecycle.internal.statistics.ShardingStatisticsAPIImpl;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author lisl
 * @desc 任务管理
 */
@Component
@EnableAsync
public class ElasticjobUtils {

    private static final Logger logger = LoggerFactory.getLogger(ElasticjobUtils.class);

    private static final ConcurrentHashMap<HashCode, CoordinatorRegistryCenter> REG_CENTER_REGISTRY = new ConcurrentHashMap<>();

    /**
     * zkUrl ZK连接地址，ip:port,ip:port,ip:port
     */
    @Value("${elastic.job.zk.server}")
    private String zkUrl;

    /**
     * 任务所在命名空间，多个以逗号分开
     */
    @Value("${elastic.job.zk.namespace}")
    private String namespace;
    /**
     * @author lisl
     * @desc 手动触发JOB调度
     * @param jobName 任务名称
     */
    @Async
    public void triggerJob(String jobName) {

        logger.info("手动触发任务：{}开始", jobName);
        String [] namespaceArray = StringUtils.split(namespace, ",");
        for(String space : namespaceArray) {
            JobOperateAPI jobOperateAPI = new JobOperateAPIImpl(createCoordinatorRegistryCenter(zkUrl, space, Optional.fromNullable(null)));
            jobOperateAPI.trigger(Optional.of(jobName), Optional.<String>absent());
        }
        logger.info("手动触发任务：{}完成", jobName);
    }

    /**
     * @author lisl
     * @desc 获取对应job 对应namespace的 job运行信息
     * @param jobName
     * @param namespace
     */
    @Async
    public Future<JobBriefInfo> getJobBriefInfo(String jobName, String namespace) {

        JobStatisticsAPIImpl jobStatisticsAPI = new JobStatisticsAPIImpl(createCoordinatorRegistryCenter(zkUrl, namespace, Optional.fromNullable(null)));
        return new AsyncResult<>(jobStatisticsAPI.getJobBriefInfo(jobName));
    }

    /**
     * @author lisl
     * @desc 获取对应job 对应namespace的 job分片信息
     * @param jobName
     * @param namespace
     */
    @Async
    public Future<Collection<ShardingInfo>> getShardingInfo(String jobName, String namespace) {

        ShardingStatisticsAPIImpl shardingStatisticsAPI = new ShardingStatisticsAPIImpl(createCoordinatorRegistryCenter(zkUrl, namespace, Optional.fromNullable(null)));
        return new AsyncResult(shardingStatisticsAPI.getShardingInfo(jobName));
    }


    /**
     * @author lisl
     */
    @Async
    public void invokeMethod(String jobName, String methodName, String namespace) {

        logger.info("手动{}任务：{}开始", methodName, jobName);
        JobOperateAPI jobOperateAPI = new JobOperateAPIImpl(createCoordinatorRegistryCenter(zkUrl, namespace, Optional.fromNullable(null)));

        try {
            Method method = jobOperateAPI.getClass().getMethod(methodName, new Class[]{Optional.class, Optional.class});
            method.invoke(jobOperateAPI, Optional.of(jobName), Optional.<String>absent());
        } catch (Exception e) {

            logger.error(e.getMessage());
        }
        logger.info("手动{}任务：{}完成", methodName, jobName);
    }

    /**
     * 创建注册中心.
     *
     * @param connectString 注册中心连接字符串
     * @param namespace 注册中心命名空间
     * @param digest 注册中心凭证
     * @return 注册中心对象
     */
    private CoordinatorRegistryCenter createCoordinatorRegistryCenter(final String connectString, final String namespace, final Optional<String> digest) {
        Hasher hasher =  Hashing.md5().newHasher().putString(connectString, Charsets.UTF_8).putString(namespace, Charsets.UTF_8);
        if (digest.isPresent()) {
            hasher.putString(digest.get(), Charsets.UTF_8);
        }
        HashCode hashCode = hasher.hash();
        CoordinatorRegistryCenter result = REG_CENTER_REGISTRY.get(hashCode);
        if (null != result) {
            return result;
        }
        ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(connectString, namespace);
        if (digest.isPresent()) {
            zkConfig.setDigest(digest.get());
        }
        zkConfig.setConnectionTimeoutMilliseconds(60000);
        zkConfig.setSessionTimeoutMilliseconds(60000);
        zkConfig.setMaxSleepTimeMilliseconds(6000);
        zkConfig.setBaseSleepTimeMilliseconds(3000);

        result = new ZookeeperRegistryCenter(zkConfig);
        result.init();
        REG_CENTER_REGISTRY.put(hashCode, result);
        return result;
    }


}
