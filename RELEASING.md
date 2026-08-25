# Release Procedure

Do not publish a Knot release until the Gradle wrapper is tracked and a clean checkout passes tests and produces the expected release artifact.

Before release:

1. Confirm application ID, namespace, version code, and version name.
2. Run unit, instrumentation, accessibility, and lifecycle tests on supported Android versions.
3. Validate Firebase configuration against the intended non-development project and confirm App Check enforcement.
4. Supply signing credentials through protected release infrastructure.
5. Enable and test release optimization rules as appropriate.
6. Review dependency and Android manifest changes.
7. Update `CHANGELOG.md` and record the artifact checksum.

Never commit the keystore, passwords, service accounts, or App Check debug tokens.
