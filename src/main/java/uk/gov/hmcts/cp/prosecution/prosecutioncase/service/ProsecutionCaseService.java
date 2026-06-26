package uk.gov.hmcts.cp.prosecution.prosecutioncase.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.hmcts.cp.openapi.model.ProsecutionCaseView;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.client.ProsecutionCasefileClient;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.mapper.ProsecutionCasefileMapper;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileResponse;

import java.util.UUID;

@Service
@Slf4j
public class ProsecutionCaseService {

    private final ProsecutionCasefileClient casefileClient;
    private final ProsecutionCasefileMapper mapper;

    public ProsecutionCaseService(final ProsecutionCasefileClient casefileClient,
                                  final ProsecutionCasefileMapper mapper) {
        this.casefileClient = casefileClient;
        this.mapper = mapper;
    }

    public ProsecutionCaseView getDefendants(final UUID caseId) {
        final CasefileResponse casefile = casefileClient.getCaseById(caseId);
        validateOrThrowError(casefile, HttpStatus.NOT_FOUND, "progression response should not be empty");
        return mapper.toProsecutionCaseView(casefile);
    }

    private void validateOrThrowError(final CasefileResponse casefile, final HttpStatus status, final String errorMessage) {
        if (ObjectUtils.isEmpty(casefile)) {
            log.error(errorMessage);
            throw new ResponseStatusException(status, errorMessage);
        }
    }
}