package com.lishiliang.web.controller;


import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.lishiliang.core.exception.BusinessRuntimeException;
import com.lishiliang.core.model.DataModel;
import com.lishiliang.core.utils.*;
import com.lishiliang.facade.JobConfigFacade;
import com.lishiliang.facade.JobManageFacade;
import com.lishiliang.model.*;
import com.lishiliang.model.Constant;
import com.lishiliang.model.Enums;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author lisl
 * @version 1.0
 * @desc : 任务基础配置管理
 */
@Controller
@RequestMapping("/job/config")
public class JobConfigController {

    private static final Logger logger = LoggerFactory.getLogger(JobConfigController.class);


    @Reference
    private JobConfigFacade jobConfigFacade;

    @Reference(retries = 0)
    private JobManageFacade JobManageFacade;


    /**
     * @desc 任务基础配置管理首页
     */
    @RequestMapping(value = "/index", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index() {

        ModelAndView view = new ModelAndView();
        view.setViewName("job/config/jobConfigManage");
        return view;
    }

    /**
     * @desc
     */
    @RequestMapping(value = "/orgList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public DataModel orgList() {
        Map<String, Object> org1 = new HashMap();
        org1.put("id", 1L);
        org1.put("name", "机构1");
        Map<Object, Object> org2 = new HashMap();
        org2.put("id", 2L);
        org2.put("name", "机构2");
        return BuildUtils.bulidDataModel(Lists.newArrayList(org1, org2));
    }


    /**
     * @return
     * @desc: 添加任务配置
     */
    @RequestMapping(value = "/add",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public DataModel add(JobConfigDO jobConfigDO, HttpServletRequest request) {

        //参数校验
        checkParams(jobConfigDO);

        processRequest(jobConfigDO, request);

        DataModel data = new DataModel();
        jobConfigFacade.add(jobConfigDO);

        data.setMsg("操作完成");
        return data;
    }

    /**
     * @return
     * @desc: 修改任务配置
     */
    @RequestMapping(value = "/update",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public DataModel update(JobConfigDO jobConfigDO, HttpServletRequest request) {

        DataModel data = new DataModel();

        //参数校验
        checkParams(jobConfigDO);

        processRequest(jobConfigDO, request);
        if (jobConfigDO.getDeleteFlag()) {
            //删除zk配置信息
            JobManageFacade.removeJob(jobConfigDO.getJobCode());
        }

        jobConfigFacade.update(jobConfigDO);

        data.setMsg("操作完成");
        return data;
    }

    /**
     * @return
     * @desc: 删除任务配置
     */
    @RequestMapping(value = "/delete",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public DataModel delete(JobConfigDO jobConfigDO) {

        DataModel data = new DataModel();
        jobConfigFacade.delete(jobConfigDO);

        data.setMsg("操作完成");
        return data;
    }

    /**
     * @return
     * @desc: 查询任务配置信息
     */
    @RequestMapping(value = "/pageInfo",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public DataModel pageInfo(JobConfigDO jobConfigDO, int page, int limit) {

        DataModel data = new DataModel();
        PageInfo<JobConfigVO> pageInfo = jobConfigFacade.pageInfo(jobConfigDO, page, limit);
        data.setCount(pageInfo.getTotal());
        data.setData(pageInfo.getList());
        data.setMsg("操作完成");
        return data;
    }


    /**
     * @return
     * @desc: 重新执行任务
     */
    @RequestMapping(value = "/triggerJob",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public DataModel reStartJob(JobConfigDO jobConfigDO) {

        DataModel data = new DataModel();
        if (jobConfigDO.getBusinessSource().endsWith(Enums.JobBusinessSource.KETTLE_JOB.getCode())) {
            JobManageFacade.triggerJob(jobConfigDO.getJobCode());
        }
        data.setMsg("操作完成");
        return data;
    }

    /**
     * @return
     * @desc: 批量启用停用
     */
    @RequestMapping(value = "/batchEnbleOrDisable",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public DataModel batchEnbleOrDisable(@RequestParam("ids") List<Long> ids, @RequestParam("enable") boolean enable) {

        DataModel data = new DataModel();
        jobConfigFacade.batchEnbleOrDisable(ids, enable);

        data.setMsg("操作完成");
        return data;
    }

    /**
     * @return
     * @desc: 获取job详情
     */
    @RequestMapping(value = "/getJobDetail",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public DataModel getJobDetail(JobConfigDO jobConfigDO) {

        DataModel data = new DataModel();

        if (jobConfigDO.getBusinessSource().endsWith(Enums.JobBusinessSource.KETTLE_JOB.getCode())) {
            JobDetailVO jobDetailVO = JobManageFacade.getJobDetail(jobConfigDO.getJobCode());
            data.setData(jobDetailVO);
        }

        data.setMsg("操作完成");
        return data;
    }

    /**
     * @return
     * @desc: 切换环境
     */
    @RequestMapping(value = "/change/environment",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public DataModel changeEnvironment(JobConfigDO jobConfigDO) {

        DataModel data = new DataModel();

        jobConfigFacade.changeEnvironment(jobConfigDO);

        data.setMsg("操作完成");
        return data;
    }

    /**
     * @return
     * @desc: 下载ktr文件
     */
    @RequestMapping(value = "/download",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<byte[]> download(final String filePath) {


        // 从共享目录上获取文件
        try {

            AssertUtils.isNotBlank(filePath);
            File localFile = new File(filePath);

            HttpHeaders headers = new HttpHeaders();

            String fileName = URLEncoder.encode(localFile.getName(), "utf-8");
            headers.add("Content-Disposition", "attchement;filename=" + fileName + "");
            // 设置请求状态
            HttpStatus statusCode = HttpStatus.OK;
            return new ResponseEntity<>(FileUtils.readFileToByteArray(localFile), headers, statusCode);

        } catch (BusinessRuntimeException e) {

            logger.error("ktr文件下载失败：" + e.getMessage(), e);
            throw e;

        } catch (Exception e) {

            logger.error("ktr文件下载失败：" + e.getMessage(), e);
            throw new RuntimeException("ktr文件下载失败");
        }

    }

    //处理文件上传请求
    private void processRequest(JobConfigDO jobConfigDO, HttpServletRequest request) {

        if (request instanceof StandardMultipartHttpServletRequest) {
            //获取上传文件对象
            MultipartFile multipartFile = ((StandardMultipartHttpServletRequest) request).getMultiFileMap().values().stream().flatMap(Collection::stream).findFirst().orElse(null);

            AssertUtils.isTrue(multipartFile != null && multipartFile.getSize() > 0, ErrorCode.CODE_1011.getErrCode(), "上传文件不能为空");
            AssertUtils.isTrue(Enums.JobBusinessSource.KETTLE_JOB.getCode().equals(jobConfigDO.getBusinessSource()) && multipartFile.getOriginalFilename().endsWith(Constant.JOB_FILE_SUFFIX), ErrorCode.CODE_1011.getErrCode(), "请上传正确的文件");

            // 文件上传
            byte[] bytes = CatchUtils.Receive.catchException(MultipartFile::getBytes, multipartFile);
            final String filePath = JobManageFacade.uploadKtrFile(bytes, multipartFile.getOriginalFilename(), jobConfigDO.getJobCode());
            AssertUtils.isNotBlank(filePath, ErrorCodes.ERR_PARAM.getCode(), "文件路径不能为空！");

            //组装jobParameter参数
            String orgJobParameter = jobConfigDO.getJobParameter();
            if (orgJobParameter.contains(Constant.JOB_PARAMETER_KTR_FILE + "=")) {
                orgJobParameter = orgJobParameter.split(Constant.JOB_PARAMETER_CONCATENATION)[1];
            }
            jobConfigDO.setJobParameter(Constant.JOB_PARAMETER_KTR_FILE + "=" + filePath + Constant.JOB_PARAMETER_CONCATENATION + orgJobParameter);
        }
    }

    /**
     * 参数校验
     * @param configDO
     */
    private void checkParams(JobConfigDO configDO) {

        AssertUtils.isTrue(!StringUtils.isAnyBlank(configDO.getJobCode(), configDO.getCron(), configDO.getBusinessSource(),
                configDO.getJobParameter(), configDO.getJobName()), ErrorCode.CODE_1011.getErrCode(),
                "参数: [任务编码,cron表达式,业务来源,job参数,任务名称]任何一个不能为空");
        AssertUtils.isNotNull(configDO.getJobCycle(), ErrorCodes.ERR_PARAM.getCode(), "参数: [jobCycle]不能为空");
        AssertUtils.isNotNull(configDO.getJobPeriod(), ErrorCodes.ERR_PARAM.getCode(), "参数: [jobPeriod]不能为空");
        AssertUtils.isNotNull(configDO.getJobCanSubscribe(), ErrorCodes.ERR_PARAM.getCode(), "参数: [jobCanSubscribe]不能为空");
        //校验cron表达式
        AssertUtils.isTrue(Regex.matchRegex(Regex.CRON, configDO.getCron()), ErrorCode.CODE_1011.getErrCode(), "请输入正确的cron表达式");

    }


}
