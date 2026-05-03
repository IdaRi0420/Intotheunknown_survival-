Here's your **ITU_S Technical Specification v0.0.1** in your IDA spec format.

---

---
# IDA INDUSTRIES
## Into The Unknown: Survival
### Technical Specification v0.0.1

---

## Document Control

| Field | Value |
|-------|-------|
| **Document ID** | ITU-SPEC-001 |
| **Version** | 0.0.1 |
| **Status** | Draft |
| **Date** | 2026-05-04 |
| **Author** | IDA |
| **Contributor** | Akashi |
| **Classification** | Internal / Open Source |

---

## 1. Overview

### 1.1 Purpose

Into The Unknown: Survival (ITU_S) is a 2D top-down zombie survival game developed in LibGDX with Java 17. This document defines the technical architecture, domain boundaries, and integration contracts for the project.

### 1.2 Vision

A realistic survival game where **man is only as capable as their tools**. No leveling. No bullet sponges. Gear progression only. One bite kills. Bullets are indiscriminate.

### 1.3 Inspiration

| Source | Lesson |
|--------|--------|
| **Metro Exodus** | Atmosphere, ballistic realism, weapon feel |
| **World War Z** | Duct tape armor, swarm intensity |
| **Project Zomboid** | Inventory tetris, weight reduction, container system |
| **Real life** | Source of all stats and behavior |

### 1.4 Development Team

| Role | Owner | Responsibility |
|------|-------|----------------|
| **Combat/AI Domain** | IDA | Entities, factions, AI, damage system, inventory, controllers |
| **Items/Inventory Content** | Akashi | Weapons, armor, consumables, backpacks |
| **Integration** | Both | Integration tests, contract verification |

---

## 2. Project Philosophy

### 2.1 Core Pillars

| Pillar | Meaning |
|--------|---------|
| **Full realism** | Stats and behavior sourced from real life |
| **Man = tools** | Player stats frozen. Progression through gear only. |
| **Indiscriminate bullets** | No friendly fire toggle. Bullets hit what they hit. |
| **Health is armor** | Bite protection = arm guard layers. Duct tape = healing. |
| **Black Box Doctrine** | Sealed domains, contracts only, no internal access. |
| **Unix Philosophy** | Small, focused modules that plug together. |
| **Multiplayer from day one** | Possession pattern. Entity persists beyond controller. |

### 2.2 Anti-Pillars (What We Don't Do)

| Rejected | Why |
|----------|-----|
| Player leveling | Unrealistic. Tools improve, not the man. |
| Faction-checking bullets | Real bullets don't check ID. |
| Magic healing | Duct tape or nothing. |
| Bullet sponges | One headshot kills. One bite kills (without armor). |
| Deep inheritance chains | Flat > nested. Human/Infected only. |

---

## 3. Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 17 (Temurin) |
| Framework | LibGDX | Latest |
| Build System | Gradle | Wrapper |
| Version Control | Git | - |
| Remote | GitHub | - |
| IDE | Any (IntelliJ/VSCode) | - |

### 3.1 Run Command

```bash
.\gradlew lwjgl3:run
```

---

## 4. Architecture

