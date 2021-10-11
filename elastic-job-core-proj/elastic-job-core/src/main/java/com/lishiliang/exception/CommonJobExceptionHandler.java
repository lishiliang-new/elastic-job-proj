/**
 * 
 */
package com.lishiliang.exception;

import com.dangdang.ddframe.job.executor.handler.JobExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义异常
 */
public class CommonJobExceptionHandler implements JobExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(CommonJobExceptionHandler.class);
    
    @Override
    public void handleException(String jobName, Throwable cause) {

        logger.error("任务："+ jobName +" 执行异常！", cause);
    }

}
