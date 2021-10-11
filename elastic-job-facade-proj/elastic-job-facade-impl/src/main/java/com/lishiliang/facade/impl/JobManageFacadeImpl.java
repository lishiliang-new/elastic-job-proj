package com.lishiliang.facade.impl;


import com.dangdang.ddframe.job.lite.lifecycle.domain.JobBriefInfo;
import com.dangdang.ddframe.job.lite.lifecycle.domain.ShardingInfo;
import com.lishiliang.model.JobDetailVO;
import com.lishiliang.model.JobStatus;
import com.lishiliang.core.utils.AssertUtils;
import com.lishiliang.core.utils.CatchUtils;
import com.lishiliang.facade.JobManageFacade;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;


@Service(timeout=60000, filter="-exception")
public class JobManageFacadeImpl implements JobManageFacade {



    @Value("#{'${kettle.ktr.file.path:}'.empty?''.split(','):'${kettle.ktr.file.path:}'.split(',')}")
    private List<String> ktrFilePaths;

    @Autowired
    private ElasticjobUtils elasticjobUtils;




    /**
     * 手动触发任务
     * @return
     */
    @Override
    public void triggerJob(String jobCode) {

        //异步操作任务
        this.invokeMethod(jobCode, "trigger");
    }


    /**
     * 文件上传
     * @return
     */
    @Override
    public String uploadKtrFile(byte[] bytes, String orgFileName, String jobCode) {
        //根据jobCode前缀 匹配上传路径
        String fileRemotePath = ktrFilePaths.stream().filter(ktrFilePath->jobCode.startsWith(ktrFilePath.substring(ktrFilePath.lastIndexOf("/") + 1))).findFirst().orElse(null);
        AssertUtils.isNotBlank(fileRemotePath, "1011", "上传路径不能为空, 上传路径以jobCode前缀进行匹配 请检查配置路径!");
        final String filePath = fileRemotePath + "/" + orgFileName;

        //文件复制
        FileOutputStream out = null;
        try {
            File file = new File(filePath);
            if(!file.exists()){
                //创建文件夹
                file.getParentFile().mkdirs();
                //创建文件
                file.createNewFile();
            }
            out = new FileOutputStream(filePath);
            out.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException("文件复制失败 msg:" + e.getMessage());
        } finally {
            if (null != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return filePath;
    }

    @Override
    public JobDetailVO getJobDetail(String jobCode) {

        Future<JobBriefInfo> briefInfoFuture = elasticjobUtils.getJobBriefInfo(jobCode, "elastic_job");
        Future<Collection<ShardingInfo>> shardingInfoFuture = elasticjobUtils.getShardingInfo(jobCode, "elastic_job");

        JobBriefInfo jobBriefInfo = CatchUtils.Receive.catchException(Future::get, briefInfoFuture);

        JobDetailVO jobDetail = new JobDetailVO();
        jobDetail.setJobCode(jobCode);
        if (jobBriefInfo == null) {
            return jobDetail;
        }
        if (jobBriefInfo.getStatus() != null) {
            jobDetail.setJobStatus(JobStatus.valueOf(jobBriefInfo.getStatus().name()).desc);
        }
        jobDetail.setInstanceCount(jobBriefInfo.getInstanceCount());

        //没有下线获取分片信息
        if (!JobBriefInfo.JobStatus.CRASHED.equals(jobBriefInfo.getStatus())) {
            Collection<ShardingInfo> shardingInfos = CatchUtils.Receive.catchException(Future::get, shardingInfoFuture);
            jobDetail.setShardingInfos(shardingInfos);
        }
        return jobDetail;
    }

    @Override
    public void removeJob(String jobCode) {

        this.invokeMethod(jobCode, "remove");
    }


    /**
     * 执行JobOperateAPI对应方法
     * @param
     */
    public void invokeMethod(String jobCode, String methodName) {

        elasticjobUtils.invokeMethod(jobCode, methodName,"elastic_job");
    }
}
