/**
 * 
 */
package com.lishiliang.commons;

/**
 * 错误码表
 */
public enum ErrorCodes {

    ERR_PARAM("100100000","参数错误","参数错误"),
    
    KETTLE_EXCEPTION("100100001","Kettle脚本执行异常","Kettle脚本执行异常，请检查"),
    
    KETTLE_JOB_UPDATE_EXCEPTION("100100002","Kettle任务更新失败","Kettle任务更新失败，参数不足！"),
    
    KETTLE_JOB_RUN_CURRENT_MONTH_EXCEPTION("100100003","Kettle任务执行失败，任务时间超过当前时间","Kettle任务执行失败，任务时间超过当前时间"),
    
    KETTLE_NO_JOB_EXCEPTION("100100004","Kettle任务执行失败，当前无可执行的Kettle任务","Kettle任务执行失败，当前无可执行的Kettle任务"),
    
    KETTLE_DEPENDENT_JOB_EXCEPTION("100100005","Kettle任务执行失败，当前依赖任务尚未执行完成","Kettle任务执行失败，当前依赖任务尚未执行完成"),
    
    KETTLE_JOB_CONFIG_NOT_FOUND_EXCEPTION("100100006","Kettle任务配置信息缺失","Kettle任务配置信息缺失，参数不足！"),
    
    KETTLE_JOB_CONFIG_PARSE_EXCEPTION("100100007","Kettle任务的依赖任务配置信息解析失败","Kettle任务的依赖任务配置信息解析失败！"),
    
    KETTLE_JOB_CONFIG_CYCLE_EXCEPTION("100100008","暂不支持当前任务配置的运行周期","暂不支持当前任务配置的运行周期"),
    
    KETTLE_JOB_RESULT_EXCEPTION("100100009","任务结果文件处理失败","任务结果文件处理失败"),
    
    KETTLE_JOB_RUNNING_EXCEPTION("100100010","Kettle任务执行失败，当前任务执行中，请等待","Kettle任务执行失败，当前任务执行中，请等待"),
    
    KETTLE_DECRYPT_PASSWORD_EXCEPTION("100100011","Kettle脚本执行异常，密码解密失败！","Kettle脚本执行异常，密码解密失败！"),
    ;
    
    String code;
    String desc;
    String outDesc;
    
    ErrorCodes(String code,String desc,String outDesc){
        this.code = code;
        this.desc = desc;
        this.outDesc = outDesc;
    }
    public String getCode() {
    
        return code;
    }
    public String getDesc() {
    
        return desc;
    }
    public String getOutDesc() {
    
        return outDesc;
    }
}
