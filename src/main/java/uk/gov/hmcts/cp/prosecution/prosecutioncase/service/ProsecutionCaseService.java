package uk.gov.hmcts.cp.prosecution.prosecutioncase.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.client.ProsecutionCasefileClient;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.mapper.ProsecutionCasefileMapper;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.output.DefendantView;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileResponse;

import java.util.List;
import java.util.UUID;

@Service
public class ProsecutionCaseService {

    private final ProsecutionCasefileClient casefileClient;
    private final ProsecutionCasefileMapper mapper;

    public ProsecutionCaseService(ProsecutionCasefileClient casefileClient,
                                  ProsecutionCasefileMapper mapper) {
        this.casefileClient = casefileClient;
        this.mapper = mapper;
    }

    public record DefendantsView(List<DefendantView> defendants) {}

    public DefendantsView getDefendants(UUID caseId) {
        CasefileResponse casefile = casefileClient.getCaseById(caseId);
        List<DefendantView> defendants = mapper.toDefendantViews(casefile);
        return new DefendantsView(defendants);
    }
}