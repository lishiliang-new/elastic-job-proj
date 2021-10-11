package com.lishiliang.facade;


import com.lishiliang.model.JobDetailVO;

public interface JobManageFacade {

    public void triggerJob(String jobCode);

    public String uploadKtrFile(byte[] bytes, String orgFileName, String jobCode);

    JobDetailVO getJobDetail(String jobCode);

    void removeJob(String jobCode);
}
