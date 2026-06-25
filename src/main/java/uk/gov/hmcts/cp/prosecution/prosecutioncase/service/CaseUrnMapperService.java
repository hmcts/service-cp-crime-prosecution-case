package uk.gov.hmcts.cp.prosecution.prosecutioncase.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.client.CaseUrnMapperClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseUrnMapperService {

    private final CaseUrnMapperClient caseUrnMapperClient;

    public UUID getCaseId(final String caseUrn) {
        return caseUrnMapperClient.getCaseId(caseUrn);
    }
}