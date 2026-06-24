# service-cp-crime-prosecution-case

HMCTS Common Platform crime prosecution case service — aggregates defendant and offence data from the prosecution casefile.

Built from [service-hmcts-crime-springboot-template](https://github.com/hmcts/service-hmcts-crime-springboot-template).
For template docs, build patterns, and upgrade guides see the [template README](https://github.com/hmcts/service-hmcts-crime-springboot-template/blob/main/README.md) and its [docs](https://github.com/hmcts/service-hmcts-crime-springboot-template/blob/main/docs).

## API contract

Consumes `uk.gov.hmcts.cp:api-cp-crime-prosecution-case:1.0.0` via the `apiSpec` configuration.

## Upstream / downstream

| Direction | Service | Notes |
|-----------|---------|-------|
| Calls | `prosecution-casefile-service` | Fetches case/defendant/offence data via vendor media types |
| Calls | `system-id-mapper-service` | Resolves case URN → internal case file ID |
| Called by | UI / API gateway consumers | Read-only aggregation (stateless, all GET) |

## Ownership

- **Team:** [api-marketplace](https://github.com/orgs/hmcts/teams/api-marketplace)
- **Support:** #cpp-crime-support
- **Domain package:** `uk.gov.hmcts.cp.prosecution.prosecutioncase`

## Running locally

```bash
./gradlew build
docker compose up
./gradlew bootRun
curl -i http://localhost:8082/actuator/health/readiness
```

Logs are JSON to stdout. Confirm `correlationId`/`requestId` fields appear in MDC.

## New team member setup

```bash
gh auth login
git clone git@github.com:hmcts/service-cp-crime-prosecution-case.git
cd service-cp-crime-prosecution-case
git checkout -b smoke/access-check
git commit --allow-empty -m "chore: verify push access"
git push -u origin smoke/access-check
git push origin --delete smoke/access-check
```

If the push is rejected, re-check team membership in [api-marketplace](https://github.com/orgs/hmcts/teams/api-marketplace).

## License

MIT — see [LICENSE](LICENSE).