### 4.1 High-Level View

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              CORE / GLUE                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────┐  │
│  │ GameOrchestrator│  │  LibGDXGame     │  │      InputHandler       │  │
│  │ (Game Loop)     │  │  (Application)  │  │  (Keyboard/Mouse)       │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                            VIEW (Rendering)                              │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────┐  │
│  │   Sprites       │  │   UI (Inventory,│  │   Effects (Muzzle Flash,│  │
│  │ (Entity Visals) │  │    Health Bar)  │  │    Blood, Bullet Trail) │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                            LOGIC (Pure Java)                             │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐                │
│  │   Entities    │  │   Controllers │  │   World       │                │
│  │ (Human,       │  │ (Player, AI,  │  │ (Game State,  │                │
│  │  Infected)    │  │  Null)        │  │  Spawning)    │                │
│  └───────────────┘  └───────────────┘  └───────────────┘                │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐                │
│  │   Inventory   │  │   Messages    │  │   AI Library  │                │
│  │ (Containers,  │  │ (HitReceiver, │  │ (WalkerAI,    │                │
│  │  Weight, Slot)│  │  DamageType)  │  │  TankAI, etc) │                │
│  └───────────────┘  └───────────────┘  └───────────────┘                │
│  ┌───────────────┐                                                      │
│  │   Items (IF)  │                                                      │
│  │ (Interface    │                                                      │
│  │  for Akashi)  │                                                      │
│  └───────────────┘                                                      │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        AKASHI ITEMS (Implementations)                    │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────┐  │
│  │    Weapons      │  │     Armor       │  │   Consumables           │  │
│  │ (AKM, Axe, Bow) │  │ (ArmGuard,      │  │ (DuctTape, Food, Water) │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
```

### 4.2 Separation Rules

| Layer | Can Import | Cannot Import |
|-------|------------|----------------|
| **logic/** | Java standard lib only | LibGDX, view/ |
| **view/** | logic/, LibGDX | core/ (except via orchestrator) |
| **core/** | logic/, view/, LibGDX | - |
| **akashi_items/** | logic/items/Item, logic/messages/ | logic/entity/, logic/inventory/ |

---

## 5. Entity Hierarchy

### 5.1 Inheritance Chain (Flat)

```
BaseEntity (lib/entity/)
├── Human (entities/human/)
│   ├── PlayerEntity
│   ├── BanditEntity
│   └── ScavengerEntity
└── Infected (entities/infected/)
    ├── InfectedWalkerEntity
    ├── BoomerEntity (extends InfectedWalkerEntity)
    ├── ViralEntity
    ├── JumperEntity
    ├── TankEntity
    └── EvolvedEntity
```

### 5.2 Entity Composition

Each entity is **self-contained** in its own folder:

```
entities/human/player/
├── PlayerEntity.java      (extends Human)
├── PlayerAI.java          (null — player controller)
├── PlayerSprite.java      (extends BaseSprite)
└── Player_help.txt

entities/infected/walker/
├── InfectedWalkerEntity.java (extends Infected)
├── InfectedWalkerAI.java     (extends BaseAI)
├── InfectedWalkerSprite.java (extends BaseSprite)
└── InfectedWalker_help.txt
```

### 5.3 Faction Enum

```java
// lib/entity/Faction.java
public enum Faction {
    HUMAN,
    INFECTED
}
```

No deeper factions. AI handles behavior differences (Bandit attacks Army, etc.).

---

## 6. Controller Possession Pattern

### 6.1 Concept

| Component | Role |
|-----------|------|
| **Entity** | The in-game character (health, position, inventory). Persists. |
| **Controller** | The driver (human or AI). Comes and goes. |

### 6.2 Controller Types

| Controller | Purpose |
|------------|---------|
| `PlayerController` | Human input (keyboard/mouse) |
| `AIController` | AI decision making (wraps entity's AI) |
| `NullController` | No controller (entity idle) |

### 6.3 Flow

```
1. Entity spawned with AIController (NPC behavior)
2. Player disconnects → PlayerController destroyed → Entity gets AIController
3. Player reconnects → New PlayerController possesses same entity
4. Entity dies → Controller relinquished → New entity for respawn
```

### 6.4 Multiplayer Ready

- Entity exists independently of any controller
- State can be synced over network (`EntityState` POJO)
- Controller ID maps to network connection

---

## 7. Inventory System (Project Zomboid Style)

### 7.1 Core Concepts

| Concept | Description |
|---------|-------------|
| **Slots, not just weight** | Items occupy grid cells (size matters) |
| **Container hierarchy** | Main inventory + optional backpack container |
| **Weight reduction** | Items in backpack have reduced weight (bag distributes load) |
| **Body slots** | Equipped items (chest, arms, back, hands) |
| **Every entity has inventory** | Even zombies (pockets with random loot) |

### 7.2 Inventory Components

| Class | Responsibility |
|-------|----------------|
| `Inventory` | Main container + body slots + backpack reference |
| `InventoryContainer` | Grid-based storage (width x height) |
| `BodySlot` | Enum (CHEST, ARMS, BACK, HANDS, LEGS) |
| `ItemStack` | Item + quantity + grid position |

### 7.3 Weight Rules

| State | Weight Calculation |
|-------|--------------------|
| Item in main inventory | Full weight |
| Item in backpack | Weight × backpack.multiplier (e.g., 0.7) |
| Item equipped | Full weight (on body, not carried) |

### 7.4 Example Capacities

| Entity | Main Inventory | Backpack | Body Slots |
|--------|---------------|----------|------------|
| Player | 5x5 (25 slots) | Optional (+8x10) | Chest, Arms, Back, Hands |
| Bandit | 4x4 (16 slots) | None | Chest, Hands |
| InfectedWalker | 2x3 (6 slots) | None | None (pockets only) |
| Tank | 3x3 (9 slots) | None | None (carries nothing) |

---

## 8. AI Library

### 8.1 Architecture

- **`BaseAI`** interface (lib/ai/)
- **Concrete AI implementations** live in each entity's folder
- AI can extend other AI (e.g., `BoomerAI extends InfectedWalkerAI`)

### 8.2 AI Interface

```java
public interface BaseAI {
    void update(BaseEntity entity, float delta, GameWorld world);
    void onDamage(BaseEntity entity, int damage, DamageType type);
    void onDeath(BaseEntity entity, GameWorld world);
}
```

### 8.3 AI Per Entity

| Entity | AI Class | Extends |
|--------|----------|---------|
| InfectedWalker | `InfectedWalkerAI` | `BaseAI` |
| Boomer | `BoomerAI` | `InfectedWalkerAI` (adds explode on death) |
| Viral | `ViralAI` | `BaseAI` (faster chasing) |
| Tank | `TankAI` | `BaseAI` (high health, smash) |
| Evolved | `EvolvedAI` | `BaseAI` (spawn minions, use guns) |
| Bandit | `BanditAI` | `BaseAI` (faction targeting) |

---

## 9. Messages (Cross-Domain Contracts)

### 9.1 Purpose

All communication between domains goes through `lib/messages/`.

### 9.2 Message Types

| Message | Sender | Receiver |
|---------|--------|----------|
| `HitReceiver.hit(damage, type)` | Bullet (Akashi) | Entity (IDA) |
| `DamageType` enum | Any | Any |
| `UseItemMessage` | PlayerController (IDA) | Inventory (IDA) |
| `EquipItemMessage` | PlayerController (IDA) | Inventory (IDA) |
| `DropLootMessage` | Entity (IDA) | World (IDA) |

### 9.3 HitReceiver Contract

```java
// lib/messages/HitReceiver.java
public interface HitReceiver {
    void hit(int damage, DamageType type);
}

// lib/messages/DamageType.java
public enum DamageType {
    BITE,      // Checks arm guard durability
    BULLET,    // Checks ballistic vest
    BLUNT,     // Direct health damage (fall, melee)
    EXPLOSION  // Area damage
}
```

**Akashi only needs this interface.** Nothing else.

---

## 10. File Structure

```
core/src/main/java/com/itus/
│
├── lib/                                    👈 BASE CLASSES (framework)
│   ├── entity/
│   │   ├── BaseEntity.java
│   │   ├── BaseEntity_help.txt
│   │   └── Faction.java
│   ├── ai/
│   │   ├── BaseAI.java
│   │   └── BaseAI_help.txt
│   ├── sprite/
│   │   ├── BaseSprite.java
│   │   └── BaseSprite_help.txt
│   ├── inventory/
│   │   ├── Inventory.java
│   │   ├── InventoryContainer.java
│   │   ├── BodySlot.java
│   │   └── Inventory_help.txt
│   ├── messages/
│   │   ├── HitReceiver.java
│   │   ├── DamageType.java
│   │   └── Messages_help.txt
│   └── items/
│       ├── Item.java                       👈 Interface only
│       └── Item_help.txt
│
├── entities/                               👈 SELF-CONTAINED ENTITIES
│   ├── human/
│   │   ├── Human.java                      👈 extends BaseEntity
│   │   ├── player/
│   │   │   ├── PlayerEntity.java
│   │   │   ├── PlayerAI.java
│   │   │   ├── PlayerSprite.java
│   │   │   └── Player_help.txt
│   │   ├── bandit/
│   │   │   ├── BanditEntity.java
│   │   │   ├── BanditAI.java
│   │   │   ├── BanditSprite.java
│   │   │   └── Bandit_help.txt
│   │   └── scavenger/
│   │       ├── ScavengerEntity.java
│   │       ├── ScavengerAI.java
│   │       ├── ScavengerSprite.java
│   │       └── Scavenger_help.txt
│   └── infected/
│       ├── Infected.java                   👈 extends BaseEntity
│       ├── walker/
│       │   ├── InfectedWalkerEntity.java
│       │   ├── InfectedWalkerAI.java
│       │   ├── InfectedWalkerSprite.java
│       │   └── InfectedWalker_help.txt
│       ├── boomer/
│       │   ├── BoomerEntity.java
│       │   ├── BoomerAI.java
│       │   ├── BoomerSprite.java
│       │   └── Boomer_help.txt
│       ├── runner/
│       │   ├── ViralEntity.java
│       │   ├── ViralAI.java
│       │   ├── ViralSprite.java
│       │   └── Viral_help.txt
│       ├── jumper/
│       │   ├── JumperEntity.java
│       │   ├── JumperAI.java
│       │   ├── JumperSprite.java
│       │   └── Jumper_help.txt
│       ├── tank/
│       │   ├── TankEntity.java
│       │   ├── TankAI.java
│       │   ├── TankSprite.java
│       │   └── Tank_help.txt
│       └── evolved/
│           ├── EvolvedEntity.java
│           ├── EvolvedAI.java
│           ├── EvolvedSprite.java
│           └── Evolved_help.txt
│
├── view/                                   👈 RENDERING (LibGDX)
│   ├── camera/
│   │   ├── GameCamera.java
│   │   └── Camera_help.txt
│   ├── ui/
│   │   ├── InventoryUI.java
│   │   ├── HealthBar.java
│   │   └── UI_help.txt
│   ├── effects/
│   │   ├── BulletTrail.java
│   │   ├── MuzzleFlash.java
│   │   └── Effects_help.txt
│   └── renderer/
│       ├── GameRenderer.java
│       └── Renderer_help.txt
│
├── core/                                   👈 GLUE
│   ├── GameOrchestrator.java
│   ├── LibGDXGame.java
│   ├── InputHandler.java
│   └── Core_help.txt
│
├── integration/                            👈 TESTS
│   ├── tests/
│   │   ├── BulletHitTest.java
│   │   ├── InventoryWeightTest.java
│   │   └── ControllerPossessionTest.java
│   └── Integration_help.txt
│
└── akashi_items/                           👈 AKASHI'S DOMAIN
    ├── weapons/
    │   ├── firearm/AKM.java
    │   ├── melee/Axe.java
    │   └── diy/Bow.java
    ├── armor/
    │   ├── ArmGuard.java
    │   └── BallisticVest.java
    ├── consumables/
    │   ├── DuctTape.java
    │   └── Food.java
    └── backpacks/
        ├── HikingBackpack.java
        └── MilitaryBackpack.java
```

---

## 11. Development Workflow

### 11.1 Daily Cycle

```bash
git pull
# Work in your domain only
./gradlew test          # Run integration tests before commit
git add .
git commit -m "message"
git push
```

### 11.2 Integration Test Rule

If integration test fails:
- IDA's test fails → IDA fixes
- Akashi's test fails → Akashi fixes
- Both review together

### 11.3 Crack Protocol

1. Message owner: "Need to crack [box name]"
2. Owner responds: "Go ahead" or "Let me look first"
3. Open box, fix only what's broken
4. Update `_help.txt` if public methods changed
5. Run integration tests
6. Push with `CRACK: [box name]` in commit message

**One crack at a time. No silent cracks.**

---

## 12. Version 0.1 Milestone

### 12.1 Scope (Minimum Viable Product)

| Component | Status |
|-----------|--------|
| Player moves (WASD) | Target |
| One infected type (Walker) | Target |
| Walker AI (follows player) | Target |
| One firearm (AKM) | Target |
| Bullet collision + hit() | Target |
| Player takes damage | Target |
| Arm guard (3 bite layers) | Target |
| Duct tape (repairs 1 layer) | Target |
| Basic inventory (5x5 grid) | Target |
| Integration test passes | Target |

### 12.2 Out of Scope for v0.1

- Runner / Large infected
- Ballistic vest
- Backpack container
- Hunger system
- Multiple weapons (just AKM)
- Stealth
- Network multiplayer

### 12.3 Target Date

2-3 weeks from project start.

---

## 13. Risk Register

| Risk | Probability | Mitigation |
|------|-------------|------------|
| Git merge conflicts | Medium | Separate domains, never same file |
| Help.txt drifts from code | Medium | Version headers + crack protocol |
| Friend loses motivation | Low | Clear ownership, no creative friction |
| LibGDX learning curve | Low (IDA) / Medium (Akashi) | Start with LibGDX demo |
| Integration test gaps | Medium | Add test for every contract |
| Scope creep | High | Strict v0.1 milestone |

---

## 14. Glossary

| Term | Definition |
|------|-------------|
| **Black Box** | Unit with defined contract, no internal visibility |
| **Contract** | Public methods + `_help.txt` |
| **Crack** | Opening a box to debug or modify |
| **Domain** | Owner's area (IDA or Akashi) |
| **Help.txt** | Human/AI readable contract documentation |
| **HitReceiver** | Primary cross-domain interface |
| **Integration test** | Test verifying contract between domains |
| **Possession** | Controller taking control of an entity |
| **Container** | Grid-based inventory storage (backpack, main inventory) |

---

## Appendix A: Quick Reference Card

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ITU_S v0.0.1 - QUICK REFERENCE                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  RUN:     .\gradlew lwjgl3:run                                          │
│  TEST:    .\gradlew test                                                │
│                                                                          │
│  DOMAINS:                                                               │
│    lib/           Base classes (Entity, AI, Sprite, Inventory)          │
│    entities/      Self-contained entities (player, bandit, infected)    │
│    akashi_items/  Akashi's item implementations                         │
│                                                                          │
│  CONTRACT:                                                              │
│    HitReceiver.hit(int damage, DamageType type)                         │
│                                                                          │
│  RULES:                                                                 │
│    • Logic has no LibGDX imports                                        │
│    • Entities self-contained in one folder                              │
│    • Help.txt beside every class                                        │
│    • One crack at a time                                                │
│    • Integration test must pass before push                             │
│                                                                          │
│  MILESTONE v0.1:                                                        │
│    Player + Walker + AKM + Bullet + ArmGuard + DuctTape                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Appendix B: First Week Plan

| Day | IDA Task | Akashi Task |
|-----|----------|-------------|
| 1 | LibGDX setup, moving player sprite | Study HitReceiver interface |
| 2 | Walker AI (follows player) | AKM.java + Bullet.java skeleton |
| 3 | Walker takes damage, dies | Bullet collision detection |
| 4 | Integration test (bullet hits player) | Fix bullet test together |
| 5 | Arm guard + bite damage | Duct tape item |
| 6 | Basic inventory (5x5 grid) | Item size/weight properties |
| 7 | First playable build | First playable build |

---

## Appendix C: Help.txt Template

```text
CLASSNAME.java
DOC_VERSION: YYYY-MM-DD-v1
OWNER: [IDA/Akashi]
BEHAVIOR SOURCE: Real life

WHAT I AM:
[One sentence description]

PUBLIC METHODS (contract):
- methodOne(param) -> [what it returns/does]
- methodTwo(param) -> [what it returns/does]

WHAT I NEED FROM OTHER DOMAIN:
- [Interface or method name]

WHAT I DO NOT KNOW:
- [What I shouldn't be aware of]

EXAMPLE:
[Code example showing usage]

OWNER CONTACT: [name]
```

---

## Appendix D: Example Item Implementation (Akashi)

```java
// akashi_items/weapons/firearm/AKM.java
package com.itus.akashi_items.weapons.firearm;

import com.itus.lib.items.Item;
import com.itus.lib.items.ItemType;

public class AKM implements Item {
    @Override
    public String getName() { return "AKM"; }
    
    @Override
    public float getBaseWeightKg() { return 4.3f; }
    
    @Override
    public int getWidth() { return 3; }
    
    @Override
    public int getHeight() { return 1; }
    
    @Override
    public ItemType getType() { return ItemType.FIREARM; }
    
    @Override
    public void onUse(Entity user) {
        // Spawn bullet at user's position toward aim direction
        Bullet bullet = new Bullet(user.getX(), user.getY(), user.getAimDirection());
        bullet.setDamage(34);
        user.getWorld().addProjectile(bullet);
    }
}
```

---

**END OF DOCUMENT**

---

*IDA Industries — "Onward to the unknown, inward to our fears, upward to our dreams."