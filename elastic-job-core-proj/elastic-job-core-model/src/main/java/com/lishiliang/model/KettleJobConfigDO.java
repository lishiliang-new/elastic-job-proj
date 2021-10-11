//
///**
// * @Title: KettleJobConfig.java
// * @Package:com.lishiliang.kettle.model
// * @desc: TODO
// * @author: lisl
// * @date:2021年3月29日 下午3:40:52
// */
//
//package com.lishiliang.model;
//
//import java.io.Serializable;
//import java.util.Date;
//
///**
// * @author lisl
// * @desc
// */
//public class KettleJobConfigDO implements Serializable {
//
//    /**
//     * @desc
//     */
//    private static final long serialVersionUID = -6999718403511962907L;
//
//    /** 主键 */
//    private Long id;
//
//    /** 任务编号 */
//    private String jobCode;
//
//    /** 任务名称 */
//    private String jobName;
//
//    /** 业务来源;(report_job,kettle_job...) */
//    private String businessSource;
//
//    /** cron表达式;用于控制作业触发时间 */
//    private String cron;
//
//    /** 作业分片总数 */
//    private Integer shardTotalCount;
//
//    /** 分片序列;列号从0开始,不可大于或等于作业分片总数.如：0=a,1=b,2=c */
//    private String shardingItemParameters;
//
//    /** 作业扩展参数;(kettle_job额外参数不能为空 格式如:ktrFile=/home/kettle/ktr;jobDirection=1) */
//    private String jobParameter;
//
//    /** 作业描述信息 */
//    private String description;
//
//    /** 扩展参数 */
//    private String extraParameter;
//
//    /** 是否启用;1-启用 0-不启用 */
//    private Integer enable;
//
//    /** 当前运行环境;dev test prod */
//    private String profiles;
//
//    /** 删除标识;1-已删除不可用 0-未删除可用 默认0 */
//    private Integer deleteFlag;
//
//    /** 任务运行周期;1-秒，2-日，3-周，4-月，5-季度，6-年 */
//    private Integer jobCycle;
//
//    /** 任务运行间隔;配合任务运行周期使用 */
//    private Integer jobPeriod;
//
//    /** 任务是否可订阅;0-不可订阅1-可订阅 */
//    private Integer jobCanSubscribe;
//
//    /** 任务归属权限机构 */
//    private String jobOrgNo;
//
//    /** 任务的依赖列表;存储结构：kettle-job:syncPaasAgencyHierarchyJob,report-job:agentDevelopDetailPushTask */
//    private String jobDependences;
//
//    /** 任务首次执行日期;默认为当前日期的前一日 */
//    private String jobFirstDate ;
//    /** 任务首次执行时间;默认为对应日期的0点 */
//    private String jobFirstTime ;
//
//    /** 创建人 */
//    private String createOper;
//
//    /** 创建时间 */
//    private Date createTime;
//
//    /** 更新人 */
//    private String updateOper;
//
//    /** 更新时间 */
//    private Date updateTime;
//
//    /** 主键 */
//    public Long getId() {
//
//        return this.id;
//    }
//
//    /** 主键 */
//    public void setId(Long id) {
//
//        this.id = id;
//    }
//
//    /** 任务编号 */
//    public String getJobCode() {
//
//        return this.jobCode;
//    }
//
//    /** 任务编号 */
//    public void setJobCode(String jobCode) {
//
//        this.jobCode = jobCode;
//    }
//
//    /** 任务名称 */
//    public String getJobName() {
//
//        return this.jobName;
//    }
//
//    /** 任务名称 */
//    public void setJobName(String jobName) {
//
//        this.jobName = jobName;
//    }
//
//    /** 业务来源;(report_job,kettle_job...) */
//    public String getBusinessSource() {
//
//        return this.businessSource;
//    }
//
//    /** 业务来源;(report_job,kettle_job...) */
//    public void setBusinessSource(String businessSource) {
//
//        this.businessSource = businessSource;
//    }
//
//    /** cron表达式;用于控制作业触发时间 */
//    public String getCron() {
//
//        return this.cron;
//    }
//
//    /** cron表达式;用于控制作业触发时间 */
//    public void setCron(String cron) {
//
//        this.cron = cron;
//    }
//
//    /** 作业分片总数 */
//    public Integer getShardTotalCount() {
//
//        return this.shardTotalCount;
//    }
//
//    /** 作业分片总数 */
//    public void setShardTotalCount(Integer shardTotalCount) {
//
//        this.shardTotalCount = shardTotalCount;
//    }
//
//    /** 分片序列;列号从0开始,不可大于或等于作业分片总数.如：0=a,1=b,2=c */
//    public String getShardingItemParameters() {
//
//        return this.shardingItemParameters;
//    }
//
//    /** 分片序列;列号从0开始,不可大于或等于作业分片总数.如：0=a,1=b,2=c */
//    public void setShardingItemParameters(String shardingItemParameters) {
//
//        this.shardingItemParameters = shardingItemParameters;
//    }
//
//    /** 作业扩展参数;(kettle_job额外参数不能为空 格式如:ktrFile=/home/kettle/ktr;jobDirection=1) */
//    public String getJobParameter() {
//
//        return this.jobParameter;
//    }
//
//    /** 作业扩展参数;(kettle_job额外参数不能为空 格式如:ktrFile=/home/kettle/ktr;jobDirection=1) */
//    public void setJobParameter(String jobParameter) {
//
//        this.jobParameter = jobParameter;
//    }
//
//    /** 作业描述信息 */
//    public String getDescription() {
//
//        return this.description;
//    }
//
//    /** 作业描述信息 */
//    public void setDescription(String description) {
//
//        this.description = description;
//    }
//
//    /** 扩展参数 */
//    public String getExtraParameter() {
//
//        return this.extraParameter;
//    }
//
//    /** 扩展参数 */
//    public void setExtraParameter(String extraParameter) {
//
//        this.extraParameter = extraParameter;
//    }
//
//    /** 是否启用;1-启用 0-不启用 */
//    public Integer getEnable() {
//
//        return this.enable;
//    }
//
//    /** 是否启用;1-启用 0-不启用 */
//    public void setEnable(Integer enable) {
//
//        this.enable = enable;
//    }
//
//    /** 当前运行环境;dev test prod */
//    public String getProfiles() {
//
//        return this.profiles;
//    }
//
//    /** 当前运行环境;dev test prod */
//    public void setProfiles(String profiles) {
//
//        this.profiles = profiles;
//    }
//
//    /** 删除标识;1-已删除不可用 0-未删除可用 默认0 */
//    public Integer getDeleteFlag() {
//
//        return this.deleteFlag;
//    }
//
//    /** 删除标识;1-已删除不可用 0-未删除可用 默认0 */
//    public void setDeleteFlag(Integer deleteFlag) {
//
//        this.deleteFlag = deleteFlag;
//    }
//
//    /** 任务运行周期;1-秒，2-日，3-周，4-月，5-季度，6-年 */
//    public Integer getJobCycle() {
//
//        return this.jobCycle;
//    }
//
//    /** 任务运行周期;1-秒，2-日，3-周，4-月，5-季度，6-年 */
//    public void setJobCycle(Integer jobCycle) {
//
//        this.jobCycle = jobCycle;
//    }
//
//    /** 任务运行间隔;配合任务运行周期使用 */
//    public Integer getJobPeriod() {
//
//        return this.jobPeriod;
//    }
//
//    /** 任务运行间隔;配合任务运行周期使用 */
//    public void setJobPeriod(Integer jobPeriod) {
//
//        this.jobPeriod = jobPeriod;
//    }
//
//    /** 任务是否可订阅;0-不可订阅1-可订阅 */
//    public Integer getJobCanSubscribe() {
//
//        return jobCanSubscribe;
//    }
//
//    /** 任务是否可订阅;0-不可订阅1-可订阅 */
//    public void setJobCanSubscribe(Integer jobCanSubscribe) {
//
//        this.jobCanSubscribe = jobCanSubscribe;
//    }
//
//    /** 任务归属权限机构 */
//    public String getJobOrgNo() {
//
//        return this.jobOrgNo;
//    }
//
//    /** 任务归属权限机构 */
//    public void setJobOrgNo(String jobOrgNo) {
//
//        this.jobOrgNo = jobOrgNo;
//    }
//
//    /** 任务的依赖列表;存储结构：kettle-job:syncPaasAgencyHierarchyJob,report-job:agentDevelopDetailPushTask */
//    public String getJobDependences() {
//
//        return this.jobDependences;
//    }
//
//    /** 任务的依赖列表;存储结构：kettle-job:syncPaasAgencyHierarchyJob,report-job:agentDevelopDetailPushTask */
//    public void setJobDependences(String jobDependences) {
//
//        this.jobDependences = jobDependences;
//    }
//
//    /** 创建人 */
//    public String getCreateOper() {
//
//        return this.createOper;
//    }
//
//    /** 创建人 */
//    public void setCreateOper(String createOper) {
//
//        this.createOper = createOper;
//    }
//
//    /** 创建时间 */
//    public Date getCreateTime() {
//
//        return this.createTime;
//    }
//
//    /** 创建时间 */
//    public void setCreateTime(Date createTime) {
//
//        this.createTime = createTime;
//    }
//
//    /** 更新人 */
//    public String getUpdateOper() {
//
//        return this.updateOper;
//    }
//
//    /** 更新人 */
//    public void setUpdateOper(String updateOper) {
//
//        this.updateOper = updateOper;
//    }
//
//    /** 更新时间 */
//    public Date getUpdateTime() {
//
//        return this.updateTime;
//    }
//
//    /** 更新时间 */
//    public void setUpdateTime(Date updateTime) {
//
//        this.updateTime = updateTime;
//    }
//
//    public String getJobFirstDate() {
//
//        return jobFirstDate;
//    }
//
//    public void setJobFirstDate(String jobFirstDate) {
//
//        this.jobFirstDate = jobFirstDate;
//    }
//
//    public String getJobFirstTime() {
//
//        return jobFirstTime;
//    }
//
//    public void setJobFirstTime(String jobFirstTime) {
//
//        this.jobFirstTime = jobFirstTime;
//    }
//
//}
