///**
// *
// */
//package com.lishiliang.model;
//
//import java.io.Serializable;
//
///**
// * @author yangcx
// *
// */
//public class KettleJobDO implements Serializable {
//
//    private static final long serialVersionUID = 7079364594884995056L;
//    private String id;
//    /**
//     * 任务编码
//     */
//    private String kettleJobCode;
//    /**
//     * 任务名称
//     */
//    private String kettleJobName;
//    /**
//     * 当前任务执行的年月
//     */
//    private String currentMonth;
//    /**
//     * 当前任务执行的日（按日抽取数据时使用
//     */
//    private String currentMonthDate;
//    /**
//     * 当前任务状态0-未执行1-执行中2-执行成功3-执行失败
//     */
//    private Integer jobStatus;
//    /**
//     * 依赖任务编号，多个时，以英文逗号分开
//     */
//    private String dependentJobCode;
//    /**
//     * 任务方向0-向前(倒推)1-向后(顺推)
//     */
//    private Integer jobDirection;
//    /**
//     * 备注
//     */
//    private String remark;
//
//    private String updateTime;
//
//    public String getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(String updateTime) {
//        this.updateTime = updateTime;
//    }
//
//    public String getId() {
//        return id;
//    }
//    public void setId(String id) {
//        this.id = id;
//    }
//    public String getKettleJobName() {
//        return kettleJobName;
//    }
//    public void setKettleJobName(String kettleJobName) {
//        this.kettleJobName = kettleJobName;
//    }
//    public String getKettleJobCode() {
//        return kettleJobCode;
//    }
//    public void setKettleJobCode(String kettleJobCode) {
//        this.kettleJobCode = kettleJobCode;
//    }
//    public String getCurrentMonth() {
//        return currentMonth;
//    }
//    public void setCurrentMonth(String currentMonth) {
//        this.currentMonth = currentMonth;
//    }
//    public String getCurrentMonthDate() {
//        return currentMonthDate;
//    }
//    public void setCurrentMonthDate(String currentMonthDate) {
//        this.currentMonthDate = currentMonthDate;
//    }
//    public Integer getJobStatus() {
//        return jobStatus;
//    }
//    public void setJobStatus(Integer jobStatus) {
//        this.jobStatus = jobStatus;
//    }
//    public Integer getJobDirection() {
//        return jobDirection;
//    }
//    public void setJobDirection(Integer jobDirection) {
//        this.jobDirection = jobDirection;
//    }
//    public String getRemark() {
//        return remark;
//    }
//    public void setRemark(String remark) {
//        this.remark = remark;
//    }
//	public String getDependentJobCode() {
//		return dependentJobCode;
//	}
//	public void setDependentJobCode(String dependentJobCode) {
//		this.dependentJobCode = dependentJobCode;
//	}
//}
