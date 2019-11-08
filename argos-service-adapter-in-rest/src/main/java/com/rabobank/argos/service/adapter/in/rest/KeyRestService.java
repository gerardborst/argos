package com.rabobank.argos.service.adapter.in.rest;

import com.rabobank.argos.domain.KeyPairRepository;
import com.rabobank.argos.domain.model.KeyPair;
import com.rabobank.argos.service.adapter.in.rest.api.handler.KeyApi;
import com.rabobank.argos.service.adapter.in.rest.api.model.RestKeyPair;
import com.rabobank.argos.service.adapter.in.rest.mapper.KeyPairMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RequestMapping("/api")
@RestController
@Slf4j
@RequiredArgsConstructor
public class KeyRestService implements KeyApi {

    private final KeyPairMapper converter;
    private final KeyPairRepository keyPairRepository;

    @Override
    public ResponseEntity<RestKeyPair> getKey(String keyId) {
        KeyPair keyPair = keyPairRepository.findByKeyId(keyId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "key pair not found : " + keyId)
        );
        return new ResponseEntity<>(converter.convertToRestKeyPair(keyPair), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> storeKey(@Valid RestKeyPair restKeyPair) {
        keyPairRepository.save(converter.convertFromRestKeyPair(restKeyPair));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}