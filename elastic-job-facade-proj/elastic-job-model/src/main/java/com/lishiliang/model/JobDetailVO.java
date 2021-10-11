package com.lishiliang.model;

import java.io.Serializable;
import java.util.Collection;


public class JobDetailVO<T> implements Serializable {

    private static final long serialVersionUID = 2964567157402594280L;

    /**
     * jobCode
     */
    private String jobCode;
    /**
     * 运行状态
     * @see com.lishiliang.model.JobStatus
     */
    private String jobStatus;
    /**
     * 注册的实例个数
     */
    private Integer instanceCount;
    /**
     * job分片信息
     */
    private Collection<T> shardingInfos;


    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Integer getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
    }

    public Collection<T> getShardingInfos() {
        return shardingInfos;
    }

    public void setShardingInfos(Collection<T> shardingInfos) {
        this.shardingInfos = shardingInfos;
    }
}
