package uk.gov.hmcts.cp.prosecution.prosecutioncase.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.cp.openapi.model.ProsecutionCaseView;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileDefendant;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileOffence;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileResponse;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProsecutionCasefileMapperTest {

    private static final UUID DEFENDANT_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OFFENCE_1 = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID DEFENDANT_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID OFFENCE_2 = UUID.fromString("00000000-0000-0000-0000-000000000022");
    private static final UUID DEFENDANT_3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID DEFENDANT_4 = UUID.fromString("00000000-0000-0000-0000-000000000004");

    private final ProsecutionCasefileMapper mapper = new ProsecutionCasefileMapper();

    @Test
    void toProsecutionCaseView_maps_name_and_offences() {
        CasefileOffence offence = CasefileOffence.builder()
                .offenceId(OFFENCE_1.toString())
                .offenceCode("TH68001")
                .offenceTitle("Theft")
                .build();
        CasefileDefendant defendant = CasefileDefendant.builder()
                .defendantId(DEFENDANT_1.toString())
                .personalInformation(new CasefileDefendant.PersonalInformation("Jane", "Doe"))
                .offences(List.of(offence))
                .build();
        CasefileResponse casefile = CasefileResponse.builder()
                .caseId("case-1")
                .urn("22SW0001")
                .defendants(List.of(defendant))
                .build();

        ProsecutionCaseView result = mapper.toProsecutionCaseView(casefile);

        assertThat(result.getDefendants()).hasSize(1);
        assertThat(result.getDefendants().getFirst().getId()).isEqualTo(DEFENDANT_1);
        assertThat(result.getDefendants().getFirst().getName()).isEqualTo("Jane Doe");
        assertThat(result.getDefendants().getFirst().getOffences()).hasSize(1);
        assertThat(result.getDefendants().getFirst().getOffences().getFirst().getId()).isEqualTo(OFFENCE_1);
        assertThat(result.getDefendants().getFirst().getOffences().getFirst().getCode()).isEqualTo("TH68001");
        assertThat(result.getDefendants().getFirst().getOffences().getFirst().getTitle()).isEqualTo("Theft");
        assertThat(result.getDefendants().getFirst().getOffences().getFirst().getStatus()).isEqualTo("Active");
    }

    @Test
    void toProsecutionCaseView_uses_offenceWording_when_title_is_null() {
        CasefileOffence offence = CasefileOffence.builder()
                .offenceId(OFFENCE_2.toString())
                .offenceCode("AA00001")
                .offenceWording("Wording text")
                .plea("Guilty")
                .build();
        CasefileDefendant defendant = CasefileDefendant.builder()
                .defendantId(DEFENDANT_2.toString())
                .personalInformation(new CasefileDefendant.PersonalInformation("John", "Smith"))
                .offences(List.of(offence))
                .build();
        CasefileResponse casefile = CasefileResponse.builder()
                .caseId("case-2")
                .urn("22SW0002")
                .defendants(List.of(defendant))
                .build();

        ProsecutionCaseView result = mapper.toProsecutionCaseView(casefile);

        assertThat(result.getDefendants().getFirst().getOffences().getFirst().getTitle()).isEqualTo("Wording text");
        assertThat(result.getDefendants().getFirst().getOffences().getFirst().getStatus()).isEqualTo("Guilty");
    }

    @Test
    void toProsecutionCaseView_returns_empty_defendants_when_casefile_is_null() {
        assertThat(mapper.toProsecutionCaseView(null).getDefendants()).isEmpty();
    }

    @Test
    void toProsecutionCaseView_returns_empty_defendants_when_defendants_is_null() {
        CasefileResponse casefile = CasefileResponse.builder().caseId("case-3").build();
        assertThat(mapper.toProsecutionCaseView(casefile).getDefendants()).isEmpty();
    }

    @Test
    void toProsecutionCaseView_returns_empty_offences_when_offences_is_null() {
        CasefileDefendant defendant = CasefileDefendant.builder()
                .defendantId(DEFENDANT_3.toString())
                .personalInformation(new CasefileDefendant.PersonalInformation("Alice", "Brown"))
                .build();
        CasefileResponse casefile = CasefileResponse.builder()
                .caseId("case-4")
                .defendants(List.of(defendant))
                .build();

        ProsecutionCaseView result = mapper.toProsecutionCaseView(casefile);

        assertThat(result.getDefendants().get(0).getOffences()).isEmpty();
    }

    @Test
    void toProsecutionCaseView_handles_null_personal_information() {
        CasefileDefendant defendant = CasefileDefendant.builder()
                .defendantId(DEFENDANT_4.toString())
                .offences(List.of())
                .build();
        CasefileResponse casefile = CasefileResponse.builder()
                .caseId("case-5")
                .defendants(List.of(defendant))
                .build();

        ProsecutionCaseView result = mapper.toProsecutionCaseView(casefile);

        assertThat(result.getDefendants().get(0).getName()).isNull();
    }
}