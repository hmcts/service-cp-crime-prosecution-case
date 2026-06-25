package uk.gov.hmcts.cp.prosecution.prosecutioncase.integration;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.client.CaseUrnMapperClient;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.client.ProsecutionCasefileClient;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileDefendant;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileOffence;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileResponse;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ProsecutionCasefileControllerIntegrationTest {

    @Resource
    private MockMvc mockMvc;

    @MockitoBean
    private ProsecutionCasefileClient prosecutionCasefileClient;

    @MockitoBean
    private CaseUrnMapperClient caseUrnMapperClient;

    @Test
    void getCaseByUrn_returns_full_defendant_view_through_real_mapper() throws Exception {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        CasefileResponse casefile = new CasefileResponse(caseId.toString(), "22SW0001", null, null,
                List.of(new CasefileDefendant(
                        "def-1",
                        new CasefileDefendant.PersonalInformation("Jane", "Doe"),
                        List.of(new CasefileOffence("off-1", "TH68001", null, "Theft", null))
                )));

        when(caseUrnMapperClient.getCaseId("22SW0001")).thenReturn(caseId);
        when(prosecutionCasefileClient.getCaseById(caseId)).thenReturn(casefile);

        mockMvc.perform(get("/prosecution-case/cases/22SW0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defendants[0].id").value("def-1"))
                .andExpect(jsonPath("$.defendants[0].name").value("Jane Doe"))
                .andExpect(jsonPath("$.defendants[0].offences[0].id").value("off-1"))
                .andExpect(jsonPath("$.defendants[0].offences[0].code").value("TH68001"))
                .andExpect(jsonPath("$.defendants[0].offences[0].title").value("Theft"))
                .andExpect(jsonPath("$.defendants[0].offences[0].status").value("Active"));
    }

    @Test
    void getCaseByUrn_defaults_offence_status_to_active_when_plea_is_null() throws Exception {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        CasefileResponse casefile = new CasefileResponse(caseId.toString(), "22SW0002", null, null,
                List.of(new CasefileDefendant(
                        "def-2",
                        new CasefileDefendant.PersonalInformation("John", "Smith"),
                        List.of(new CasefileOffence("off-2", "TH68002", null, "Burglary", null))
                )));

        when(caseUrnMapperClient.getCaseId("22SW0002")).thenReturn(caseId);
        when(prosecutionCasefileClient.getCaseById(caseId)).thenReturn(casefile);

        mockMvc.perform(get("/prosecution-case/cases/22SW0002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defendants[0].offences[0].status").value("Active"));
    }

    @Test
    void getCaseByUrn_uses_plea_as_status_when_present() throws Exception {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        CasefileResponse casefile = new CasefileResponse(caseId.toString(), "22SW0003", null, null,
                List.of(new CasefileDefendant(
                        "def-3",
                        new CasefileDefendant.PersonalInformation("Alice", "Jones"),
                        List.of(new CasefileOffence("off-3", "TH68003", null, "Theft", "Guilty"))
                )));

        when(caseUrnMapperClient.getCaseId("22SW0003")).thenReturn(caseId);
        when(prosecutionCasefileClient.getCaseById(caseId)).thenReturn(casefile);

        mockMvc.perform(get("/prosecution-case/cases/22SW0003"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defendants[0].offences[0].status").value("Guilty"));
    }

    @Test
    void getCaseByUrn_falls_back_to_offence_wording_when_title_is_null() throws Exception {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000004");
        CasefileResponse casefile = new CasefileResponse(caseId.toString(), "22SW0004", null, null,
                List.of(new CasefileDefendant(
                        "def-4",
                        new CasefileDefendant.PersonalInformation("Bob", "Brown"),
                        List.of(new CasefileOffence("off-4", "TH68004", "Wording only", null, null))
                )));

        when(caseUrnMapperClient.getCaseId("22SW0004")).thenReturn(caseId);
        when(prosecutionCasefileClient.getCaseById(caseId)).thenReturn(casefile);

        mockMvc.perform(get("/prosecution-case/cases/22SW0004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defendants[0].offences[0].title").value("Wording only"));
    }

    @Test
    void getCaseByUrn_returns_empty_defendants_when_casefile_defendants_is_null() throws Exception {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000005");
        CasefileResponse casefile = new CasefileResponse(caseId.toString(), "22SW0005", null, null, null);

        when(caseUrnMapperClient.getCaseId("22SW0005")).thenReturn(caseId);
        when(prosecutionCasefileClient.getCaseById(caseId)).thenReturn(casefile);

        mockMvc.perform(get("/prosecution-case/cases/22SW0005"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defendants").isArray())
                .andExpect(jsonPath("$.defendants").isEmpty());
    }

    @Test
    void getCaseByUrn_returns_404_when_urn_mapper_client_throws_not_found() throws Exception {
        when(caseUrnMapperClient.getCaseId("NOTFOUND"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No mapping for URN"));

        mockMvc.perform(get("/prosecution-case/cases/NOTFOUND"))
                .andExpect(status().isNotFound());
    }
}