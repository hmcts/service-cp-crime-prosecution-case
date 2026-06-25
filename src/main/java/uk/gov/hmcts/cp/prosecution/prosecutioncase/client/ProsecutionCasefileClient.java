package uk.gov.hmcts.cp.prosecution.prosecutioncase.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.cp.config.AppPropertiesBackend;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileResponse;

import java.util.UUID;

@Component
public class ProsecutionCasefileClient {

    private static final String ACCEPT_CASE = "application/vnd.prosecutioncasefile.query.case+json";

    private final AppPropertiesBackend appProperties;
    private final RestTemplate restTemplate;

    public ProsecutionCasefileClient(final AppPropertiesBackend appProperties,
                                     final RestTemplate restTemplate) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplate;
    }

    public CasefileResponse getCaseById(final UUID caseId) {
        final String url = String.format("%s%s/%s",
                appProperties.getProsecutionCasefileUrl(),
                appProperties.getProsecutionCasefilePath(),
                caseId);
        return restTemplate.exchange(url,
                HttpMethod.GET,
                getRequestEntity(),
                CasefileResponse.class).getBody();
    }

    private HttpEntity<String> getRequestEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", ACCEPT_CASE);
        headers.add("CJSCPPUID", appProperties.getProsecutionCasefileCjscppuid());
        return new HttpEntity<>(headers);
    }
}