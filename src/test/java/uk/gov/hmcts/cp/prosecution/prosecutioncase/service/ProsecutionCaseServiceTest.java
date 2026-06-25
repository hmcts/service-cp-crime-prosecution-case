
package uk.gov.hmcts.cp.prosecution.prosecutioncase.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.client.ProsecutionCasefileClient;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.mapper.ProsecutionCasefileMapper;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.output.DefendantView;
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
        CasefileResponse casefile = new CasefileResponse("00000000-0000-0000-0000-000000000001",
                "22SW0001", null, null, List.of());
        List<DefendantView> defendants = List.of(new DefendantView("d1", "Jane Doe", List.of()));

        when(casefileClient.getCaseById(caseId)).thenReturn(casefile);
        when(mapper.toDefendantViews(casefile)).thenReturn(defendants);

        ProsecutionCaseService.DefendantsView result = service.getDefendants(caseId);

        assertThat(result.defendants()).isEqualTo(defendants);
    }

    @Test
    void getDefendants_passes_caseId_to_client() {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        CasefileResponse casefile = new CasefileResponse(caseId.toString(), null, null, null, null);

        when(casefileClient.getCaseById(caseId)).thenReturn(casefile);
        when(mapper.toDefendantViews(casefile)).thenReturn(List.of());

        service.getDefendants(caseId);

        verify(casefileClient).getCaseById(caseId);
        verify(mapper).toDefendantViews(casefile);
    }

    @Test
    void getDefendants_wraps_mapped_defendants_in_view() {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        CasefileResponse casefile = new CasefileResponse(caseId.toString(), null, null, null, null);
        DefendantView d1 = new DefendantView("id1", "Alice Smith", List.of());
        DefendantView d2 = new DefendantView("id2", "Bob Jones", List.of());

        when(casefileClient.getCaseById(caseId)).thenReturn(casefile);
        when(mapper.toDefendantViews(casefile)).thenReturn(List.of(d1, d2));

        ProsecutionCaseService.DefendantsView result = service.getDefendants(caseId);

        assertThat(result.defendants()).containsExactly(d1, d2);
    }
}