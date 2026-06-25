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

    private static final UUID DEF_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OFF_1 = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID DEF_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID OFF_2 = UUID.fromString("00000000-0000-0000-0000-000000000022");
    private static final UUID DEF_3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID DEF_4 = UUID.fromString("00000000-0000-0000-0000-000000000004");

    private final ProsecutionCasefileMapper mapper = new ProsecutionCasefileMapper();

    @Test
    void toProsecutionCaseView_maps_name_and_offences() {
        CasefileOffence offence = new CasefileOffence(OFF_1.toString(), "TH68001", null, "Theft", null);
        CasefileDefendant defendant = new CasefileDefendant(DEF_1.toString(),
                new CasefileDefendant.PersonalInformation("Jane", "Doe"), List.of(offence));
        CasefileResponse casefile = new CasefileResponse("case-1", "22SW0001", null, null, List.of(defendant));

        ProsecutionCaseView result = mapper.toProsecutionCaseView(casefile);

        assertThat(result.getDefendants()).hasSize(1);
        assertThat(result.getDefendants().getFirst().getId()).isEqualTo(DEF_1);
        assertThat(result.getDefendants().getFirst().getName()).isEqualTo("Jane Doe");
        assertThat(result.getDefendants().getFirst().getOffences()).hasSize(1);
        assertThat(result.getDefendants().getFirst().getOffences().getFirst().getId()).isEqualTo(OFF_1);
        assertThat(result.getDefendants().getFirst().getOffences().getFirst().getCode()).isEqualTo("TH68001");
        assertThat(result.getDefendants().getFirst().getOffences().getFirst().getTitle()).isEqualTo("Theft");
        assertThat(result.getDefendants().getFirst().getOffences().getFirst().getStatus()).isEqualTo("Active");
    }

    @Test
    void toProsecutionCaseView_uses_offenceWording_when_title_is_null() {
        CasefileOffence offence = new CasefileOffence(OFF_2.toString(), "AA00001", "Wording text", null, "Guilty");
        CasefileDefendant defendant = new CasefileDefendant(DEF_2.toString(),
                new CasefileDefendant.PersonalInformation("John", "Smith"), List.of(offence));
        CasefileResponse casefile = new CasefileResponse("case-2", "22SW0002", null, null, List.of(defendant));

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
        CasefileResponse casefile = new CasefileResponse("case-3", null, null, null, null);
        assertThat(mapper.toProsecutionCaseView(casefile).getDefendants()).isEmpty();
    }

    @Test
    void toProsecutionCaseView_returns_empty_offences_when_offences_is_null() {
        CasefileDefendant defendant = new CasefileDefendant(DEF_3.toString(),
                new CasefileDefendant.PersonalInformation("Alice", "Brown"), null);
        CasefileResponse casefile = new CasefileResponse("case-4", null, null, null, List.of(defendant));

        ProsecutionCaseView result = mapper.toProsecutionCaseView(casefile);

        assertThat(result.getDefendants().get(0).getOffences()).isEmpty();
    }

    @Test
    void toProsecutionCaseView_handles_null_personal_information() {
        CasefileDefendant defendant = new CasefileDefendant(DEF_4.toString(), null, List.of());
        CasefileResponse casefile = new CasefileResponse("case-5", null, null, null, List.of(defendant));

        ProsecutionCaseView result = mapper.toProsecutionCaseView(casefile);

        assertThat(result.getDefendants().get(0).getName()).isNull();
    }
}