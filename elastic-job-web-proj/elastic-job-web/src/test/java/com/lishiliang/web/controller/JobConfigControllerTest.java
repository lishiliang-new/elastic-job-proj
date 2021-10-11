package com.lishiliang.web.controller;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.lishiliang.facade.JobConfigFacade;
import com.lishiliang.facade.JobManageFacade;
import com.lishiliang.model.JobConfigDO;
import com.lishiliang.model.JobConfigVO;
import com.lishiliang.model.JobDetailVO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * @author lisl
 * @version 1.0
 * @desc :
 */
public class JobConfigControllerTest {

    @InjectMocks
    @Spy
    private JobConfigController jobConfigController;

    @Spy
    private JobConfigFacade jobConfigFacade;
    @Spy
    private JobManageFacade jobManageFacade;

    private JobConfigDO jobConfigDO = new JobConfigDO();

    @Before
    public void before () {
        MockitoAnnotations.initMocks(this);

        jobConfigDO.setProfiles("dev");
        jobConfigDO.setBusinessSource("kettle_job");
        jobConfigDO.setJobCode("mock_test");
        jobConfigDO.setJobParameter("ktrFile=/home/app/nfsdata/radar/ktr/paas/xxcopy.ktr;jobDirection=0");
        jobConfigDO.setCron("0/33 * * * * ?");
        jobConfigDO.setJobName("mock测试名称");
        jobConfigDO.setDescription("mock测试描述");
        jobConfigDO.setJobCanSubscribe(1);
        jobConfigDO.setJobCycle(1);
        jobConfigDO.setJobPeriod(1);
        jobConfigDO.setDeleteFlag(false);

        
        doNothing().when(jobManageFacade).triggerJob(any());
        when(jobManageFacade.uploadKtrFile(any(byte[].class), any(), any())).thenReturn("mockFilePath");
    }


    @Test
    public void testAdd() {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        //request.addFile(new MockMultipartFile("/fileName", "fileBytes".getBytes()));
        StandardMultipartHttpServletRequest httpServletRequest = new StandardMultipartHttpServletRequest(request);
        httpServletRequest.getMultiFileMap().add("file", new MockMultipartFile("/fileName.ktr", "orgFileName.ktr","multipart/form-data", "fileBytes".getBytes()));

        doNothing().when(jobConfigFacade).add(any());
        jobConfigController.add(jobConfigDO, httpServletRequest);
    }

    @Test
    public void testUpdate() {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        StandardMultipartHttpServletRequest httpServletRequest = new StandardMultipartHttpServletRequest(request);
        httpServletRequest.getMultiFileMap().add("file", new MockMultipartFile("/fileName.ktr", "orgFileName.ktr","multipart/form-data", "fileBytes".getBytes()));

        doNothing().when(jobConfigFacade).update(any());
        jobConfigController.update(jobConfigDO, httpServletRequest);
    }

    @Test
    public void testDeleteRadarJobConfig() {
        doNothing().when(jobConfigFacade).delete(any());
        jobConfigController.delete(jobConfigDO);
    }

    @Test
    public void testPageInfo() {

        when(jobConfigFacade.pageInfo(any(), anyInt(), anyInt())).thenReturn(new PageInfo<>(Lists.newArrayList(new JobConfigVO())));
        jobConfigController.pageInfo(jobConfigDO, 1 ,50);
    }

    @Test
    public void testReStartJob() {

        jobConfigController.reStartJob(jobConfigDO);
    }

    @Test
    public void testBatchEnbleOrDisable() {
        doNothing().when(jobConfigFacade).batchEnbleOrDisable(Arrays.asList(1L, 2L), true);
        jobConfigController.batchEnbleOrDisable(Arrays.asList(1L, 2L), true);
    }

    @Test
    public void testGetJobDetail() {
        when(jobManageFacade.getJobDetail(any())).thenReturn(new JobDetailVO());
        jobConfigController.getJobDetail(jobConfigDO);
    }

    @Test
    public void testIndex() throws Exception {
        jobConfigController.index();
    }
}