package uk.gov.hmcts.cp.prosecution.prosecutioncase.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.cp.config.AppPropertiesBackend;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class ProsecutionCasefileControllerIntegrationTest {

    @Autowired
    AppPropertiesBackend appProperties;

    @Resource
    private MockMvc mockMvc;

    protected WireMockServer wireMockServer;

    String caseUrn = "ABCD1234567";
    UUID caseId = UUID.fromString("970fc69f-fbd4-4a83-baaf-ce8c10df1e51");

    @BeforeEach
    void beforeEach() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8081));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8081);
    }

    @AfterEach
    void afterEach() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void get_case_detail_for_progression_response_should_return_ok() {
        String cp_response = "cp_response.json";
        String expected_amp_response = "expected_amp_response.json";

        stub_cp_response_and_verify_expected_amp_response(cp_response, expected_amp_response);
    }

    @Test
    void empty_cp_response_should_return_404() throws Exception {
        stubMappingResponse(caseUrn, caseId);
        String expectedProsecutionUrl = String.format("%s%s/%s", appProperties.getProsecutionCasefileUrl(), appProperties.getProsecutionCasefilePath(), caseId);

        ResponseDefinitionBuilder mockResponse = aResponse()
                .withStatus(HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(readResourceContents("cp_empty_response.json"));
        log.info("Stubbing prosecution response url:{}", expectedProsecutionUrl);
        stubFor(WireMock.get(urlEqualTo(expectedProsecutionUrl)).willReturn(mockResponse));

        mockMvc.perform(get("/prosecution-case/cases/{case_urn}", caseUrn)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void bad_caseurn_should_return_404() throws Exception {
        String expectedUrl = String.format("%s/%s", appProperties.getCaseMapperPath(), caseUrn);
        ResponseDefinitionBuilder mockResponse = aResponse()
                .withStatus(HTTP_NOT_FOUND)
                .withHeader("Content-Type", "application/json");
        log.info("Stubbing mapping url:{}", expectedUrl);
        stubFor(WireMock.get(urlEqualTo(expectedUrl)).willReturn(mockResponse));


        mockMvc.perform(get("/cases/{case_urn}", caseUrn)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void bad_prosecutionCasefile_response_should_return_404() throws Exception {
        stubMappingResponse(caseUrn, caseId);
        String expectedProsecutionUrl = String.format("%s%s/%s", appProperties.getProsecutionCasefileUrl(), appProperties.getProsecutionCasefilePath(), caseId);

        ResponseDefinitionBuilder mockResponse = aResponse()
                .withStatus(HTTP_NOT_FOUND)
                .withHeader("Content-Type", "application/json");
        log.info("Stubbing prosecution response url:{}", expectedProsecutionUrl);
        stubFor(WireMock.get(urlEqualTo(expectedProsecutionUrl)).willReturn(mockResponse));

        mockMvc.perform(get("/cases/{case_urn}", caseUrn)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private void stub_cp_response_and_verify_expected_amp_response(String cp_response_file, String expected_amp_response_file) {
        stubMappingResponse(caseUrn, caseId);
        stubGetProgressionCaseResponse(caseId, cp_response_file);

        String expectedResponse = readResourceContents(expected_amp_response_file);
        amp_endpoint_and_verify_response(expectedResponse);
    }

    @SneakyThrows
    private void amp_endpoint_and_verify_response(String expectedResponse) {
        mockMvc.perform(get("/prosecution-case/cases/{case_urn}", caseUrn))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse))
                .andReturn();
    }

    private void stubMappingResponse(String caseUrn, UUID caseId) {
        String expectedUrl = String.format("%s/%s", appProperties.getCaseMapperPath(), caseUrn);
        String responseBody = String.format("{\"caseUrn\":\"%s\", \"caseId\":\"%s\"}", caseUrn, caseId);
        ResponseDefinitionBuilder mockResponse = aResponse()
                .withStatus(HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(responseBody);
        log.info("Stubbing mapping url:{}", expectedUrl);
        stubFor(WireMock.get(urlEqualTo(expectedUrl)).willReturn(mockResponse));
    }

    private void stubGetProgressionCaseResponse(UUID caseId, String filename) {
        String expectedUrl = String.format("%s/%s", appProperties.getProsecutionCasefilePath(), caseId);
        ResponseDefinitionBuilder mockResponse = aResponse()
                .withStatus(HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(readResourceContents(filename));
        log.info("Stubbing progression url:{}", expectedUrl);
        stubFor(WireMock.get(urlEqualTo(expectedUrl)).willReturn(mockResponse));
    }

    @SneakyThrows
    private String readResourceContents(final String resourceName) {
        URL resource = getClass().getClassLoader().getResource(resourceName);
        return Files.readString(Path.of(resource.toURI()));
    }
}