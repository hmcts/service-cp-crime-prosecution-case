package uk.gov.hmcts.cp.prosecution.prosecutioncase.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.hmcts.cp.openapi.api.ProsecutionCasesApi;
import uk.gov.hmcts.cp.openapi.model.ProsecutionCaseView;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.service.CaseUrnMapperService;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.service.ProsecutionCaseService;

import java.util.UUID;

@RestController
@Slf4j
@AllArgsConstructor
public class ProsecutionCasefileController implements ProsecutionCasesApi {

    private static final String CASE_URN_REGEX = "^[0-9a-zA-Z]{1,30}$";
    private final CaseUrnMapperService caseUrnMapperService;
    private final ProsecutionCaseService prosecutionCaseService;

    @Override
    public ResponseEntity<ProsecutionCaseView> getProsecutionCase(final String caseURN) {
        final UUID caseId = caseUrnMapperService.getCaseId(validateCaseUrn(caseURN));
        return ResponseEntity.ok(prosecutionCaseService.getDefendants(caseId));
    }

    private String validateCaseUrn(final String caseUrn) {
        if (caseUrn == null || !caseUrn.matches(CASE_URN_REGEX)) {
            log.warn("CaseUrn does not match expected caseRegex:{}", CASE_URN_REGEX);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Case urn must be between 1 and 30 alphanumerics");
        }
        return caseUrn;
    }
}