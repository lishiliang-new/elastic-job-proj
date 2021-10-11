package com.lishiliang.model;

/**
     * job详情状态
     */
public  enum JobStatus {

    OK("OK", "正常"),
    CRASHED("CRASHED", "已下线"),
    DISABLED("DISABLED", "已失效"),
    SHARDING_FLAG("SHARDING_FLAG", "分片待调整");

    public String code;
    public String desc;

    JobStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}