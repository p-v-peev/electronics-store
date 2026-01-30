package com.example.pvpeev.electronics_store.auth.controller;

import com.example.pvpeev.electronics_store.auth.dto.*;
import com.example.pvpeev.electronics_store.auth.service.UserAddressService;
import com.example.pvpeev.electronics_store.auth.service.UserAuthorityService;
import com.example.pvpeev.electronics_store.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.auth.controller.UserController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class UserController {
    public static final String PATH = "/api/v1/users";

    private final UserService userService;
    private final UserAddressService userAddressService;
    private final UserAuthorityService userAuthorityService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody UserRequest request, UriComponentsBuilder ucb) {
        final UUID userId = userService.create(request);
        return ResponseEntity.created(ucb.path(PATH).pathSegment("{id}").build(userId)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable("id") UUID id) {
        final Optional<UserResponse> response = userService.getById(id);
        return response.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<UserAddressResponse>> getUserAddresses(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userAddressService.findAllByUserId(id));
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<UserAddressResponse> createUserAddress(@RequestBody UserAddressRequest request, @PathVariable("id") UUID id, UriComponentsBuilder ucb) {
        UserAddressResponse address = userAddressService.createUserAddress(request, id);
        final URI uri = ucb.path(PATH).pathSegment("{id}", "addresses").build(id);
        return ResponseEntity.created(uri).body(address);
    }

    @GetMapping("/{id}/authorities")
    public ResponseEntity<List<AuthorityResponse>> getUserAuthorities(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userAuthorityService.findAllByUserId(id));
    }

    @PostMapping("/{userId}/authorities/{authorityId}")
    public ResponseEntity<AuthorityResponse> createUserAuthority(@PathVariable("userId") UUID userId, @PathVariable("authorityId") Integer authorityId, UriComponentsBuilder ucb) {
        final AuthorityResponse authority = userAuthorityService.grantUserAuthority(userId, authorityId);
        final URI uri = ucb.path(PATH).pathSegment("{id}", "authorities").build(userId);
        return ResponseEntity.created(uri).body(authority);
    }

    @DeleteMapping("/{userId}/authorities/{authorityId}")
    public ResponseEntity<Void> revokeUseAuthority(@PathVariable("userId") UUID userId, @PathVariable("authorityId") Integer authorityId) {
        userAuthorityService.revokeUserAuthority(userId, authorityId);
        return ResponseEntity.noContent().build();
    }
}
