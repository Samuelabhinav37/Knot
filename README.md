# Knot

Knot is an Android application exploring an AI-assisted mobile experience with local persistence, Firebase services, and Jetpack Compose. The repository is currently under active development and should be treated as a development build rather than a production release.

## Technology

- Kotlin and Jetpack Compose
- Android Gradle Plugin with version catalogs
- Room for local persistence
- Firebase services, including App Check and AI integrations

## Prerequisites

- Android Studio with a compatible JDK
- Android SDK and an emulator or physical device
- A project-specific Firebase configuration for features that require Firebase

## Local development

Open the repository in Android Studio, allow Gradle synchronization to complete, and select the `app` run configuration. From a terminal, use the Gradle wrapper once it has been committed to the repository:

```sh
./gradlew test
./gradlew assembleDebug
```

On Windows, replace `./gradlew` with `gradlew.bat`.

## Configuration and secrets

Do not commit signing keys, keystore passwords, service-account credentials, App Check debug tokens, or private Firebase configuration. Release signing values are expected to be supplied through the build environment.

## Project status and limitations

- The Gradle wrapper is not yet tracked, so a clean clone is not currently reproducible.
- Continuous integration is a placeholder and does not build or test the application.
- Release minification and production signing require final validation.
- Package namespace and application identifier conventions require reconciliation.

These items should be resolved before distributing a release build.

## Verification

Before submitting a change, run unit tests and build the debug application. Changes involving Firebase should also be exercised against a non-production Firebase project.

## License

No license has been declared. Until one is added, reuse and redistribution rights are not granted.
