package com.sudhr.approval.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sudhr.approval.pojo.Level;
import com.sudhr.approval.pojo.Task;
import com.sudhr.approval.pojo.User;
import com.sudhr.approval.pojo.Workflow;
import com.sudhr.approval.repository.LevelsRepository;
import com.sudhr.approval.repository.TasksRepository;
import com.sudhr.approval.repository.UsersRepository;
import com.sudhr.approval.repository.WorkflowRepository;

import util.FlowConstants;

@RestController
@RequestMapping("/api")
public class AppController {
	
	@Autowired
	UsersRepository users;
	@Autowired
	TasksRepository tasks;
	@Autowired
	WorkflowRepository workflow;
	@Autowired
	LevelsRepository levels;
	@Autowired
	WorkflowHelper helper;
	
	
	 @GetMapping("/users")
	  public ResponseEntity<List<User>> getAllUsers() {
		
		 List<User> userList = new ArrayList<User>();
		      users.findAll().forEach(userList::add);
		      return new ResponseEntity<>(userList, HttpStatus.OK);	  }

	
	 @PostMapping("/levels")
	  public ResponseEntity<Level> createLevel(@RequestBody Level level) {
		 try {
			    Level _level = levels.save(level);
			    return new ResponseEntity<>(_level, HttpStatus.CREATED);
			  } catch (Exception e) {
			    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			  }
	  }
	 
	 @GetMapping("/levels")
	  public ResponseEntity<List<Level>> getAllLevels() {
		
		 List<Level> levelsList = new ArrayList<Level>();
		 levels.findAll().forEach(levelsList::add);
		      return new ResponseEntity<>(levelsList, HttpStatus.OK);	  }
	 
	 @PostMapping("/flow")
	  public ResponseEntity<Workflow> createFlow(@RequestBody Workflow flow) {
		 try {
			 flow.setStatus(FlowConstants.ACTIVE_STATUS);
			 Workflow _flow = workflow.save(flow);
			 helper.initialize(flow,0);
			    return new ResponseEntity<>(_flow, HttpStatus.CREATED);
			  } catch (Exception e) {
			    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			  }
	  }
	 
	 @GetMapping("/flow")
	  public ResponseEntity<List<Workflow>> getAllFlows() {
		
		 List<Workflow> flowList = new ArrayList<Workflow>();
		 workflow.findAll().forEach(flowList::add);
		      return new ResponseEntity<>(flowList, HttpStatus.OK);	  }
	 
	 @GetMapping("/flow/{id}")
	 public ResponseEntity<Workflow> getFlowByID(@PathVariable("id") String id) {
	   Optional<Workflow> workflowData = workflow.findById(id);

	   if (workflowData.isPresent()) {
	     return new ResponseEntity<>(workflowData.get(), HttpStatus.OK);
	   } else {
	     return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	   }
	 }
	 
	 @GetMapping("/tasks/{id}")
	  public ResponseEntity<List<Task>> getTasksForUser(@PathVariable("id") String id) {
		
		 List<Task> taskList = new ArrayList<Task>();
		 tasks.findAll().forEach(taskList::add);
		 List<Task> tasksfList = taskList.stream().filter(t -> t.getUserId().equals(id)&& t.isVisible()).collect(Collectors.toList());
		      return new ResponseEntity<>(tasksfList, HttpStatus.OK);	  }
	 
	 @PostMapping("/tasks/{id}")
	  public ResponseEntity<Workflow> performAction(@PathVariable("id") String id,@RequestParam("action") String action) {
		 try {
			    helper.performAction(id,action);
			    return new ResponseEntity<>(null, HttpStatus.CREATED);
			  } catch (Exception e) {
			    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			  }
	  }
	 

}
