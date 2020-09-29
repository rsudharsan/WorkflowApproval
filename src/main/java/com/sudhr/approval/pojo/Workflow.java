package com.sudhr.approval.pojo;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection="workflow")
@JsonInclude(Include.NON_NULL)
public class Workflow {
	
	@Id
	  private String id;

	  private String description;
	  
	  private String status ;
	  
	  
	  private List<Level> levels;
	  
	  private int currLevel;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Level> getLevels() {
		return levels;
	}

	public void setLevels(List<Level> levels) {
		this.levels = levels;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCurrLevel() {
		return currLevel;
	}

	public void setCurrLevel(int currLevel) {
		this.currLevel = currLevel;
	}
	  

}
