package uk.gov.hmcts.cp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
public class AppPropertiesBackend {

    private final String prosecutionCasefileUrl;
    private final String prosecutionCasefilePath;
    private final String prosecutionCasefileCjscppuid;
    private final String caseMapperUrl;
    private final String caseMapperPath;

    public AppPropertiesBackend(
            @Value("${prosecution-casefile-client.url}") final String prosecutionCasefileUrl,
            @Value("${prosecution-casefile-client.path}") final String prosecutionCasefilePath,
            @Value("${prosecution-casefile-client.cjscppuid:}") final String prosecutionCasefileCjscppuid,
            @Value("${case-mapper-client.url}") final String caseMapperUrl,
            @Value("${case-mapper-client.path}") final String caseMapperPath) {
        this.prosecutionCasefileUrl = prosecutionCasefileUrl;
        this.prosecutionCasefilePath = prosecutionCasefilePath;
        this.prosecutionCasefileCjscppuid = prosecutionCasefileCjscppuid;
        this.caseMapperUrl = caseMapperUrl;
        this.caseMapperPath = caseMapperPath;
    }
}