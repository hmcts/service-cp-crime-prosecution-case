package uk.gov.hmcts.cp.prosecution.prosecutioncase.model.output;

public record OffenceView(
        String id,
        String code,
        String title,
        String status
) {}