# 🌌 VoidMiners Reforged

**Extract infinite resources from the void dimension!**

VoidMiners Reforged is a comprehensive tech mod that allows you to generate resources from the void using advanced multiblock miners and solar panels. Perfect for skyblock modpacks or any survival world where resources are scarce!

---

## 📖 Table of Contents
- [Features](#-features)
- [Getting Started](#-getting-started)
- [Void Miners](#-void-miners)
- [Solar Panels](#-solar-panels)
- [Modifiers](#-modifiers)
- [Multiblock Structures](#-multiblock-structures)
- [Configuration](#-configuration)
- [Tips & Tricks](#-tips--tricks)

---

## ✨ Features

- **9 Tiers of Void Miners** - From basic Rubetine to the powerful Ultimate tier
- **9 Tiers of Solar Panels** - Generate RF from sunlight with customizable multiblock structures
- **Modifier System** - Enhance your machines with Speed, Energy, and Item modifiers
- **Void Dimension** - A special dimension optimized for resource generation
- **Fully Configurable** - Customize energy consumption, generation rates, and loot tables
- **Multiblock Flexibility** - Build structures from 3x3x3 up to 11x11x11

---

## 🚀 Getting Started

### Creating Your First Void Miner

1. **Craft a Controller Block** (e.g., Rubetine Miner Controller)
2. **Craft Frame Blocks** (minimum 26 for a 3x3x3 structure)
3. **Build the Structure:**
   - Place the Controller in the center
   - Surround it with Frame blocks in a cube pattern
   - Leave the interior hollow
4. **Power it up** with RF/FE
5. **Watch resources generate** in the internal inventory!

### Accessing the Void Dimension

1. **Craft a Void Miner Teleporter**
2. **Right-click** to teleport to the Void
3. **Build your mining setup** in the void for optimal performance

---

## ⛏️ Void Miners

Void Miners extract resources from nothingness, consuming RF to generate items based on configured loot tables.

### Miner Tiers & Stats

| Tier | Energy Storage | Energy/Tick | Base Duration | Color |
|------|---------------|-------------|---------------|--------|
| **Rubetine** | 10M RF | 300 RF/t | 1000 ticks | 🔴 Red |
| **Aurantium** | 25M RF | 350 RF/t | 900 ticks | 🟠 Orange |
| **Citrinetine** | 50M RF | 400 RF/t | 800 ticks | 🟡 Yellow |
| **Verdium** | 100M RF | 450 RF/t | 700 ticks | 🟢 Green |
| **Azurine** | 250M RF | 500 RF/t | 600 ticks | 🔵 Blue |
| **Caerium** | 500M RF | 550 RF/t | 500 ticks | 🟦 Dark Blue |
| **Amethystine** | 750M RF | 600 RF/t | 400 ticks | 🟣 Purple |
| **Rosarium** | 1B RF | 650 RF/t | 300 ticks | 🩷 Pink |
| **Ultimate** | 2.14B RF | 700 RF/t | 200 ticks | ⚫ Dark Red |

### Structure Sizes

- **Minimum:** 3x3x3 (26 frames)
- **Maximum:** 11x11x11 (1330 frames)
- **Larger structures** = More resources per operation!

---

## ☀️ Solar Panels

Generate RF from sunlight! Solar panels work in any dimension but are most efficient in clear weather during daytime.

### Solar Panel Tiers & Stats

| Tier | Energy Storage | Generation | Duration | Weather Resistance |
|------|---------------|------------|----------|-------------------|
| **Rubetine** | 5M RF | 20 RF/t | 100 ticks | Low |
| **Aurantium** | 10M RF | 40 RF/t | 80 ticks | Low |
| **Citrinetine** | 20M RF | 80 RF/t | 60 ticks | Medium |
| **Verdium** | 40M RF | 160 RF/t | 50 ticks | Medium |
| **Azurine** | 80M RF | 320 RF/t | 40 ticks | Medium |
| **Caerium** | 160M RF | 640 RF/t | 30 ticks | High |
| **Amethystine** | 320M RF | 1,280 RF/t | 25 ticks | High |
| **Rosarium** | 640M RF | 2,560 RF/t | 20 ticks | Very High |
| **Ultimate** | 2.14B RF | 5,120 RF/t | 15 ticks | Maximum |

### Solar Efficiency Factors

- **☀️ Daytime:** 100% efficiency at noon
- **🌙 Nighttime:** 0% efficiency
- **🌧️ Rain:** 30% efficiency (without modifiers)
- **⛈️ Thunderstorm:** 15% efficiency (without modifiers)
- **🌫️ Void Dimension:** Works perfectly with clear sky above!

---

## 🔧 Modifiers

Enhance your machines with powerful modifiers! Each tier has its own set of modifiers.

### Void Miner Modifiers

| Modifier | Effect | Trade-off |
|----------|--------|-----------|
| **Speed Modifier** | -5% operation time | +10% energy consumption |
| **Energy Modifier** | -10% energy consumption | No speed change |
| **Item Modifier** | +75% item output | +20% energy consumption |

### Solar Panel Modifiers

| Modifier | Effect on Generation | Special Effect |
|----------|---------------------|----------------|
| **Efficiency Modifier** | +50% RF generation | Boosts overall output |
| **Output Modifier** | +50% RF generation | Stacks with Efficiency! |
| **Weather Modifier** | No direct boost | 2.5x weather resistance |

**Pro Tip:** Solar modifiers stack multiplicatively! Using both Efficiency and Output gives 2.25x generation!

---

## 🏗️ Multiblock Structures

### Basic Void Miner (3x3x3)
```
Layer 1 (Bottom):    Layer 2 (Middle):    Layer 3 (Top):
F F F                F   F                F F F
F F F                  C                  F F F
F F F                F   F                F F F

F = Frame Block
C = Controller Block
```

### With Modifiers (3x3x3)
```
Layer 1:             Layer 2:             Layer 3:
F F F                F M F                F F F
F F F                M C M                F F F
F F F                F M F                F F F

M = Modifier Block (Speed/Energy/Item)
```

### Solar Panel Structure (5x5x3)
```
Layer 1 (Base):           Layer 2:
F F F F F                 F M M M F
F F F F F                 M   C   M
F F F F F                 M       M
F F F F F                 M   P   M
F F F F F                 F M M M F

Layer 3 (Top - Solar Panels):
P P P P P
P P P P P
P P P P P
P P P P P
P P P P P

F = Solar Frame
C = Solar Controller
M = Modifier (Efficiency/Output/Weather)
P = Glass Panel (for solar collection)
```

---

## ⚙️ Configuration

All aspects of the mod are configurable via the `config/void-miners.json5` file:

- **Energy consumption rates**
- **Generation speeds**
- **Buffer sizes**
- **Modifier effectiveness**
- **Loot tables** (via datapack)

### Custom Loot Tables

Create custom loot tables in:
`data/voidminers/loot_tables/gameplay/`

Example tiers:
- `tier_1.json` → Rubetine
- `tier_2.json` → Aurantium
- `tier_9.json` → Ultimate

---

## 💡 Tips & Tricks

### Maximizing Void Miner Output
1. **Build larger structures** - More frames = more items per operation
2. **Use Item Modifiers** - +75% output for only +20% energy
3. **Place in Void Dimension** - No obstructions, perfect for automation
4. **Chain multiple tiers** - Use lower tiers for common resources, higher for rare

### Optimizing Solar Panels
1. **Stack modifiers** - Efficiency + Output = 2.25x generation!
2. **Use Weather Modifiers** in rainy biomes - Maintains 75% efficiency in rain
3. **Build in Void Dimension** - No weather, always clear sky
4. **Create solar farms** - Multiple panels for consistent power

### Power Management
- **Solar panels** for passive generation during day
- **Store excess** in external batteries for night use
- **Buffer drains fast** - This is intentional! Plan accordingly
- **Use Energy Modifiers** on miners to reduce consumption

### Automation Tips
- Miners have **built-in inventory** - Connect pipes/hoppers
- Solar panels **output from any side**
- Use **redstone control** for on-demand operation
- **Void dimension** is perfect for lag-free mega-bases

---

## 🎮 Progression Guide

### Early Game (Rubetine → Citrinetine)
- Start with basic 3x3x3 structures
- Focus on power generation infrastructure
- Use Speed modifiers for faster resource gathering

### Mid Game (Verdium → Caerium)
- Expand to 5x5x5 or 7x7x7 structures
- Add Item modifiers for increased output
- Set up solar panel arrays for sustainable power

### Late Game (Amethystine → Ultimate)
- Build maximum size structures (11x11x11)
- Combine all modifier types
- Create dedicated void dimension mining facilities
- Achieve infinite resource generation!

---

## 🔄 Version Compatibility

- **Minecraft:** 1.20.1
- **Forge:** 47.4.1+
- **Dependencies:** Forge only

---

## 📝 Credits

- **Original Mod:** VoidMiners by MtcLeo05
- **Reforged Version:** Enhanced with new features and optimizations

---

## 🐛 Bug Reports & Suggestions

Found a bug or have a suggestion? Report it on our [GitHub Issues](https://github.com/PsyGuy007-sys/VoidMiners-Reforged/issues)

---

**Enjoy extracting infinite resources from the void!** 🌌⛏️