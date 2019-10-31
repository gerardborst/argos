package com.rabobank.argos.service.adapter.in.rest;

import com.rabobank.argos.domain.LinkMetaBlockRepository;
import com.rabobank.argos.domain.SupplyChainRepository;
import com.rabobank.argos.domain.model.LinkMetaBlock;
import com.rabobank.argos.domain.model.SupplyChain;
import com.rabobank.argos.service.adapter.in.rest.api.model.RestLinkMetaBlock;
import com.rabobank.argos.service.adapter.in.rest.mapper.LinkMetaBlockMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LinkRestServiceTest {

    private static final String SUPPLY_CHAIN_ID = "supplyChainId";
    private static final String HASH = "hash";
    @Mock
    private LinkMetaBlockRepository linkMetaBlockRepository;

    @Mock
    private SupplyChainRepository supplyChainRepository;

    @Mock
    private LinkMetaBlockMapper converter;

    @Mock
    private RestLinkMetaBlock restLinkMetaBlock;

    @Mock
    private LinkMetaBlock linkMetaBlock;

    private LinkRestService restService;

    @Mock
    private SupplyChain supplyChain;

    @BeforeEach
    void setUp() {
        restService = new LinkRestService(linkMetaBlockRepository, supplyChainRepository, converter);
    }

    @Test
    void createLink() {
        when(converter.convertFromRestLinkMetaBlock(restLinkMetaBlock)).thenReturn(linkMetaBlock);
        assertThat(restService.createLink(SUPPLY_CHAIN_ID, restLinkMetaBlock).getStatusCodeValue(), is(204));
        verify(linkMetaBlock).setSupplyChainId(SUPPLY_CHAIN_ID);
        verify(linkMetaBlockRepository).save(linkMetaBlock);
    }

    @Test
    void findLink() {
        when(supplyChainRepository.findBySupplyChainId(SUPPLY_CHAIN_ID)).thenReturn(Optional.of(supplyChain));
        when(linkMetaBlockRepository.findBySupplyChainAndSha(SUPPLY_CHAIN_ID, HASH)).thenReturn(Collections.singletonList(linkMetaBlock));
        when(converter.convertToRestLinkMetaBlock(linkMetaBlock)).thenReturn(restLinkMetaBlock);
        ResponseEntity<List<RestLinkMetaBlock>> response = restService.findLink(SUPPLY_CHAIN_ID, HASH);
        assertThat(response.getBody(), hasSize(1));
        assertThat(response.getBody().get(0), sameInstance(restLinkMetaBlock));
        assertThat(response.getStatusCodeValue(), is(200));
    }

    @Test
    void findLinkUnknownSupplyChain() {
        when(supplyChainRepository.findBySupplyChainId(SUPPLY_CHAIN_ID)).thenReturn(Optional.empty());
        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> restService.findLink(SUPPLY_CHAIN_ID, HASH).getStatusCodeValue());
        assertThat(error.getStatus().value(), is(404));
    }
}