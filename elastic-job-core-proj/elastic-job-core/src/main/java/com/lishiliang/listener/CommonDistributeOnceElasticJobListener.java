package com.lishiliang.listener;

 import com.dangdang.ddframe.job.executor.ShardingContexts;
 import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;


 /**
  * @desc 在分布式作业中只执行一次的监听器.默认实现
  */
 public class CommonDistributeOnceElasticJobListener extends AbstractDistributeOnceElasticJobListener {

     private static final Logger logger = LoggerFactory.getLogger(CommonDistributeOnceElasticJobListener.class);

     public CommonDistributeOnceElasticJobListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {

         super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
     }

     @Override
     public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {

         logger.info("分布式环境中最后一个作业执行前的执行的方法.{}", shardingContexts.getJobName());
     }

     @Override
     public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {

         logger.info("分布式环境中最后一个作业执行后的执行的方法.{}", shardingContexts.getJobName());
     }

 }
