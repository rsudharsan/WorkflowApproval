package com.sudhr.approval.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sudhr.approval.pojo.Approval;
import com.sudhr.approval.pojo.Level;
import com.sudhr.approval.pojo.Task;
import com.sudhr.approval.pojo.Workflow;
import com.sudhr.approval.repository.LevelsRepository;
import com.sudhr.approval.repository.TasksRepository;
import com.sudhr.approval.repository.WorkflowRepository;

import util.FlowConstants;
import util.LevelTypes;

@Component
public class WorkflowHelper {

	@Autowired
	TasksRepository tasks;

	@Autowired
	LevelsRepository levels;

	@Autowired
	WorkflowRepository workflow;

	public void initialize(Workflow flow, int level) {
		// initialize current level for flow by generating tasks for user
		flow.setCurrLevel(level);
		Optional<Level> currLevelData = levels.findById(flow.getLevels().get(level).getId());
		if (currLevelData.isPresent()) {
			Level currLevel = currLevelData.get();
			boolean isSeq = false;
			// if level type is seq set visible true for first user task only
			if (currLevel.getType().equals(LevelTypes.SEQUENTIAL))
				isSeq = true;
			int order = 0;
			Task prevTask = null;
			for (Approval user : currLevel.getApprovals()) {
				Task currTask = new Task();
				currTask.setUserId(user.getUser().getId());
				currTask.setWorkflowId(flow.getId());
				if (isSeq) {
					if (order == 0)
						currTask.setVisible(true);
				} else {
					currTask.setVisible(true);
				}
				currTask.setOrder(order);
				order = order + 1;
				tasks.save(currTask);
				// persist link to next task
				if (prevTask != null) {
					prevTask.setNextTaskId(currTask.getId());
					tasks.save(prevTask);
				}
					
				prevTask = currTask;

			}

		}

	}
	
	public List<Task> getTaskListForWorkflow(String id){
		
		List<Task> taskList = new ArrayList<Task>();
		tasks.findAll().forEach(taskList::add);
		List<Task> tasksfList = taskList.stream().filter(t -> t.getWorkflowId().equals(id))
				.collect(Collectors.toList());
		return tasksfList;
		
	}

	public void performAction(String taskid, String action) {
		// TODO: log action
		// find task
		Optional<Task> currTaskData = tasks.findById(taskid);
		if (currTaskData.isPresent()) {
			Task currTask = currTaskData.get();
			currTask.setUserAction(action);
			tasks.save(currTask);
			Optional<Workflow> workflowData = workflow.findById(currTask.getWorkflowId());
			if (workflowData.isPresent()) {
				Workflow currFlow = workflowData.get();
				
				if (action.equals(FlowConstants.REJ)) {
					//if any action is rejected open workflow and mark rejected
					currFlow.setStatus(FlowConstants.REJ);
					workflow.save(currFlow);
					// flag all tasks for the workflow as not visible
					List<Task> tasksfList = getTaskListForWorkflow(currTask.getWorkflowId());
					for (Task looptask : tasksfList) {
						looptask.setVisible(false);
						tasks.save(looptask);
					}
				} else {
					// get current level
					int currlevel = currFlow.getCurrLevel();
					Optional<Level> currLevelData = levels.findById(currFlow.getLevels().get(currlevel).getId());
					if (currLevelData.isPresent()) {
						Level currLevel = currLevelData.get();
						// if seq, set flag for next task
						if (currLevel.getType().equals(LevelTypes.SEQUENTIAL)) {
							Optional<Task> nextTaskData = tasks.findById(currTask.getNextTaskId());
							if (nextTaskData.isPresent()) {
								Task nextTask = nextTaskData.get();
								nextTask.setVisible(true);
								tasks.save(nextTask);
							}
						}
						 
						//if any one approval 
						if (currLevel.getType().equals(LevelTypes.ANY) && action.equals(FlowConstants.APPR)) {
							currFlow.setStatus(FlowConstants.APPR);
							workflow.save(currFlow);
							
							// flag other tasks for the workflow as not visible
							List<Task> tasksfList = getTaskListForWorkflow(currTask.getWorkflowId());
							for (Task looptask : tasksfList) {
								looptask.setVisible(false);
								looptask.setUserAction(action);
								tasks.save(looptask);
							}
							
						}
						List<Task> tasksfList = getTaskListForWorkflow(currTask.getWorkflowId());
						boolean allTasksApproved = true;
						// if any/all tasks complete in a particular level -> initialize flow to next level
						for (Task looptask : tasksfList) {
							if(looptask.getUserAction()==null || !looptask.getUserAction().equals(FlowConstants.APPR))allTasksApproved=false;
						}
						// all approved and no further levels
						if(allTasksApproved && currFlow.getCurrLevel()== currFlow.getLevels().size()) {
							currFlow.setStatus(FlowConstants.APPR);
							workflow.save(currFlow);
							
							// flag other tasks for the workflow as not visible
							 tasksfList = getTaskListForWorkflow(currTask.getWorkflowId());
							for (Task looptask : tasksfList) {
								looptask.setVisible(false);
								looptask.setUserAction(action);
								tasks.save(looptask);
							}
							
							
							
						}// all tasks in current level approved -> initialize tasks for next level
						else if (allTasksApproved && currFlow.getCurrLevel() < currFlow.getLevels().size()) {
							initialize(currFlow, currFlow.getCurrLevel()+1);
							
						}

					}
				}

			}

		}

	}
}
