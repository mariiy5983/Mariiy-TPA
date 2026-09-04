# Contributing

1. Fork & clone
2. Use JDK 21+
3. Prefer changes in `common/` for behavior; keep platform modules as thin adapters
4. Build at least `:paper:shadowJar` before opening a PR
5. Keep chat UX: non-blocking clickable chat, immediate teleport on accept

## Module tips

- **Paper**: widest server coverage; keep using Adventure click events
- **Fabric / NeoForge / Forge**: pin a Minecraft version per release; bump mappings carefully
- Do not commit `run/`, `.gradle/`, or large downloaded artifacts
