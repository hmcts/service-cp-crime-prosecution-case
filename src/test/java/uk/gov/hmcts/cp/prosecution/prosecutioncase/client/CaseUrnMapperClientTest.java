package uk.gov.hmcts.cp.prosecution.prosecutioncase.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.cp.config.AppPropertiesBackend;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.caseurnmapper.CaseMapperResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseUrnMapperClientTest {

    @Mock
    private AppPropertiesBackend appProperties;

    @Mock
    private RestTemplate restTemplate;

    private CaseUrnMapperClient client;

    @BeforeEach
    void setUp() {
        client = new CaseUrnMapperClient(appProperties, restTemplate);
    }

    @Test
    void getCaseId_returns_uuid_from_response_body() {
        UUID expected = UUID.fromString("00000000-0000-0000-0000-000000000001");
        CaseMapperResponse body = CaseMapperResponse.builder().caseId(expected).caseUrn("22SW0001").build();

        when(appProperties.getCaseMapperUrl()).thenReturn("http://localhost:8081");
        when(appProperties.getCaseMapperPath()).thenReturn("/urnmapper");
        when(restTemplate.exchange(
                eq("http://localhost:8081/urnmapper/22SW0001"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CaseMapperResponse.class)
        )).thenReturn(ResponseEntity.ok(body));

        UUID result = client.getCaseId("22SW0001");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getCaseId_builds_url_from_configured_base_url_and_path() {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        CaseMapperResponse body = CaseMapperResponse.builder().caseId(caseId).build();

        when(appProperties.getCaseMapperUrl()).thenReturn("http://mapper-service");
        when(appProperties.getCaseMapperPath()).thenReturn("/api/urns");
        when(restTemplate.exchange(
                eq("http://mapper-service/api/urns/TESTURN"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CaseMapperResponse.class)
        )).thenReturn(ResponseEntity.ok(body));

        client.getCaseId("TESTURN");

        verify(restTemplate).exchange(
                eq("http://mapper-service/api/urns/TESTURN"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CaseMapperResponse.class)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void getCaseId_sends_json_accept_header() {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        CaseMapperResponse body = CaseMapperResponse.builder().caseId(caseId).build();
        ArgumentCaptor<HttpEntity<String>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(appProperties.getCaseMapperUrl()).thenReturn("http://localhost:8081");
        when(appProperties.getCaseMapperPath()).thenReturn("/urnmapper");
        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                entityCaptor.capture(),
                eq(CaseMapperResponse.class)
        )).thenReturn(ResponseEntity.ok(body));

        client.getCaseId("22SW0001");

        assertThat(entityCaptor.getValue().getHeaders().getAccept())
                .containsExactly(MediaType.APPLICATION_JSON);
    }
}