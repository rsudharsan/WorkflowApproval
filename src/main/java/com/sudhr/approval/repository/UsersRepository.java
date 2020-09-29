package com.sudhr.approval.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sudhr.approval.pojo.User;

public interface UsersRepository extends MongoRepository<User, String> {

}
