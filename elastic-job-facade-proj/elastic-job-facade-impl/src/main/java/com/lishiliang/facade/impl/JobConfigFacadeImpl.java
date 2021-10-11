package com.lishiliang.facade.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.lishiliang.model.Enums;
import com.lishiliang.model.ErrorCode;
import com.lishiliang.model.JobConfigDO;
import com.lishiliang.model.JobConfigVO;
import com.lishiliang.core.exception.BusinessRuntimeException;
import com.lishiliang.core.utils.AssertUtils;
import com.lishiliang.core.utils.Constant;
import com.lishiliang.core.utils.ErrorCodes;
import com.lishiliang.dao.JobConfigDao;
import com.lishiliang.facade.JobConfigFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.List;


@Service(timeout=60000, filter="-exception")
public class JobConfigFacadeImpl implements JobConfigFacade {

    private static final Logger logger = LoggerFactory.getLogger(JobConfigFacadeImpl.class);

    @Autowired
    private JobConfigDao jobConfigDao;

    @Autowired
    private Environment env;

    @Override
    public void add(JobConfigDO jobConfigDO) {
        logger.info("添加任务配置 {} " , JSON.toJSONString(jobConfigDO));

        //不是普通kettle类型任务 需要判断是否已经存在
        if (!Enums.JobBusinessSource.KETTLE_JOB.getCode().equalsIgnoreCase(jobConfigDO.getBusinessSource())) {
            JobConfigDO condition = new JobConfigDO();
            condition.setBusinessSource(jobConfigDO.getBusinessSource());
            condition.setProfiles(env.getActiveProfiles()[0]);
            List<JobConfigVO> jobConfigVOS = jobConfigDao.all(condition);
            if (CollectionUtils.isNotEmpty(jobConfigVOS)) {
                throw new BusinessRuntimeException(ErrorCode.CODE_1011.getErrCode(), "该类型任务已经存在请在原任务上修改");
            }

        } else {
            //处理前端传入&问题
            //jobConfigDO.setJobParameter(HtmlUtils.htmlUnescape(jobConfigDO.getJobParameter()));
            jobConfigDO.setProfiles(env.getActiveProfiles()[0]);

            processDateTime(jobConfigDO);

            jobConfigDao.add(jobConfigDO);
        }
    }

    /**
     * 处理job首次执行时间
     * @param jobConfigDO
     */
    private void processDateTime(JobConfigDO jobConfigDO) {

        DateTime dateTime = DateTime.now().withTime(00,00,00, 00);
        if (StringUtils.isNotBlank(jobConfigDO.getJobFirstDate())) {
            //补齐时间8位长度
            String date = StringUtils.rightPad(jobConfigDO.getJobFirstDate(), 8, "01");
            String time = StringUtils.defaultString(jobConfigDO.getJobFirstTime(), "000000");
            dateTime = DateTime.parse(String.format("%s%s", date, time), DateTimeFormat.forPattern(Constant.DATE_TIME_YYYYMMDDHHMMSS));
        }

//        switch (getByCode(jobConfigDO.getJobCycle())) {
//            case DAILY: //日任务
//                jobConfigDO.setJobFirstDate(dateTime.toString(Constant.DATE_TIME_YYYYMMDD));
//                break;
//            case MONTHLY: //月任务
//                jobConfigDO.setJobFirstDate(dateTime.toString(Constant.DATE_TIME_YYYYMM));
//                break;
//            case YEARLY: //年任务
//                jobConfigDO.setJobFirstDate(dateTime.toString(Constant.DATE_TIME_YYYY));
//                break;
//            default:
//                break;
//        }
        jobConfigDO.setJobFirstDate(dateTime.toString(Constant.DATE_TIME_YYYYMMDD));
        jobConfigDO.setJobFirstTime(dateTime.toString(Constant.DATE_TIME_HHMMSS));
    }

    @Override
    public void update(JobConfigDO jobConfigDO) {
        logger.info("修改任务配置 {} " , JSON.toJSONString(jobConfigDO));

        AssertUtils.isNotNull(jobConfigDO.getId(), ErrorCodes.ERR_PARAM.getCode(), ErrorCodes.ERR_PARAM.getDesc());

        //处理前端传入&问题
        //jobConfigDO.setJobParameter(HtmlUtils.htmlUnescape(jobConfigDO.getJobParameter()));
        jobConfigDO.setUpdateTime(DateTime.now().toString(Constant.DATE_TIME_YYYY_MM_DD_HH_MM_SS));

        processDateTime(jobConfigDO);

        jobConfigDao.update(jobConfigDO);
    }

    @Override
    public PageInfo<JobConfigVO> pageInfo(JobConfigDO jobConfigDO, int page, int limit) {
        logger.info("分页查询任务配置 {} 页码 {} 行数 {} " , JSON.toJSONString(jobConfigDO), page, limit);

        jobConfigDO.setProfiles(env.getActiveProfiles()[0]);
        return jobConfigDao.pageInfo(jobConfigDO, page, limit);
    }

    @Override
    public List<JobConfigVO> all(JobConfigDO jobConfigDO) {
        logger.info("查询任务配置 {}  " , JSON.toJSONString(jobConfigDO));

        jobConfigDO.setProfiles(env.getActiveProfiles()[0]);
        return jobConfigDao.all(jobConfigDO);
    }

    @Override
    public void delete(JobConfigDO jobConfigDO) {
        logger.info("删除任务配置 {}  " , JSON.toJSONString(jobConfigDO));

        jobConfigDao.delete(jobConfigDO);
    }

    @Override
    public void batchEnbleOrDisable(List<Long> ids, boolean enable) {
        logger.info("批量{}任务 {}" , enable ? "启用" : "停用", JSON.toJSONString(ids));

        jobConfigDao.batchEnbleOrDisable(ids, enable);
    }

    @Override
    public void changeEnvironment(JobConfigDO jobConfigDO) {
        //获取需要切换到的目标环境
        Enums.JobProfile canChangeEnv = Enums.JobProfile.getByCode(jobConfigDO.getProfiles()).getCanChangeEnv();
        AssertUtils.isNotNull(canChangeEnv, ErrorCode.CODE_1011.getErrCode(), "当前环境" + jobConfigDO.getProfiles() + " 没有可切换的目标环境");

        //查询目标环境是否存在相同的任务
        JobConfigDO condition = new JobConfigDO();
        condition.setBusinessSource(jobConfigDO.getBusinessSource());
        condition.setJobCode(jobConfigDO.getJobCode());
        condition.setProfiles(canChangeEnv.getCode());
        List<JobConfigVO> jobConfigVOS = jobConfigDao.all(condition);
        AssertUtils.isTrue(CollectionUtils.isEmpty(jobConfigVOS), ErrorCode.CODE_1011.getErrCode(), "该任务已存在目标环境中,请在原任务上修改");

        //修改环境
        jobConfigDO.setProfiles(canChangeEnv.getCode());
        this.update(jobConfigDO);
    }


}
