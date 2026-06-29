package uk.gov.hmcts.cp.prosecution.prosecutioncase.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.cp.config.AppPropertiesBackend;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileResponse;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProsecutionCasefileClientTest {

    @Mock
    private AppPropertiesBackend appProperties;

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<String>> entityCaptor;

    private ProsecutionCasefileClient client;

    @BeforeEach
    void setUp() {
        client = new ProsecutionCasefileClient(appProperties, restTemplate);
    }

    @Test
    void getCaseById_returns_casefile_response() {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        CasefileResponse expected = new CasefileResponse(caseId.toString(), "22SW0001", null, null, List.of());

        when(appProperties.getProsecutionCasefileUrl()).thenReturn("http://localhost:8080");
        when(appProperties.getProsecutionCasefilePath()).thenReturn("/cases");
        when(appProperties.getProsecutionCasefileCjscppuid()).thenReturn("test-uid");
        when(restTemplate.exchange(
                eq("http://localhost:8080/cases/" + caseId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CasefileResponse.class)
        )).thenReturn(ResponseEntity.ok(expected));

        CasefileResponse result = client.getCaseById(caseId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getCaseById_builds_url_from_configured_base_url_and_path() {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        CasefileResponse body = new CasefileResponse(caseId.toString(), null, null, null, null);

        when(appProperties.getProsecutionCasefileUrl()).thenReturn("http://casefile-service");
        when(appProperties.getProsecutionCasefilePath()).thenReturn("/api/cases");
        when(appProperties.getProsecutionCasefileCjscppuid()).thenReturn("test-uid");
        when(restTemplate.exchange(
                eq("http://casefile-service/api/cases/" + caseId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CasefileResponse.class)
        )).thenReturn(ResponseEntity.ok(body));

        client.getCaseById(caseId);

        verify(restTemplate).exchange(
                eq("http://casefile-service/api/cases/" + caseId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CasefileResponse.class)
        );
    }

    @Test
    void getCaseById_sends_correct_accept_header() {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        CasefileResponse body = new CasefileResponse(caseId.toString(), null, null, null, null);

        when(appProperties.getProsecutionCasefileUrl()).thenReturn("http://localhost:8080");
        when(appProperties.getProsecutionCasefilePath()).thenReturn("/cases");
        when(appProperties.getProsecutionCasefileCjscppuid()).thenReturn("test-uid");
        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                entityCaptor.capture(),
                eq(CasefileResponse.class)
        )).thenReturn(ResponseEntity.ok(body));

        client.getCaseById(caseId);

        assertThat(entityCaptor.getValue().getHeaders().getFirst("Accept"))
                .isEqualTo("application/vnd.prosecutioncasefile.query.case+json");
    }
}