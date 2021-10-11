package com.lishiliang.model;

import java.io.Serializable;

/**
 * @author lisl
 * @version 1.0
 * @desc : 配置信息
 */
public class ElasticJobConfigDO implements Serializable {

    private static final long serialVersionUID = -4923778142260948973L;
    /**
     * id
     */
    private Long id;
    /**
     * 作业名称
     */
    private String jobCode;
    /**
     * cron表达式，用于控制作业触发时间
     */
    private String cron;
    /**
     * 分片数 默认1
     */
    private int shardTotalCount;
    /**
     * 分片序列 列号从0开始,不可大于或等于作业分片总数.如：0=a,1=b,2=c
     */
    private String shardingItemParameters;
    /**
     * (参数不能为空 格式如: ktrFile=/home/kettle/ktr,jobDirection=1)
     */
    private String jobParameter;
    /**
     * 描述信息
     */
    private String description;
    /**
     * 添加完成时是否启用
     */
    private Integer enable;
    /**
     * 运行环境变量
     */
    private String profiles;
    /**
     * 软删除标识 1-已删除不可用 0-未删除可用
     */
    private Integer deleteFlag;
    /**
     * 扩展参数
     */
    private String extraParameter;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 创建时间
     */
    private String updateTime;
    /**
     * 创建人
     */
    private String createOper;
    /**
     * 修改人
     */
    private String updateOper;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getShardTotalCount() {
        return shardTotalCount;
    }

    public void setShardTotalCount(int shardTotalCount) {
        this.shardTotalCount = shardTotalCount;
    }

    public String getShardingItemParameters() {
        return shardingItemParameters;
    }

    public void setShardingItemParameters(String shardingItemParameters) {
        this.shardingItemParameters = shardingItemParameters;
    }

    public String getJobParameter() {
        return jobParameter;
    }

    public void setJobParameter(String jobParameter) {
        this.jobParameter = jobParameter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getExtraParameter() {
        return extraParameter;
    }

    public void setExtraParameter(String extraParameter) {
        this.extraParameter = extraParameter;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateOper() {
        return createOper;
    }

    public void setCreateOper(String createOper) {
        this.createOper = createOper;
    }

    public String getUpdateOper() {
        return updateOper;
    }

    public void setUpdateOper(String updateOper) {
        this.updateOper = updateOper;
    }

    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }
}