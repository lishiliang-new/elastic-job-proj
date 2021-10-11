package com.lishiliang.commons;

/**
 * @author lisl
 * @version 1.0
 * @desc : 常量
 */
public class Constant {

    /**
     * kettle_job上传文件后缀
     */
    public static final String KETTLE_JOB_FILE_SUFFIX = "ktr";
    /**
     * job参数 拼接符
     */
    public static final String JOB_PARAMETER_CONCATENATION = ";";
    /**
     * kettle_job参数 ktrFile ktr脚本路径
     */
    public static final String JOB_PARAMETER_KTR_FILE = "ktrFile";
    /**
     * kettle_job参数 kettleLogLevel日志输出级别
     */
    public static final String KETTLE_LOG_LEVEL = "kettleLogLevel";
    /**
     * kettle_job参数 jobDirection 运行方向
     */
    public static final String JOB_PARAMETER_KTR_JOB_DIRECTION = "jobDirection";
    /**
     * kettle_job注册任务code
     */
    public static final String REGIST_JOB = "registJob";
    
    /**
     * 依赖任务ALL：依赖所有的任务（除自己外）
     */
    public static final String ALL_DEPENDENCE_JOB = "ALL";
    
    /**
     * 应用的公钥key
     */
    public static final String DATASOURCE_DRUID_PUBLIC_KEY = "spring.datasource.druid.public-key";
}
