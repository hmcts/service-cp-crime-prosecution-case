package uk.gov.hmcts.cp.prosecution.prosecutioncase.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.output.DefendantView;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileDefendant;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileOffence;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.model.response.casefile.CasefileResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProsecutionCasefileMapperTest {

    private final ProsecutionCasefileMapper mapper = new ProsecutionCasefileMapper();

    @Test
    void toDefendantViews_maps_name_and_offences() {
        CasefileOffence offence = new CasefileOffence("off-1", "TH68001", null, "Theft", null);
        CasefileDefendant defendant = new CasefileDefendant("def-1",
                new CasefileDefendant.PersonalInformation("Jane", "Doe"), List.of(offence));
        CasefileResponse casefile = new CasefileResponse("case-1", "22SW0001", null, null, List.of(defendant));

        List<DefendantView> result = mapper.toDefendantViews(casefile);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("def-1");
        assertThat(result.get(0).name()).isEqualTo("Jane Doe");
        assertThat(result.get(0).offences()).hasSize(1);
        assertThat(result.get(0).offences().get(0).id()).isEqualTo("off-1");
        assertThat(result.get(0).offences().get(0).code()).isEqualTo("TH68001");
        assertThat(result.get(0).offences().get(0).title()).isEqualTo("Theft");
        assertThat(result.get(0).offences().get(0).status()).isEqualTo("Active");
    }

    @Test
    void toDefendantViews_uses_offenceWording_when_title_is_null() {
        CasefileOffence offence = new CasefileOffence("off-2", "AA00001", "Wording text", null, "Guilty");
        CasefileDefendant defendant = new CasefileDefendant("def-2",
                new CasefileDefendant.PersonalInformation("John", "Smith"), List.of(offence));
        CasefileResponse casefile = new CasefileResponse("case-2", "22SW0002", null, null, List.of(defendant));

        List<DefendantView> result = mapper.toDefendantViews(casefile);

        assertThat(result.get(0).offences().get(0).title()).isEqualTo("Wording text");
        assertThat(result.get(0).offences().get(0).status()).isEqualTo("Guilty");
    }

    @Test
    void toDefendantViews_returns_empty_list_when_casefile_is_null() {
        assertThat(mapper.toDefendantViews(null)).isEmpty();
    }

    @Test
    void toDefendantViews_returns_empty_list_when_defendants_is_null() {
        CasefileResponse casefile = new CasefileResponse("case-3", null, null, null, null);
        assertThat(mapper.toDefendantViews(casefile)).isEmpty();
    }

    @Test
    void toDefendantViews_returns_empty_offences_when_offences_is_null() {
        CasefileDefendant defendant = new CasefileDefendant("def-3",
                new CasefileDefendant.PersonalInformation("Alice", "Brown"), null);
        CasefileResponse casefile = new CasefileResponse("case-4", null, null, null, List.of(defendant));

        List<DefendantView> result = mapper.toDefendantViews(casefile);

        assertThat(result.get(0).offences()).isEmpty();
    }

    @Test
    void toDefendantViews_handles_null_personal_information() {
        CasefileDefendant defendant = new CasefileDefendant("def-4", null, List.of());
        CasefileResponse casefile = new CasefileResponse("case-5", null, null, null, List.of(defendant));

        List<DefendantView> result = mapper.toDefendantViews(casefile);

        assertThat(result.get(0).name()).isNull();
    }
}