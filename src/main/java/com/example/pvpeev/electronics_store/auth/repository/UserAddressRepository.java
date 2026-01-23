package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserAddressEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAddressRepository extends CrudRepository<UserAddressEntity, UUID> {

    List<UserAddressEntity> findAllByUserId(UUID userId);
}
