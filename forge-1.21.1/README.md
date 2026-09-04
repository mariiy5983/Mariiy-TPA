# Forge modules

ForgeGradle **6.x does not support Gradle 9+**.

| Module | Minecraft | Artifact name |
|--------|-----------|---------------|
| `forge-1.20.1` | 1.20.1 – 1.20.4 | `Mariiy-TPA-forge-1.20-*.jar` |
| `forge-1.21.1` | 1.21 – 1.21.1 | `Mariiy-TPA-forge-1.21-*.jar` |

## Build (JDK 21 + Gradle 8.12 recommended)

```bash
# only Forge 1.20.x
./gradlew -Pplatforms=forge-1.20.1 :forge-1.20.1:build

# only Forge 1.21.x
./gradlew -Pplatforms=forge-1.21.1 :forge-1.21.1:build
```

Point the wrapper at Gradle 8.12.1 when building Forge, or use a separate Gradle 8 install.
