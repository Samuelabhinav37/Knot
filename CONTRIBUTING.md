# Contributing to Knot

Contributions should keep the Android project reproducible, protect Firebase and signing material, and include verification appropriate to the affected feature.

## Workflow

1. Create a focused branch.
2. Make the smallest coherent change.
3. Run unit tests and build the debug application.
4. Exercise UI or Firebase changes against a non-production project.
5. Update documentation for configuration or behavior changes.

Do not commit keystores, signing passwords, service-account files, OAuth credentials, App Check debug tokens, or personal test data. Pull requests should describe devices or emulators tested and any accessibility or lifecycle considerations.

Security-sensitive findings should be reported using `SECURITY.md`.
