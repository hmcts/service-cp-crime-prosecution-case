package uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CasefileResponse(
        String caseId,
        String urn,
        String type,
        String prosecutionCaseReference,
        List<CasefileDefendant> defendants
) {}