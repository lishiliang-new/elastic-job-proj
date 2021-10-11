package com.lishiliang.facade;



import com.github.pagehelper.PageInfo;
import com.lishiliang.model.JobConfigDO;
import com.lishiliang.model.JobConfigVO;

import java.util.List;


public interface JobConfigFacade {

    /**
     * 添加配置
     * @param jobConfigDO
     */
    void add(JobConfigDO jobConfigDO);

    /**
     * 修改配置
     * @param jobConfigDO
     */
    void update(JobConfigDO jobConfigDO);

    /**
     * 条件分页查询job配置
     * @param jobConfigDO
     * @param page
     * @param limit
     * @return
     */
    PageInfo<JobConfigVO> pageInfo(JobConfigDO jobConfigDO, int page, int limit);

    /**
     * 条件查询所有
     * @param jobConfigDO
     * @return
     */
    List<JobConfigVO> all(JobConfigDO jobConfigDO);

    void delete(JobConfigDO jobConfigDO);

    /**
     * 批量禁用启用
     * @param ids
     * @param enable
     */
    void batchEnbleOrDisable(List<Long> ids, boolean enable);

    /**
     * 环境切换
     * @param jobConfigDO
     */
    void changeEnvironment(JobConfigDO jobConfigDO);

}
