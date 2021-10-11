
 /**
 * @Title: Enums.java 
 * @Package:com.lishiliang.radar.common
 * @desc: TODO  
 * @author: lisl    
 * @date:2019年8月21日 下午1:26:03    
 */

 package com.lishiliang.model;


public class Enums {

    /**
     * @author lisl
     * @desc 地区类型
     */
    public static enum AreaType {
        COUNTRR(1), //国家
        PROVINCE(2), //省,直辖市,自治区
        CITY(3), //市
        AREA(4); //区县

        int code;
        AreaType(int code){
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }

    /**
     * @author lisl
     * @desc 删除标志
     */
    public static enum ActiveFlag {
        DELETE(0), //0-删除
        NORMAL(1); //1-正常

        int code;
        ActiveFlag(int code){
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }


    /**
     * job类型枚举
     * @author lisl
     */
    public static enum JobBusinessSource {

        KETTLE_JOB("kettle_job", "kettle任务"),

        REGIST_JOB("regist_job", "注册任务") {
            @Override
            public String getRegistJobCode() {
                return "registJobTask";
            }
        },
        REPORT_JOB("report_job", "report任务"),
        ;

        String code;
        String desc;

        JobBusinessSource(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        /**
         * 是否是注册任务
         * @param code
         * @return
         */
        public static boolean isRegistBusinessSource(String code) {
            if (code == null || "".equals(code)) {
                return false;
            }

            return REGIST_JOB.code.equalsIgnoreCase(code);
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        //获取注册任务的 jobCode
        public String getRegistJobCode() {
            return "";
        }
    }

    /**
     * 任务的执行周期
     * 1-秒，2-日，3-周，4-月，5-季度，6-年
     * @author lisl
     */
    public static enum JobCycleStatus {

        SEC(1), //秒
        DAILY(2), //日
        WEEKLY(3), //周
        MONTHLY(4), //月
        QUARTERLY(5), //季度
        YEARLY(6), //年
        OTHER(-1),
        ;

        int code;
        JobCycleStatus(int code){
            this.code = code;
        }
        public int getCode() {
            return code;
        }
        public static JobCycleStatus getByCode(int code) {
            for(JobCycleStatus status : values()) {
                if(status.getCode() == code) {
                    return status;
                }
            }
            return OTHER;
        }
    }


    /**
     * 任务订阅状态
     * @author lisl
     */
    public static enum JobSubscribeStatus {

        ENABLE(1), //订阅
        CANCEL(0); //取消

        int code;
        JobSubscribeStatus(int code){
            this.code = code;
        }
        public int getCode() {
            return code;
        }
    }

    public static enum JobProfile {
        //dev
        DEV("dev"),
        //pre
        PRE("pre"){
            //预发布环境可切换为生产
            @Override
            public JobProfile getCanChangeEnv() {
                return JobProfile.PROD;
            }
        },
        //prod
        PROD("prod"){
            //生成环境可切换为预发布
            @Override
            public JobProfile getCanChangeEnv() {
                return JobProfile.PRE;
            }
        },
        //test
        TEST("test");

        String code;
        JobProfile(String code){
            this.code = code;
        }
        public String getCode() {
            return code;
        }

        //获取可切换的环境
        public JobProfile getCanChangeEnv() {
            return null;
        };

        public static JobProfile getByCode(String code) {
            for (JobProfile profile : JobProfile.values()) {
                if (profile.getCode().equals(code)) {
                    return profile;
                }
            }
            return null;
        }
    }
}
