package com.lasho.activiti.service;

import java.util.Map;

public interface WorkFlowService {

	/**
	 * deploy workflow-template
	 * @param processKey
	 * @param templatePath
	 * @return
	 */
	int deployWorkflowTemplate(String processKey ,String templatePath) ;

	/**
	 * start workflow
	 * @param bussinessKey
	 * @param params
	 * @param processKey
	 * @return
	 */
	String startWorkFlow(String bussinessKey,Map<String,Object> params,String processKey);
	
	/**
	 * audit workflow
	 * @param bussinessKey
	 * @param params
	 * @param processKey
	 * @param taskKey
	 * @return
	 */
	int auditWorkFlow(String bussinessKey,Map<String,String> params,String processKey,String taskKey);
}
