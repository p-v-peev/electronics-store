package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.entity.WarehouseBinEntity;
import com.example.pvpeev.electronics_store.order.repository.WarehouseBinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WarehouseBinService {

    private final WarehouseBinRepository warehouseBinRepository;

    public Integer createNewBin(String binLabel) {
        return warehouseBinRepository.save(new WarehouseBinEntity(null, binLabel)).getId();
    }


}
