package com.sudhr.approval.pojo;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection = "approval_levels")
@JsonInclude(Include.NON_NULL)
public class Level {
	
	 @Id
	  private String id;
	 
	 private String description;
	 
	private String type;
	
	private List<Approval> approvals;
	
	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	  public List<Approval> getApprovals() { return approvals; }
	  
	  public void setApprovals(List<Approval> approvals) { this.approvals =
	  approvals; }
	 
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

}
