
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
| **Date** | 2026-04-23 |
| **Author** | IDA |
| **Contributor** | Akashi |
| **Classification** | Internal / Open Source |

---

## Table of Contents

1. Overview
2. Project Philosophy
3. Technology Stack
4. Domain Architecture
5. Black Box Contracts
6. File Structure
7. Development Workflow
8. Integration Testing Protocol
9. Documentation Standard
10. Version 0.1 Milestone
11. Risk Register
12. Glossary

---

## 1. Overview

### 1.1 Purpose

Into The Unknown: Survival (ITU_S) is a 2D top-down zombie survival game developed in LibGDX with Java 17. This document defines the technical architecture, domain boundaries, and integration contracts for the project.

### 1.2 Vision

A realistic survival game where man is only as capable as their tools. No leveling. No bullet sponges. Gear progression only. One bite kills. Bullets are indiscriminate.

### 1.3 Inspiration

- Metro Exodus (atmosphere, ballistic realism, weapon feel)
- World War Z (duct tape armor, swarm intensity)
- Real life (source of all stats and behavior)

### 1.4 Development Team

| Role | Owner | Responsibility |
|------|-------|----------------|
| Combat/AI Domain | IDA | Player, infected, damage system, AI behavior |
| Items/Inventory Domain | Akashi | Weapons, armor, equipment, survival items |
| Integration | Both | Integration tests, contract verification |

---

## 2. Project Philosophy

### 2.1 Core Pillars

| Pillar | Meaning |
|--------|---------|
| Full realism | Stats and behavior sourced from real life |
| Man = tools | Player stats frozen. Progression through gear only. |
| Indiscriminate bullets | No friendly fire toggle. Bullets hit what they hit. |
| Health is armor | Bite protection = arm guard layers. Duct tape = healing. |
| Black Box Doctrine | Sealed domains, contracts only, no internal access. |

### 2.2 Anti-Pillars (What We Don't Do)

| Rejected | Why |
|----------|-----|
| Player leveling | Unrealistic. Tools improve, not the man. |
| Faction-checking bullets | Real bullets don't check ID. |
| Magic healing | Duct tape or nothing. |
| Bullet sponges | One headshot kills. One bite kills (without armor). |

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

## 4. Domain Architecture

### 4.1 Domain Ownership

```
┌─────────────────────────────────────────────────────────────┐
│                         IDA DOMAIN                          │
│  (Combat, AI, Player, Infected, Damage System)              │
│                                                             │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐         │
│  │ Player  │  │ Walker  │  │ Runner  │  │ Large   │         │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘         │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ HitReceiver (interface) → hit(damage, type)         │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ Contract
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       AKASHI DOMAIN                         │
│  (Items, Inventory, Weapons, Armor, Equipment)              │
│                                                             │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐         │
│  │ Firearm │  │ Melee   │  │ Armor   │  │ Backpack│         │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘         │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ Bullet → calls hit() on whatever it touches         │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 Separation Rule

```
NO cross-domain imports except:
- HitReceiver interface
- DamageType enum
- Public getters documented in help.txt
```

---

## 5. Black Box Contracts

### 5.1 Primary Contract: HitReceiver

```java
package com.itus.your_domain.damage;

public interface HitReceiver {
    void hit(int damage, DamageType type);
}

public enum DamageType {
    BITE,       // Checks arm guard durability
    BULLET,     // Checks ballistic vest
    BLUNT,      // Direct health damage
    EXPLOSION   // Area damage
}
```

### 5.2 Contract Rules

| Rule | Enforcement |
|------|-------------|
| Bullets call hit() on collision | Integration test |
| Damage type must be correct | Integration test |
| No direct Player import in Akashi domain | Code review |
| No direct Item import in IDA domain | Code review |

### 5.3 Player Health Model (IDA Domain)

| Layer | Behavior |
|-------|----------|
| Flesh health | 0-100. Bite = 100 damage (instant death without armor). |
| Arm guard | Absorbs bites. Each bite = -1 durability. |
| Ballistic vest | Mitigates bullet damage. Tier-based reduction. |
| Debuffs | Speed penalty at health thresholds (<75%, <50%, <25%). |

### 5.4 Item Responsibilities (Akashi Domain)

| Item | Provides |
|------|----------|
| Arm guard | Bite protection layers |
| Duct tape | Repairs arm guard |
| Ballistic vest | Bullet damage mitigation |
| Backpack | Inventory slot capacity |
| Firearm | Spawns bullets |

---

## 6. File Structure

### 6.1 Source Layout

```
core/src/main/java/com/itus/
│
├── ida_domain/                    👈 IDA owns this
│   ├── player/
│   │   ├── Player.java
│   │   └── Player_help.txt
│   ├── infected/
│   │   ├── walker/
│   │   │   ├── InfectedWalker.java
│   │   │   └── InfectedWalker_help.txt
│   │   └── (runner, large later)
│   ├── damage/
│   │   ├── HitReceiver.java
│   │   ├── DamageType.java
│   │   └── Damage_help.txt
│   └── ai/
│       ├── WalkerAI.java
│       └── AI_help.txt
│
├── akashi_domain/                 👈 Akashi owns this
│   ├── weapons/
│   │   ├── firearm/
│   │   │   ├── AKM.java
│   │   │   └── AKM_help.txt
│   │   └── melee/
│   │       ├── Axe.java
│   │       └── Axe_help.txt
│   ├── items/
│   │   ├── armor/
│   │   │   ├── ArmGuard.java
│   │   │   └── ArmGuard_help.txt
│   │   └── consumables/
│   │       ├── DuctTape.java
│   │       └── DuctTape_help.txt
│   ├── inventory/
│   │   ├── Inventory.java
│   │   └── Inventory_help.txt
│   └── projectiles/
│       ├── Bullet.java
│       └── Bullet_help.txt
│
└── integration/                   👈 Both own this
    ├── tests/
    │   └── BulletHitIntegrationTest.java
    └── Integration_help.txt
