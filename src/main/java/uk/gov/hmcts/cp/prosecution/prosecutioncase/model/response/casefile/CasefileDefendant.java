package uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record CasefileDefendant(
        String defendantId,
        PersonalInformation personalInformation,
        List<CasefileOffence> offences
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PersonalInformation(
            String firstName,
            String lastName
    ) {}
}