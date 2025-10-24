# Changelog

## [1.20.1-1.9.22] - 2025-10-24
- Added debug traces around controller state changes so dimension locks, structure loss, energy shortages, and inventory stalls surface in logs immediately.
- Updated project metadata to `1.20.1-1.9.22` for this release build.

## [1.20.1-1.9.21] - 2025-10-20
- Added detailed miner diagnostics that record energy demand, inventory fullness, modifier multipliers, and recipe availability so players see the exact reason a Void Miner pauses.
- Prevent miners from endlessly retrying impossible cycles by detecting when modifier stacks push RF/t requirements beyond the internal buffer and resetting progress until power or modifiers are adjusted.
- Expanded the controller tooltip to surface dimension locks, energy deficits, blocked output stacks, and guidance for clearing each condition.
- Updated project metadata to `1.20.1-1.9.21` for this release build.

## [1.20.1-1.9.19] - 2025-09-24
- Added shaped crafting routes for the ultimate speed, energy, and item modifiers so the final tier stays obtainable without standalone crystals.
- Regenerated the data pack (recipes + advancements) to ship the new ultimate modifier unlocks.
- Updated the project version metadata to `1.20.1-1.9.19` for the release build.

## [1.20.1-1.9.18] - 2025-09-24
- Bump the mod version metadata to `1.20.1-1.9.18` for the new distribution build.
- Tighten the CurseForge README phrasing so the feature list is clearer and matches the latest version (commit 0eca8be).
- Correct JEI recipe display formatting to keep ingredients aligned on screen (commit 5ade9c5).
