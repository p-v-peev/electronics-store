package com.example.pvpeev.electronics_store.auth.controller;

import com.example.pvpeev.electronics_store.auth.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.pvpeev.electronics_store.auth.controller.AddressController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class AddressController {
    public static final String PATH = "/api/v1/addresses";

    private final UserAddressService userAddressService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddresses(@PathVariable("id") Long id) {
        userAddressService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
