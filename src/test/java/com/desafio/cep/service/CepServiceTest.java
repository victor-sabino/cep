package com.desafio.cep.service;

import com.desafio.cep.exception.ExternalApiException;
import com.desafio.cep.model.CepLog;
import com.desafio.cep.model.CepResponse;
import com.desafio.cep.repository.CepLogRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    @Test
    public void deveRetornarErroQuandoCepNaoExistir() {
        String cep = "00000000";

        stubFor(get(urlEqualTo("/ws/" + cep + "/json/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"erro\": true }")));

        StepVerifier.create(cepService.getCep(cep))
                .expectErrorMatches(throwable ->
                        throwable instanceof ExternalApiException &&
                                throwable.getMessage().contains("CEP não encontrado"))
                .verify();
    }

    @Test
    void deveRetornarErroQuandoApiRetornar500() {
        String cep = "99999999";

        stubFor(get(urlEqualTo("/ws/" + cep + "/json/"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Erro interno da API externa")));

        StepVerifier.create(cepService.getCep(cep))
                .expectErrorMatches(throwable ->
                        throwable instanceof ExternalApiException &&
                                throwable.getMessage().contains("Erro interno"))
                .verify();
    }
}
