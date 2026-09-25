# Version migration boundary

Shared gameplay code belongs in `versions/common`. Minecraft-version-specific
operations must go through `com.beigu.yunbeiuc.api`: GUI creation, rendering,
placeholder resolution, and Block/Item construction.

Each version module supplies implementations and installs them during its
initializer with `VersionServices.install(...)` and
`VersionServices.installPlaceholders(...)`. Do not import `ButtonWidget`,
`TextFieldWidget`, `DrawContext`, or version-specific constructors into new
shared code. Existing screens are migrated incrementally, one class at a time.
