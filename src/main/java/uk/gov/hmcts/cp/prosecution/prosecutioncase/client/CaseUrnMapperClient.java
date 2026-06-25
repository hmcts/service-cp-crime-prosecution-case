package uk.gov.hmcts.cp.prosecution.prosecutioncase.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.cp.config.AppPropertiesBackend;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.caseUrnMapper.CaseMapperResponse;

import java.util.UUID;

@Component
@Slf4j
public class CaseUrnMapperClient {

    private final AppPropertiesBackend appProperties;
    private final RestTemplate restTemplate;

    public CaseUrnMapperClient(AppPropertiesBackend appProperties, RestTemplate restTemplate) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplate;
    }

    public UUID getCaseId(final String caseUrn) {
        final String url = buildUrl(caseUrn);
        log.info("Resolving caseId for URN");
        final ResponseEntity<CaseMapperResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                getRequestEntity(),
                CaseMapperResponse.class
        );
        return response.getBody().getCaseId();
    }

    private String buildUrl(final String caseUrn) {
        return String.format("%s%s/%s", appProperties.getCaseMapperUrl(), appProperties.getCaseMapperPath(), caseUrn);
    }

    private HttpEntity<String> getRequestEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }
}