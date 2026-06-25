# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and test commands

```bash
./gradlew build                          # compile, test, PMD, JAR
./gradlew test                           # all tests
./gradlew test --tests "uk.gov.hmcts.cp.SomeTest"          # single test class
./gradlew test --tests "uk.gov.hmcts.cp.SomeTest#methodName" # single test method
./gradlew pmdMain                        # PMD static analysis (main sources only; pmdTest is disabled)
./gradlew validateApiSpecVersions        # assert apiSpec dep uses a fixed X.Y.Z version
./gradlew bootRun                        # run locally on port 8082
```

Docker is required for a full local run: `docker compose up` before `./gradlew bootRun`.

## Architecture

**What this service does:** Stateless read-only aggregator. It resolves a case URN to an internal case file ID (via `system-id-mapper-service`), then fetches defendant and offence data (via `prosecution-casefile-service`), and returns a simplified view to the UI / API gateway.

**Request flow:**
```
GET /prosecution-casefile/cases/{caseURN}
  → CaseUrnResolver.resolve(urn)          # calls system-id-mapper-service → caseId
  → ProsecutionCasefileClient.getCaseByIdTyped(caseId)  # calls prosecution-casefile-service
  → map CasefileResponse → DefendantsView (controller-local record)
```

**Package layout:**
- `uk.gov.hmcts.cp` — application entry point (`Application.java`)
- `uk.gov.hmcts.cp.config` — `RestClientConfig` (named `RestClient` beans) and `ServiceProperties` (`@ConfigurationProperties(prefix="services")`)
- `uk.gov.hmcts.cp.prosecution.prosecutioncase` — all domain code:
  - `.controller` — `@RestController` classes
  - `.client` — `RestClient`-based HTTP clients (one per upstream service)
  - `.service` — business logic components (e.g. `CaseUrnResolver`)
  - `.model.response.*` — upstream response records (Jackson `@JsonIgnoreProperties(ignoreUnknown=true)`)
  - `.model.output` — outbound view records returned to callers

**Configuration:** Upstream base URLs come from env vars (`PROSECUTION_CASEFILE_BASE_URL`, `SYSTEM_ID_MAPPER_BASE_URL`) with localhost defaults for local dev. These are bound via `ServiceProperties` and injected into `RestClientConfig`.

**`apiSpec` Gradle configuration** (`uk.gov.hmcts.cp:api-cp-crime-prosecution-case`) is **version-validation only** — it is not on the compile classpath and generates no code. Implement the API contract directly.

## Coding conventions

- **Java 25 records** for all DTOs and response models. No Lombok on model classes.
- **Compiler flags** `-Xlint:unchecked -Werror` — any unchecked warning is a build failure.
- **PMD** is enforced on `src/main`. The ruleset (`.github/pmd-ruleset.xml`) allows underscores in JUnit 5 method names and excludes `ShortVariable`, `LongVariable`, `GuardLogStatement`, `PreserveStackTrace`, and a few others. See the file for the full list.

## Test conventions

| Test type | Annotation | Notes |
|-----------|-----------|-------|
| Controller slice | `@WebMvcTest(MyController.class)` | Only loads web layer; no context for `RestClientConfig` |
| Full context | `@SpringBootTest` + `@AutoConfigureMockMvc` | Import path: `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc` |
| Unit | `@ExtendWith(MockitoExtension.class)` | Plain Mockito, no Spring context |

- Inject MockMvc with `@Resource` (not `@Autowired`) — matches existing convention in `ActuatorIntegrationTest`.
- Use `@MockitoBean` (Spring Boot 4.x, `org.springframework.test.context.bean.override.mockito`) not the deprecated `@MockBean`.
- Add `@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")` to MockMvc test classes — MockMvc `andExpect()` calls count as assertions but PMD doesn't recognise them.
- `failFast = true` in test config — the suite stops on the first failure.

## Logging

Logs are JSON to stdout via `logstash-logback-encoder`. Use `logback.xml` (not `logback-spring.xml` — fragile with transitive `commons-logging` dependencies). OpenTelemetry auto-populates `traceId` and `spanId` into MDC. Additional fields can be added to MDC in a request filter. Test log output shape with a unit test like `JunitLoggingTest`.