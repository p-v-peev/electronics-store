package com.example.pvpeev.electronics_store.auth.controller;

import com.example.pvpeev.electronics_store.auth.dto.AuthorityResponse;
import com.example.pvpeev.electronics_store.auth.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.pvpeev.electronics_store.auth.controller.AuthorityController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class AuthorityController {
    public static final String PATH = "/api/v1/authorities";

    private final AuthorityService authorityService;

    @GetMapping
    public ResponseEntity<List<AuthorityResponse>> getAll() {
        return ResponseEntity.ok(authorityService.findAll());
    }
}
