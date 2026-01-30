package com.example.pvpeev.electronics_store.auth.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserAddressEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAddressRepository extends ListCrudRepository<UserAddressEntity, Long> {

    @Modifying
    @Query("DELETE FROM user_address WHERE id = :id")
    int deleteByIdWithCount(Long id);


    List<UserAddressEntity> findAllByUserId(UUID userId);

    int deleteByUserId(UUID id);
}
