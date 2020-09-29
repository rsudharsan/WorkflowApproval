package com.sudhr.approval.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sudhr.approval.pojo.Task;

public interface TasksRepository extends MongoRepository<Task, String> {

}
