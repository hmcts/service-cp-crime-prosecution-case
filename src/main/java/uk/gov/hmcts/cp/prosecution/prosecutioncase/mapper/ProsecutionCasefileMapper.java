package uk.gov.hmcts.cp.prosecution.prosecutioncase.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.cp.openapi.model.DefendantView;
import uk.gov.hmcts.cp.openapi.model.OffenceView;
import uk.gov.hmcts.cp.openapi.model.ProsecutionCaseView;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileDefendant;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileOffence;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileResponse;

import java.util.List;
import java.util.UUID;

@Component
public class ProsecutionCasefileMapper {

    public ProsecutionCaseView toProsecutionCaseView(CasefileResponse casefile) {
        if (casefile == null || casefile.defendants() == null) {
            return new ProsecutionCaseView(List.of());
        }
        final List<DefendantView> defendants = casefile.defendants().stream()
                .map(d -> DefendantView.builder()
                        .id(UUID.fromString(d.defendantId()))
                        .name(fullName(d.personalInformation()))
                        .offences(toOffenceViews(d.offences()))
                        .build())
                .toList();
        return new ProsecutionCaseView(defendants);
    }

    private String fullName(CasefileDefendant.PersonalInformation pi) {
        if (pi == null) return null;
        return (pi.firstName() + " " + pi.lastName()).strip();
    }

    private List<OffenceView> toOffenceViews(List<CasefileOffence> offences) {
        if (offences == null) return List.of();
        return offences.stream()
                .map(o -> OffenceView.builder()
                        .id(UUID.fromString(o.offenceId()))
                        .code(o.offenceCode())
                        .title(o.offenceTitle() != null ? o.offenceTitle() : o.offenceWording())
                        .status(o.plea() == null || o.plea().isBlank() ? "Active" : o.plea())
                        .build())
                .toList();
    }
}