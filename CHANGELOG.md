# Changelog

## [1.20.1-1.9.25.8] - 2026-06-20
- Performance: Optimized Void Miner visibility checks (reduced per-tick block scans).
  - Checks now prefer cheap air/fluid tests before transparency tests, respect world build limits,
    use a per-column cache with TTL and event-based invalidation, and limit frequency via configurable intervals.
- Performance: Solar panels are more TPS-friendly: they now check the blocks above the solar panel controller
  and will stop producing energy when sky visibility is blocked.

## [1.20.1-1.9.25.7] - 2026-05-28
- Remove unused logging code and clean up ControllerDiagnosticsLogger

## [1.20.1-1.9.25.6] - 2026-05-26
- Inform players that Rubetine tier-1 miners and solar panels have no modifier slots

## [1.20.1-1.9.25.5] - 2026-05-26
- Added a tooltip for Rubetine Tier not accepting any modifiers.

## [1.20.1-1.9.25.4] - 2026-04-30
- Fixed Solar Panels from generating power even when they are not receiving sunlight

## [1.20.1-1.9.25.3] - 2026-03-22
- Fixed: Repaired and improved the preview and rendering logic for Void Miners and Solar Panels.
- Fixed: Solar panels can now transfer energy amounts larger than the Java int limit (Integer.MAX_VALUE) to neighboring energy handlers.

## [1.20.1-1.9.25.2] - 2026-01-03
- Fixed: Prevent multiple executions of miner and solar panel BlockEntities within the same world-tick when external mods (e.g. Torcherino, Block Booster) attempt to accelerate ticks.

## [1.20.1-1.9.25.1] - 2026-01-02
- Fixed: ja_jp and ru_ru localization files missing entries for Storage Upgrade items.

## [1.20.1-1.9.25] - 2026-01-01
- Added: Storage Upgrade T1 / T2 / T3 (Storage Upgrade Items).
  - Storage Upgrades increase the miner controller's maximum number of output slots: T1 = +3 slots, T2 = +9 slots, T3 = +27 slots.
  - Installation: Right-click the miner controller with an upgrade to apply it directly (the item is not placed into the controller's inventory).
  - Rules: Only one upgrade can be active; upgrades are not cumulative. Installing a higher-tier upgrade replaces a lower-tier upgrade and returns the previous upgrade to the player (the mod will first attempt to add it to the player's inventory, otherwise it will drop the item into the world).
  - Restrictions: A lower-tier upgrade cannot be applied to a miner that already has a higher-tier upgrade installed (the action will be rejected).
  - Updated project metadata to `1.20.1-1.9.25` for this release build.

## [1.20.1-1.9.24] - 2025-12-31
- Fixed the Void Miners not forming.
- Updated project metadata to `1.20.1-1.9.24` for this release build.

## [1.20.1-1.9.23] - 2025-10-24
- Added: Solar Panels (tiered solar controllers and crystals).
  - Solar Panels provide passive power to solar panel controllers; multiple tiers exist and scale in generation and recipe cost.
  - Recipes: Solar crystals and crystal blocks are craftable via shaped recipes; higher tier crystals require the previous tier plus additional premium materials.
- Updated project metadata to `1.20.1-1.9.23` for this release build.

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
