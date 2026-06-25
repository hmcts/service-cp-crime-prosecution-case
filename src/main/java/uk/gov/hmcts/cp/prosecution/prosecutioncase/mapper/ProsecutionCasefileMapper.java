package uk.gov.hmcts.cp.prosecution.prosecutioncase.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.output.DefendantView;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.output.OffenceView;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileDefendant;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileOffence;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileResponse;

import java.util.List;

@Component
public class ProsecutionCasefileMapper {

    public List<DefendantView> toDefendantViews(CasefileResponse casefile) {
        if (casefile == null || casefile.defendants() == null) {
            return List.of();
        }
        return casefile.defendants().stream()
                .map(d -> new DefendantView(
                        d.defendantId(),
                        fullName(d.personalInformation()),
                        toOffenceViews(d.offences())
                ))
                .toList();
    }

    private String fullName(CasefileDefendant.PersonalInformation pi) {
        if (pi == null) return null;
        return (pi.firstName() + " " + pi.lastName()).strip();
    }

    private List<OffenceView> toOffenceViews(List<CasefileOffence> offences) {
        if (offences == null) return List.of();
        return offences.stream()
                .map(o -> new OffenceView(
                        o.offenceId(),
                        o.offenceCode(),
                        o.offenceTitle() != null ? o.offenceTitle() : o.offenceWording(),
                        o.plea() == null || o.plea().isBlank() ? "Active" : o.plea()
                ))
                .toList();
    }
}