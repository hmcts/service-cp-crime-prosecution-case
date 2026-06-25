package uk.gov.hmcts.cp.prosecution.prosecutioncase.model.output;

import java.util.List;

public record DefendantView(
        String id,
        String name,
        List<OffenceView> offences
) {}