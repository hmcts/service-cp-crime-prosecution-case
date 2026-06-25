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

    public ProsecutionCaseView toProsecutionCaseView(final CasefileResponse casefile) {
        final List<DefendantView> defendants = casefile == null || casefile.defendants() == null
                ? List.of()
                : casefile.defendants().stream()
                        .map(d -> DefendantView.builder()
                                .id(UUID.fromString(d.defendantId()))
                                .name(fullName(d.personalInformation()))
                                .offences(toOffenceViews(d.offences()))
                                .build())
                        .toList();
        return new ProsecutionCaseView(defendants);
    }

    private String fullName(final CasefileDefendant.PersonalInformation pi) {
        return pi == null ? null : (pi.firstName() + " " + pi.lastName()).strip();
    }

    private List<OffenceView> toOffenceViews(final List<CasefileOffence> offences) {
        final List<CasefileOffence> safe = offences == null ? List.of() : offences;
        return safe.stream()
                .map(o -> OffenceView.builder()
                        .id(UUID.fromString(o.offenceId()))
                        .code(o.offenceCode())
                        .title(o.offenceTitle() != null ? o.offenceTitle() : o.offenceWording())
                        .status(o.plea() == null || o.plea().isBlank() ? "Active" : o.plea())
                        .build())
                .toList();
    }
}