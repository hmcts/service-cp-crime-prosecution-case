package uk.gov.hmcts.cp.prosecution.prosecutioncase.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.service.CaseUrnMapperService;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.service.ProsecutionCaseService;

import java.util.UUID;

@RestController
@RequestMapping("/prosecution-case")
@Slf4j
public class ProsecutionCasefileController {

    private static final String CASE_URN_REGEX = "^[0-9a-zA-Z]{1,30}$";
    private final CaseUrnMapperService caseUrnMapperService;
    private final ProsecutionCaseService prosecutionCaseService;

    public ProsecutionCasefileController(CaseUrnMapperService caseUrnMapperService,
                                         ProsecutionCaseService prosecutionCaseService) {
        this.caseUrnMapperService = caseUrnMapperService;
        this.prosecutionCaseService = prosecutionCaseService;
    }

    @GetMapping("/cases/{caseURN}")
    public ProsecutionCaseService.DefendantsView getCaseByUrn(@PathVariable String caseURN) {
        UUID caseId = caseUrnMapperService.getCaseId(validateCaseUrn(caseURN));
        return prosecutionCaseService.getDefendants(caseId);
    }

    private String validateCaseUrn(final String caseUrn) {
        if (caseUrn == null || !caseUrn.matches(CASE_URN_REGEX)) {
            log.warn("CaseUrn does not match expected caseRegex:{}", CASE_URN_REGEX);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Case urn must be between 1 and 30 alphanumerics");
        }
        return caseUrn;
    }
}