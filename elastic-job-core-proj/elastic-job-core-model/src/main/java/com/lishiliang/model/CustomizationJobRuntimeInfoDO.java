//
// /**
// * @Title: CustomizationJobRuntimeInfo.java
// * @Package:com.lishiliang.kettle.model
// * @desc: TODO
// * @author: lisl
// * @date:2021年3月30日 上午11:09:18
// */
//
//package com.lishiliang.model;
//
//import java.io.Serializable;
//import java.util.Date;
//
// /**
//  * @author lisl
//  * @desc 定制化任务运行时
//  */
// public class CustomizationJobRuntimeInfoDO implements Serializable {
//     /**
//      * @desc
//      */
//     private static final long serialVersionUID = -2431983552379679860L;
//     /** 主键 */
//     private String id ;
//     /** 任务编号 */
//     private String jobCode ;
//     /** 任务名称 */
//     private String jobName ;
//     /** 任务执行日期 */
//     private String jobDate ;
//     /** 任务执行时间 */
//     private String jobTime ;
//     /** 任务执行结果 */
//     private String jobResult ;
//     /** 任务执行状态;0-未执行1-执行中2-执行成功3-执行失败 */
//     private Integer jobRuntimeStatus ;
//     /** 任务推送状态;0-未推送1-推送中2-推送成功3-推送失败 */
//     private Integer jobPushStatus ;
//     /** 任务执行失败原因 */
//     private String jobRuntimeDesc ;
//     /** 任务推送失败原因 */
//     private String jobPushDesc ;
//     /** 备注 */
//     private String remark ;
//     /** 创建人 */
//     private String createOper ;
//     /** 创建时间 */
//     private Date createTime ;
//     /** 更新人 */
//     private String updateOper ;
//     /** 更新时间 */
//     private Date updateTime ;
//
//     /** 主键 */
//     public String getId(){
//         return this.id;
//     }
//     /** 主键 */
//     public void setId(String id){
//         this.id = id;
//     }
//     /** 任务编号 */
//     public String getJobCode(){
//         return this.jobCode;
//     }
//     /** 任务编号 */
//     public void setJobCode(String jobCode){
//         this.jobCode = jobCode;
//     }
//     /** 任务名称 */
//     public String getJobName(){
//         return this.jobName;
//     }
//     /** 任务名称 */
//     public void setJobName(String jobName){
//         this.jobName = jobName;
//     }
//     /** 任务执行日期 */
//     public String getJobDate(){
//         return this.jobDate;
//     }
//     /** 任务执行日期 */
//     public void setJobDate(String jobDate){
//         this.jobDate = jobDate;
//     }
//     /** 任务执行时间 */
//     public String getJobTime(){
//         return this.jobTime;
//     }
//     /** 任务执行时间 */
//     public void setJobTime(String jobTime){
//         this.jobTime = jobTime;
//     }
//     /** 任务执行结果 */
//     public String getJobResult(){
//         return this.jobResult;
//     }
//     /** 任务执行结果 */
//     public void setJobResult(String jobResult){
//         this.jobResult = jobResult;
//     }
//     /** 任务执行状态;0-未执行1-执行中2-执行成功3-执行失败 */
//     public Integer getJobRuntimeStatus(){
//         return this.jobRuntimeStatus;
//     }
//     /** 任务执行状态;0-未执行1-执行中2-执行成功3-执行失败 */
//     public void setJobRuntimeStatus(Integer jobRuntimeStatus){
//         this.jobRuntimeStatus = jobRuntimeStatus;
//     }
//     /** 任务推送状态;0-未推送1-推送中2-推送成功3-推送失败 */
//     public Integer getJobPushStatus(){
//         return this.jobPushStatus;
//     }
//     /** 任务推送状态;0-未推送1-推送中2-推送成功3-推送失败 */
//     public void setJobPushStatus(Integer jobPushStatus){
//         this.jobPushStatus = jobPushStatus;
//     }
//     /** 任务执行失败原因 */
//     public String getJobRuntimeDesc(){
//         return this.jobRuntimeDesc;
//     }
//     /** 任务执行失败原因 */
//     public void setJobRuntimeDesc(String jobRuntimeDesc){
//         this.jobRuntimeDesc = jobRuntimeDesc;
//     }
//     /** 任务推送失败原因 */
//     public String getJobPushDesc(){
//         return this.jobPushDesc;
//     }
//     /** 任务推送失败原因 */
//     public void setJobPushDesc(String jobPushDesc){
//         this.jobPushDesc = jobPushDesc;
//     }
//     /** 备注 */
//     public String getRemark(){
//         return this.remark;
//     }
//     /** 备注 */
//     public void setRemark(String remark){
//         this.remark = remark;
//     }
//     /** 创建人 */
//     public String getCreateOper(){
//         return this.createOper;
//     }
//     /** 创建人 */
//     public void setCreateOper(String createOper){
//         this.createOper = createOper;
//     }
//     /** 创建时间 */
//     public Date getCreateTime(){
//         return this.createTime;
//     }
//     /** 创建时间 */
//     public void setCreateTime(Date createTime){
//         this.createTime = createTime;
//     }
//     /** 更新人 */
//     public String getUpdateOper(){
//         return this.updateOper;
//     }
//     /** 更新人 */
//     public void setUpdateOper(String updateOper){
//         this.updateOper = updateOper;
//     }
//     /** 更新时间 */
//     public Date getUpdateTime(){
//         return this.updateTime;
//     }
//     /** 更新时间 */
//     public void setUpdateTime(Date updateTime){
//         this.updateTime = updateTime;
//     }
// }
