
package com.lishiliang.listener;

 import com.dangdang.ddframe.job.executor.ShardingContexts;
 import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;


 /**
  * @desc: 监听
  */
 public class CommonJobListener implements ElasticJobListener {

     private static final Logger logger = LoggerFactory.getLogger(CommonJobListener.class);

     @Override
     public void beforeJobExecuted(ShardingContexts shardingContexts) {

         logger.info("任务{}执行前", shardingContexts.getJobName());
     }

     @Override
     public void afterJobExecuted(ShardingContexts shardingContexts) {

         logger.info("任务{}执行后", shardingContexts.getJobName());
     }

 }
