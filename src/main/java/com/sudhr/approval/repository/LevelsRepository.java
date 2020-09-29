package com.sudhr.approval.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sudhr.approval.pojo.Level;

public interface LevelsRepository extends MongoRepository<Level, String> {

}
