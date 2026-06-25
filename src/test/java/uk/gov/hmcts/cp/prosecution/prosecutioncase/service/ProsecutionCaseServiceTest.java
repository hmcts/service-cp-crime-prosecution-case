package uk.gov.hmcts.cp.prosecution.prosecutioncase.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.cp.openapi.model.ProsecutionCaseView;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.client.ProsecutionCasefileClient;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.mapper.ProsecutionCasefileMapper;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileResponse;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProsecutionCaseServiceTest {

    @Mock
    private ProsecutionCasefileClient casefileClient;

    @Mock
    private ProsecutionCasefileMapper mapper;

    @InjectMocks
    private ProsecutionCaseService service;

    @Test
    void getDefendants_delegates_to_client_and_mapper() {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        CasefileResponse casefile = new CasefileResponse(caseId.toString(), "22SW0001", null, null, List.of());
        ProsecutionCaseView view = new ProsecutionCaseView(List.of());

        when(casefileClient.getCaseById(caseId)).thenReturn(casefile);
        when(mapper.toProsecutionCaseView(casefile)).thenReturn(view);

        ProsecutionCaseView result = service.getDefendants(caseId);

        assertThat(result).isEqualTo(view);
    }

    @Test
    void getDefendants_passes_caseId_to_client() {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        CasefileResponse casefile = new CasefileResponse(caseId.toString(), null, null, null, null);

        when(casefileClient.getCaseById(caseId)).thenReturn(casefile);
        when(mapper.toProsecutionCaseView(casefile)).thenReturn(new ProsecutionCaseView(List.of()));

        service.getDefendants(caseId);

        verify(casefileClient).getCaseById(caseId);
        verify(mapper).toProsecutionCaseView(casefile);
    }
}