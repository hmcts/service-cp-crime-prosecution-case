package uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record CasefileOffence(
        String offenceId,
        String offenceCode,
        String offenceWording,
        String offenceTitle,
        String plea
) {}