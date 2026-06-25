package uk.gov.hmcts.cp.prosecution.prosecutioncase.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.cp.prosecution.prosecutioncase.client.CaseUrnMapperClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseUrnMapperServiceTest {

    @Mock
    private CaseUrnMapperClient caseUrnMapperClient;

    @InjectMocks
    private CaseUrnMapperService service;

    @Test
    void getCaseId_returns_uuid_from_client() {
        UUID expected = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(caseUrnMapperClient.getCaseId("22SW0001")).thenReturn(expected);

        UUID result = service.getCaseId("22SW0001");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getCaseId_passes_urn_to_client() {
        UUID caseId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(caseUrnMapperClient.getCaseId("TESTURN")).thenReturn(caseId);

        service.getCaseId("TESTURN");

        verify(caseUrnMapperClient).getCaseId("TESTURN");
    }
}