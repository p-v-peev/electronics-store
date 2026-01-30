package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserAuthorityEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAuthorityRepository extends CrudRepository<UserAuthorityEntity, Long> {

    List<UserAuthorityEntity> findAllByUserId(UUID id);

    int deleteByUserIdAndAuthorityId(UUID userId, Integer authorityId);

    int deleteByUserId(UUID id);
}
