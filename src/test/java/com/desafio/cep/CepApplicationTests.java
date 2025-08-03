package com.desafio.cep;

import com.desafio.cep.model.CepLog;
import com.desafio.cep.model.CepResponse;
import com.desafio.cep.repository.CepLogRepository;
import com.desafio.cep.service.CepService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

class CepServiceTest {

    private WireMockServer wireMockServer;
    private CepService cepService;
    private CepLogRepository repository;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        configureFor("localhost", 8089);

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8089")
                .build();

        repository = mock(CepLogRepository.class);
        cepService = new CepService(webClient, repository);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testGetCep_Sucesso() {
        String cep = "01001000";

        wireMockServer.stubFor(get(urlEqualTo("/ws/01001000/json/"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"cep\": \"01001-000\", \"logradouro\": \"Praça da Sé\" }")));

        CepResponse response = cepService.getCep(cep).block();

        assertNotNull(response);
        assertEquals("01001-000", response.getCep());
        assertEquals("Praça da Sé", response.getLogradouro());

        verify(repository, times(1)).save(any(CepLog.class));
    }

    @Test
    public void testGetCep_NaoEncontrado() {
        String cep = "99999999";

        wireMockServer.stubFor(get(urlEqualTo("/ws/99999999/json/"))
                .willReturn(aResponse()
                        .withStatus(404)));

        CepResponse response = cepService.getCep(cep)
                .onErrorResume(ex -> Mono.just(new CepResponse()))
                .block();

        assertNotNull(response);
        assertNull(response.getLogradouro());
        verify(repository, never()).save(any(CepLog.class));
    }
}
