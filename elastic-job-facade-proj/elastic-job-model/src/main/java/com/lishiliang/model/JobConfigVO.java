package com.lishiliang.model;

import java.io.Serializable;


public class JobConfigVO implements Serializable {

    private static final long serialVersionUID = -4823668114260948973L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 作业编号
     */
    private String jobCode;
    /**
     * 作业名称
     */
    private String jobName;
    /**
     * 业务来源(包含kettle_job...)
     * @see com.lishiliang.model.Enums.JobBusinessSource
     */
    private String businessSource;
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
     * (kettle_job参数不能为空多个参数以;拼接 格式如: ktrFile=/home/kettle/ktr&jobDirection=1)
     */
    private String jobParameter;
    /**
     * 描述
     */
    private String description;
    /**
     * 添加完成时是否启用
     */
    private boolean enable;
    /**
     * 运行环境
     */
    private String profiles;
    /**
     * 软删除标识 1-已删除不可用 0-未删除可用
     */
    private boolean deleteFlag;
    /**
     * 扩展参数
     */
    private String extraParameter;
    /**
     * todo
     * 运行周期：1-秒，2-日，3-周，4-月，5-季度，6-年
     */
    private Integer jobCycle;
    /**
     * 任务运行间隔，配合任务运行周期使用
     */
    private Integer jobPeriod;
    /**
     * 任务是否可订阅。0-不可订阅1-可订阅
     */
    private Integer jobCanSubscribe;
    /**
     * 任务归属权限机构
     */
    private String jobOrgNo;
    /**
     * 任务的依赖列表，存储结构：kettle-job:syncPaasAgencyHierarchyJob,report-job:agentDevelopDetailPushTask
     */
    private String jobDependences;
    /**
     * 任务首次执行日期，默认为当前日期的前一天yyyyMMdd，若任务是月任务，存储yyyyMM，若任务是年任务，存储yyyy
     */
    private String jobFirstDate;
    /**
     * 任务首次执行时间，默认为对应日期的0点HHmmss
     */
    private String jobFirstTime;
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



    public JobConfigVO() {
    }

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

    public String getBusinessSource() {
        return businessSource;
    }

    public void setBusinessSource(String businessSource) {
        this.businessSource = businessSource;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getShardTotalCount() {
        return shardTotalCount;
    }

    public String getJobFirstDate() {
        return jobFirstDate;
    }

    public void setJobFirstDate(String jobFirstDate) {
        this.jobFirstDate = jobFirstDate;
    }

    public String getJobFirstTime() {
        return jobFirstTime;
    }

    public void setJobFirstTime(String jobFirstTime) {
        this.jobFirstTime = jobFirstTime;
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
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

    public Integer getJobCycle() {
        return jobCycle;
    }

    public void setJobCycle(Integer jobCycle) {
        this.jobCycle = jobCycle;
    }

    public Integer getJobPeriod() {
        return jobPeriod;
    }

    public void setJobPeriod(Integer jobPeriod) {
        this.jobPeriod = jobPeriod;
    }

    public Integer getJobCanSubscribe() {
        return jobCanSubscribe;
    }

    public void setJobCanSubscribe(Integer jobCanSubscribe) {
        this.jobCanSubscribe = jobCanSubscribe;
    }

    public String getJobOrgNo() {
        return jobOrgNo;
    }

    public void setJobOrgNo(String jobOrgNo) {
        this.jobOrgNo = jobOrgNo;
    }

    public String getJobDependences() {
        return jobDependences;
    }

    public void setJobDependences(String jobDependences) {
        this.jobDependences = jobDependences;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}