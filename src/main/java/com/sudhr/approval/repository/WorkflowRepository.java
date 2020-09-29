package com.sudhr.approval.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sudhr.approval.pojo.Workflow;

public interface WorkflowRepository extends MongoRepository<Workflow, String> {

}