```

### 6.2 Help.txt Format

```text
FILENAME.java
DOC_VERSION: YYYY-MM-DD-vN
OWNER: [IDA/Akashi]
BEHAVIOR SOURCE: Real life

PUBLIC METHODS (contract):
- methodName(params) -> what it does

WHAT I NEED FROM OTHER DOMAIN:
- Interface or method required

EXAMPLE:
- How to use this box

OWNER CONTACT: [name]
```

---

## 7. Development Workflow

### 7.1 Daily Cycle

```
1. git pull
2. Work in your domain only
3. Run integration tests before commit
4. git add . + git commit -m "message"
5. git push
```

### 7.2 Integration Test Rule

Before pushing, run:

```bash
.\gradlew test
```

If integration test fails:
- IDA's test fails → IDA fixes
- Akashi's test fails → Akashi fixes
- Both review the failure together

### 7.3 Crack Protocol

When a box must be opened (debugging):

1. Message the owner: "Need to crack [box name]"
2. Owner responds: "Go ahead" or "Let me look first"
3. Open the box (access internal code)
4. Fix only what's broken
5. Update help.txt if public methods changed
6. Close the box
7. Run integration tests
8. Push with "CRACK: [box name]" in commit message

**One crack at a time. No silent cracks.**

---

## 8. Integration Testing Protocol

### 8.1 Critical Test: Bullet Hits Player

```java
@Test
public void bulletHitsPlayer() {
    Player player = new Player(0, 0);
    AKM gun = new AKM();
    Bullet bullet = gun.fireAt(0, 0, 0);  // Fires at player
    
    bullet.update(1.0f);  // Simulate travel
    
    assert player.getHealth() < 100;
}
```

### 8.2 Test Ownership

| Test | Owner |
|------|-------|
| Bullet → Player hit | Both (written together) |
| Bullet → Zombie hit | IDA |
| Armor durability | Akashi |
| Duct tape repair | Akashi |
| Debuff on low health | IDA |

---

## 9. Documentation Standard

### 9.1 Required Docs Per Box

| File | Required? | Who writes |
|------|-----------|------------|
| `.java` source | Yes | Owner |
| `_help.txt` | Yes | Owner (after code stabilizes) |
| `_help.txt` version match | Yes | CI or manual check |

### 9.2 Version Matching

Code file header:
```java
// DOC_VERSION: 2026-04-23-v1
```

Help.txt header:
```text
DOC_VERSION: 2026-04-23-v1
CODE_FILE: ClassName.java
```

If versions mismatch → crack protocol.

---

## 10. Version 0.1 Milestone

### 10.1 Scope (Minimum Viable Product)

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
| Integration test passes | Target |

### 10.2 Out of Scope for v0.1

- Runner / Large infected
- Ballistic vest
- Backpack
- Hunger system
- Multiple weapons
- Stealth
- Dismemberment

### 10.3 Target Date

2-3 weeks from project start.

---

## 11. Risk Register

| Risk | Probability | Mitigation |
|------|-------------|------------|
| Git merge conflicts | Medium | Work in separate domains. Never same file. |
| Help.txt drifts from code | Medium | Version headers + crack protocol |
| Friend loses motivation | Low | Clear ownership, no creative friction |
| LibGDX learning curve | Low (IDA) / Medium (Akashi) | Start with LibGDX demo |
| Integration test gaps | Medium | Add test for every contract |
| Scope creep | High | Strict v0.1 milestone. No extras. |

---

## 12. Glossary

| Term | Definition |
|------|-------------|
| **Black Box** | A unit with defined contract, no internal visibility required |
| **Contract** | Public methods + help.txt |
| **Crack** | Opening a box to debug or modify |
| **Domain** | Owner's complete area (ida_domain, akashi_domain) |
| **Help.txt** | Human/AI readable contract documentation |
| **HitReceiver** | Primary cross-domain interface |
| **Integration test** | Test that verifies contract between domains |
| **Owner** | Person responsible for a box. Name on it. |

---

## Appendix A: Quick Reference Card

```
┌─────────────────────────────────────────────────────────────┐
│              ITU_S v0.0.1 - QUICK REFERENCE                 │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  RUN: .\gradlew lwjgl3:run                                  │
│  TEST: .\gradlew test                                       │
│                                                              │
│  DOMAINS:                                                   │
│    ida_domain/    ← IDA (combat, AI, player, infected)      │
│    akashi_domain/ ← Akashi (items, weapons, inventory)      │
│                                                              │
│  CONTRACT:                                                  │
│    HitReceiver.hit(int damage, DamageType type)             │
│                                                              │
│  RULES:                                                     │
│    • No cross-domain imports (except HitReceiver)          │
│    • Bullets call hit() on whatever they touch             │
│    • Help.txt matches code version                         │
│    • One crack at a time                                   │
│    • Integration test must pass before push                │
│                                                              │
│  MILESTONE v0.1:                                           │
│    Player + Walker + AKM + Bullet + ArmGuard + DuctTape    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
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
| 6 | Polish + bug fixes | Polish + bug fixes |
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

**END OF DOCUMENT**

*IDA + Akashi — Onward to the future alongside the technology*

---