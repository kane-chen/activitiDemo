/*
 * @(#)ProcessEcontServiceChargeTest.java	2013年12月3日
 *
 * @Company <Opportune Technology Development Company LTD.>
 */
package com.lasho.activiti.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lasho.activiti.service.WorkFlowService;


/**
 * @version 1.0
 * @Author  chenqx
 * @Date    2013年12月3日
 * @description 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:workflow.xml")
public class ProcessTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTest.class) ;

    @Autowired
    private WorkFlowService workFlowService;
    
    final String filePath = "workflow-demo.bpmn" ; 
    final String workflowKey = "myProcess" ;
    //测试参数
    Integer userId = 1003814 ;
    String businessId = "11112" ;
    String perfixUrl = "http://qa-n.lashou.com/crm" ;

    @Before
    public void setUp(){
    }
    
    @Test
    public void testDeploy() {
        int r = workFlowService.deployWorkflowTemplate(workflowKey, filePath);
        Assert.assertEquals(1, r);
    }
   
    @Test
    public void startWorkflow(){
        Map<String,Object> map = new HashMap<String,Object>() ;
        try {
        	workFlowService.startWorkFlow(businessId, map, workflowKey);
        } catch (Exception e) {
            LOGGER.error("start econt-service-error",e);
        }
    }
   
    @Test
    public void salerCommit(){
        Map<String,String> map = new HashMap<String,String>() ;
        String taskKey = "starter";
        try {
			workFlowService.auditWorkFlow(businessId, map, workflowKey, taskKey );
        } catch (Exception e) {
            LOGGER.error("commit econt-service-error",e);
        }
    }
    
    @Test
    public void auditorConfirm(){
    	Map<String,String> param = new HashMap<String,String>() ;
    	String taskKey = "auditor" ;
    	workFlowService.auditWorkFlow(businessId, param, workflowKey, taskKey) ;
    }
    
}
