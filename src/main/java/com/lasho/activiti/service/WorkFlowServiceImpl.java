package com.lasho.activiti.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WorkFlowServiceImpl.class);
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private FormService formService ;

	public int deployWorkflowTemplate(String processkey, String fileName) {
		if (StringUtils.isBlank(processkey) || StringUtils.isBlank(fileName)) {
			return 0;
		}
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		try {
			Resource resource = resourceLoader.getResource(fileName);
			InputStream inputStream = resource.getInputStream();
			if (fileName.endsWith(".bpmn")) {// use bpmn to deploy
				repositoryService
						.createDeployment()
						.addInputStream(processkey + ".bpmn20.xml", inputStream)
						.deploy();
			} else if (fileName.endsWith(".bar")) {// using bar to deploy
				LOGGER.debug("read workflow from: {}", fileName);
				if (inputStream == null) {
					LOGGER.warn("ignore deploy workflow module: {}", fileName);
				} else {
					LOGGER.debug("finded workflow module: {}, deploy it!",
							fileName);
					ZipInputStream zis = new ZipInputStream(inputStream);
					repositoryService.createDeployment().addZipInputStream(zis)
							.deploy();
				}
			}
			return 1;
		} catch (FileNotFoundException e) {
			LOGGER.error("deploy failed:=====>can not find file:" + fileName);
			return 0;
		} catch (IOException e) {
			LOGGER.error("deploy failed:=====>" + e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}

	public String startWorkFlow(String bussinessKey,
			Map<String, Object> params, String processKey) {
		String procInstId = null;
		ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKey(processKey, bussinessKey, params);
		if (null != processInstance) {
			procInstId = processInstance.getId();
		}
		return procInstId;
	}

	public int auditWorkFlow(String bussinessKey, Map<String, String> params,
			String processKey, String taskKey) {
		Task currentTask = this.getSpecialTask(bussinessKey, processKey, taskKey) ;
		if(null!=currentTask){
			formService.submitTaskFormData(currentTask.getId(), params) ;
		}
		return 0;
	}

	/**
	 * 获取任务
	 * @param bussinessId 业务ID
	 * @param procDefKey 模版Key
	 * @param taskDefKey taskKey
	 * @return
	 */
	public Task getSpecialTask(String bussinessId, String procDefKey,
			String taskDefKey) {
		Task result = null ;
		TaskQuery query = taskService.createTaskQuery();
		if (StringUtils.isNotBlank(bussinessId)) {
			query.processInstanceBusinessKey(bussinessId);
		}
		if (StringUtils.isNotBlank(procDefKey)) {
			query.processDefinitionKey(procDefKey);
		}
		if (StringUtils.isNotBlank(taskDefKey)) {
			query.taskDefinitionKey(taskDefKey);
		}
		List<Task> tasks = query.list();
		if(null!=tasks && !tasks.isEmpty()){
			result = tasks.get(0) ;
		}
		return result ;
	}

}
