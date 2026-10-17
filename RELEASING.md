# Releasing the jlink Gradle plugins

## Version and artifacts

The root `gradle.properties` is the single source of truth for the release coordinates:

```properties
group=com.ryandens
version=0.9.0
```

Gradle applies these properties to the root project and its subprojects. The convention
plugin in `build-logic/` deliberately does not set a version. Do not put the project
version in `gradle/libs.versions.toml`; that catalog versions build dependencies.

All three implementation artifacts share the same version:

| Module | Published plugin IDs |
| --- | --- |
| `jlink-gradle` | `com.ryandens.jlink-jre`, `com.ryandens.jlink-application`, `com.ryandens.jlink-application-run`, `com.ryandens.jlink-application-distribution` |
| `jlink-jib` | `com.ryandens.jlink-jib` |
| `temurin-binaries-repository` | `com.ryandens.temurin-binaries-repository` |

## Prepare a release PR

1. Start from current `origin/main` with a clean working tree and confirm the fixes
   intended for the release have merged. Inspect the previous release and changes:

   ```sh
   gh release list --limit 5
   git fetch origin main --tags
   git log PREVIOUS_VERSION..origin/main --oneline
   git switch -c release/NEXT_VERSION origin/main
   ```

2. Set `version=NEXT_VERSION` in root `gradle.properties`. Use a numeric `MAJOR.MINOR.PATCH`
   version without a `v` prefix. Update this document's example and the versions of
   this repository's plugins in `README.md`. Leave third-party plugin versions alone.

3. Install JDK 25 and JDK 17. JDK 25 compiles the project; functional tests also
   launch JDK 17 toolchains. Verify the complete build and generate publication POMs:

   ```sh
   ./gradlew clean build generatePomFileForPluginMavenPublication \
     --no-build-cache --no-configuration-cache --no-scan
   ```

   If TestKit cannot discover an installed JDK, supply its installation path through
   the inherited environment (replace both paths with actual JDK homes):

   ```sh
   env 'ORG_GRADLE_PROJECT_org.gradle.java.installations.paths=/path/to/jdk17,/path/to/jdk25' \
     ./gradlew build --no-scan
   ```

   Inspect each module's `build/publications/pluginMaven/pom-default.xml`. All three
   must have the expected group and version; `jlink-jib` must depend on
   `com.ryandens:jlink-gradle:NEXT_VERSION`. This verifies the version that reaches
   consumers, rather than just the text in a build script.

4. Commit and push the release branch, open a PR, and wait for Linux, Windows, and
   wrapper-validation checks to pass. Merge the release PR before tagging. A version
   bump or a merged PR does not itself publish anything.

## Publish the merged commit

Publication requires an authorized maintainer's GitHub access and the repository's
existing `GRADLE_PUBLISH_KEY` and `GRADLE_PUBLISH_SECRET` Actions secrets. Do not copy
those credentials into files or run a local publication as part of routine preparation.

After the release PR has merged, fetch `main` and tag the exact merged release commit.
Substitute the actual version and commit SHA below; verify that the commit is on `main`
and its root `gradle.properties` contains the matching version before pushing the tag.

```sh
git fetch origin main --tags
git show RELEASE_COMMIT:gradle.properties
git merge-base --is-ancestor RELEASE_COMMIT origin/main
git tag -a NEXT_VERSION RELEASE_COMMIT -m "Release NEXT_VERSION"
git push origin refs/tags/NEXT_VERSION
```

The tag push triggers [Publish release](.github/workflows/release.yml). It:

1. Checks that the tag exactly matches the numeric version in root `gradle.properties`.
2. Installs JDKs 17 and 25 on a fresh Ubuntu runner.
3. Builds and tests all modules from source without restoring caches or using the
   build cache or configuration cache on the release path.
4. Runs `./gradlew publishPlugins` for all modules, with Portal credentials available
   only to the publication step.

Monitor the tag-triggered run, not just the PR checks:

```sh
gh run list --workflow release.yml --branch NEXT_VERSION
gh run watch RUN_ID --exit-status
```

## Verify and announce

After the publishing workflow succeeds, verify `NEXT_VERSION` is available on the
Gradle Plugin Portal for all six plugin IDs listed above. Version pages have the form
`https://plugins.gradle.org/plugin/PLUGIN_ID/NEXT_VERSION`. Also inspect the marker
POMs under `https://plugins.gradle.org/m2/` to confirm their implementation dependency
uses the same release version. Portal propagation can take a little time.

Then create the GitHub release against the existing tag, with a concise description
of the user-visible changes and the generated changelog:

```sh
gh release create NEXT_VERSION --verify-tag --title "NEXT_VERSION Release" \
  --generate-notes --latest
```

For 0.9.0, the main user-visible change restores `ALL-MODULE-PATH` on JDK 24+
distributions that include `jmods`, while preserving runtime-based linking on JDKs
without packaged modules. It also provides configuration guidance when a module path
is required. See [#187](https://github.com/ryandens/jlink-gradle-plugin/issues/187)
and [#227](https://github.com/ryandens/jlink-gradle-plugin/pull/227).

## If publication fails

Inspect the failed run with `gh run view RUN_ID --log-failed`. A tag/version mismatch
must be fixed before publication. Missing credentials need a repository maintainer to
repair the existing Actions secrets; never print or request their values in a PR.

Published Portal versions are immutable. Do not move a published tag or blindly rerun
a partially successful publication. Check which modules and plugin IDs were published
first. A retry before any publication is different from recovering a partially
published release; use a new version when already-published artifacts need changing.
Only call a release complete after both the Portal publication and GitHub release
have been verified.
