package uk.gov.hmcts.cp.prosecution.prosecutioncase.controller;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.hmcts.cp.openapi.model.DefendantView;
import uk.gov.hmcts.cp.openapi.model.OffenceView;
import uk.gov.hmcts.cp.openapi.model.ProsecutionCaseView;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.service.CaseUrnMapperService;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.service.ProsecutionCaseService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProsecutionCasefileController.class)
class ProsecutionCasefileControllerTest {

    private static final UUID DEF_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OFF_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");

    @Resource
    private MockMvc mockMvc;

    @MockitoBean
    private CaseUrnMapperService caseUrnMapperService;

    @MockitoBean
    private ProsecutionCaseService prosecutionCaseService;

    @Test
    void getCaseByUrn_returns_defendants_with_offences() throws Exception {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        ProsecutionCaseView view = new ProsecutionCaseView(List.of(
                DefendantView.builder()
                        .id(DEF_ID)
                        .name("Jane Doe")
                        .offences(List.of(OffenceView.builder()
                                .id(OFF_ID)
                                .code("TH68001")
                                .title("Theft")
                                .status("Active")
                                .build()))
                        .build()));

        when(caseUrnMapperService.getCaseId("22SW0001")).thenReturn(caseId);
        when(prosecutionCaseService.getDefendants(caseId)).thenReturn(view);

        mockMvc.perform(get("/prosecution-case/cases/22SW0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defendants[0].id").value(DEF_ID.toString()))
                .andExpect(jsonPath("$.defendants[0].name").value("Jane Doe"))
                .andExpect(jsonPath("$.defendants[0].offences[0].id").value(OFF_ID.toString()))
                .andExpect(jsonPath("$.defendants[0].offences[0].code").value("TH68001"))
                .andExpect(jsonPath("$.defendants[0].offences[0].title").value("Theft"))
                .andExpect(jsonPath("$.defendants[0].offences[0].status").value("Active"));
    }

    @Test
    void getCaseByUrn_returns_404_when_urn_not_resolved() throws Exception {
        when(caseUrnMapperService.getCaseId("UNKNOWN"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No case ID mapping found for URN: UNKNOWN"));

        mockMvc.perform(get("/prosecution-case/cases/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCaseByUrn_returns_400_when_urn_contains_special_characters() throws Exception {
        mockMvc.perform(get("/prosecution-case/cases/22SW-001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCaseByUrn_returns_400_when_urn_exceeds_30_characters() throws Exception {
        mockMvc.perform(get("/prosecution-case/cases/" + "A".repeat(31)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCaseByUrn_returns_400_when_urn_contains_underscore() throws Exception {
        mockMvc.perform(get("/prosecution-case/cases/22SW_001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCaseByUrn_accepts_urn_at_30_character_boundary() throws Exception {
        String urn = "A".repeat(30);
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(caseUrnMapperService.getCaseId(urn)).thenReturn(caseId);
        when(prosecutionCaseService.getDefendants(caseId))
                .thenReturn(new ProsecutionCaseView(List.of()));

        mockMvc.perform(get("/prosecution-case/cases/" + urn))
                .andExpect(status().isOk());
    }

    @Test
    void getCaseByUrn_accepts_single_character_urn() throws Exception {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        when(caseUrnMapperService.getCaseId("A")).thenReturn(caseId);
        when(prosecutionCaseService.getDefendants(caseId))
                .thenReturn(new ProsecutionCaseView(List.of()));

        mockMvc.perform(get("/prosecution-case/cases/A"))
                .andExpect(status().isOk());
    }
}