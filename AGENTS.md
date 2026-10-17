# Working in this repository

- Build and test with the checked-in Gradle wrapper. The build compiles with JDK 25;
  functional tests also require JDK 17. CI installs both (and JDK 21 for validation).
- Run `./gradlew build --no-scan` before proposing changes. This includes unit tests,
  functional tests, formatting checks, and plugin validation for all three modules.
- Shared build configuration lives in `build-logic/`. Keep release coordinates in
  the root `gradle.properties`: `group` and `version` apply to all published modules.
  Do not hardcode a project version in convention plugins or subproject scripts.

# Releases

Read [RELEASING.md](RELEASING.md) before preparing or publishing a release.
Release preparation goes through a PR. A numeric version tag pushed to the merged
commit triggers Plugin Portal publication for all three modules and six plugin IDs.
A GitHub release alone does not publish the Gradle plugins. Verify the publishing
workflow and all six plugin versions before reporting a release as complete.
