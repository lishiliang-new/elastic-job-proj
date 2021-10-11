/**
 * 
 */
package com.lishiliang.commons;

/**
 * @author lisl
 * 枚举类
 */
public class Enums {

    /**
     * Kettle任务的状态枚举
     * @author yangcx
     */
    public static enum JobStatus {
        
        NOT_RUN(0), //未执行
        RUNNING(1), //执行中
        SUCCESS(2), //执行成功
        FAIL(3);    //执行失败
        
        int code;
        JobStatus(int code){
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }
    

    


    
    /**
     * 任务的启停
     */
    public static enum JobEnable {
        
        ENABLE(1), //启用
        DISABLE(0); //禁用
        
        int code;
        JobEnable(int code){
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }
    

    
    /**
     * 任务的删除与否
     */
    public static enum JobDeleteFlag {
        
        DELETED(1), //已删除
        NORMAL(0); //未删除
        
        int code;
        JobDeleteFlag(int code){
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }
    
    /**
     * 任务的环境
     */
    public static enum JobProfile  {
        
        DEV("dev"), //dev
        PRE("pre"), //pre
        PROD("prod"), //prod
        TEST("test"); //test
        
        String code;
        JobProfile(String code){
            this.code = code;
        }
        public String getCode() {
            return code;
        }
    }

    /**
     * job业务来源
     * @author lisl
     */
    public static enum JobBusinessSource {

        KETTLE_JOB("kettle_job"), //普通业务job
        REPORT_JOB("report_job"), //普通业务job
        CUSTOMIZATION_JOB("customization_job"), //定制化动态job
        REGIST_KETTLE_JOB("regist_job"); //注册业务job

        String desc;
        JobBusinessSource(String desc){
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

    }
}
