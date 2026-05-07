// FILE: entities/human/Human.java
// OWNER: IDA
// DOC_VERSION: 2026-05-04-v1
// See Human_help.txt for contract

package com.ida.survivors.entities.human;

import com.ida.survivors.lib.entity.BaseEntity;
import com.ida.survivors.lib.entity.Faction;

/**
 * Human - Intermediate base class for all human entities.
 * 
 * Humans are:
 * - Faction: HUMAN
 * - Playable: PlayerEntity (controlled by human)
 * - NPCs: Bandit (hostile), Army (neutral/hostile), Scavenger (friendly)
 * 
 * This class exists to:
 * 1. Set faction to HUMAN automatically
 * 2. Provide a place for human-specific logic later (infection, morale, etc.)
 * 3. Make entity hierarchy readable (Human -> PlayerEntity vs BaseEntity -> PlayerEntity)
 * 
 * Currently adds nothing beyond BaseEntity except faction.
 * Future additions may include:
 * - Infection timer (when bitten)
 * - Inventory reference (all humans carry items)
 * - Stamina system
 * - Voice line triggers
 */
public abstract class Human extends BaseEntity {
    
    /**
     * Create a new human entity at position (x, y).
     * Faction is automatically set to HUMAN.
     * Health starts at 100.
     * 
     * @param x Initial X position
     * @param y Initial Y position
     */
    public Human(float x, float y) {
        super(x, y, Faction.HUMAN);
    }
    
    /**
     * All humans have inventory by default.
     * This method is called by specific human types (PlayerEntity, Bandit, etc.)
     * to initialize their inventory with appropriate size.
     * 
     * Base implementation does nothing.
     * Child classes override to enable inventory.
     * 
     * @param size Number of slots (width*height) or max weight (kg)
     */
    protected void initInventory(int size) {
        // Stub for Phase 1
        // Phase 2: inventory = new Inventory(size);
    }
    
    /**
     * Humans can be infected by bites.
     * Called when a bite penetrates armor.
     * 
     * Base implementation: just damage.
     * Child classes override for infection timer, transformation, etc.
     * 
     * @param damage Amount of damage from bite (usually 100)
     */
    protected void onBite(int damage) {
        // Phase 1: just take damage
        // Phase 2: start infection timer
        takeDamage(damage);
    }
    
    /**
     * Humans can use items (heal, equip, eat, drink).
     * Called by inventory system when player selects item.
     * 
     * @param item The item being used (from Akashi's domain)
     */
    public void useItem(Object item) {
        // Stub for Phase 1
        // Phase 2: delegate to inventory system
        // Phase 2: item.onUse(this)
    }
}