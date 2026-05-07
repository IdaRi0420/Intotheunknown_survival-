// FILE: entities/human/player/PlayerEntity.java
// OWNER: IDA
// DOC_VERSION: 2026-05-04-v1
// See Player_help.txt for contract

package com.ida.survivors.entities.human.player;

import com.ida.survivors.entities.human.Human;

/**
 * PlayerEntity - The character controlled by the human player.
 * 
 * Philosophically: PlayerEntity is NOT special.
 * It's just a Human entity that happens to have a PlayerController attached.
 * 
 * Same as Bandit or Scavenger under the hood:
 * - Health system: same
 * - Inventory: same (just different starting gear)
 * - Faction: HUMAN (same)
 * 
 * The only difference is it has `controller = PlayerController` instead of AI.
 * If PlayerController disconnects, this entity becomes AI-controlled (Scavenger behavior).
 * 
 * Multiplayer note:
 * - Multiple PlayerEntity instances can exist (one per connected human)
 * - Each has different `controlledBy` UUID
 * - Server owns the entity, client sends input commands
 * 
 * Current Phase 1: Just movement and rendering.
 * Phase 2: Inventory, items, damage.
 * Phase 3: Multiplayer possession.
 */
public class PlayerEntity extends Human {
    
    // ===== FIELDS =====
    
    /** Movement speed in units per second */
    private float speed = 200.0f;
    
    /** Player name (for UI, chat, multiplayer) */
    private String playerName;
    
    /** Network ID (for multiplayer: UUID of controlling client) */
    private String controlledBy;  // null = AI controlled
    
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Create a new player entity at position (x, y).
     * Default speed = 200 units/second.
     * Player name defaults to "Survivor" until set.
     * Controlled by human (null until controller attaches).
     * 
     * @param x Initial X position
     * @param y Initial Y position
     */
    public PlayerEntity(float x, float y) {
        super(x, y);
        this.playerName = "Survivor";
        this.controlledBy = null;  // Not yet possessed
    }
    
    /**
     * Create a player with custom name.
     * 
     * @param x Initial X position
     * @param y Initial Y position
     * @param playerName Display name for UI
     */
    public PlayerEntity(float x, float y, String playerName) {
        super(x, y);
        this.playerName = playerName;
        this.controlledBy = null;
    }
    
    
    // ===== MOVEMENT (Phase 1) =====
    
    /**
     * Move player by delta.
     * Called by InputHandler (or later, network receiver).
     * 
     * @param dx Change in X (positive = right)
     * @param dy Change in Y (positive = up)
     */
    public void move(float dx, float dy) {
        // Normalize diagonal movement (optional, makes speed consistent)
        if (dx != 0 && dy != 0) {
            dx *= 0.707f;  // 1/sqrt(2)
            dy *= 0.707f;
        }
        
        super.move(dx * speed, dy * speed);
    }
    
    /**
     * Set player movement speed.
     * Affected by:
     * - Encumbrance (weight carried)
     * - Injuries (low health)
     * - Terrain (later)
     * - Buffs/debuffs (later)
     * 
     * @param speed New speed in units/second (default 200)
     */
    public void setSpeed(float speed) {
        this.speed = Math.max(0, speed);
    }
    
    /**
     * Get current movement speed.
     * 
     * @return Current speed in units/second
     */
    public float getSpeed() {
        return speed;
    }
    
    
    // ===== CONTROLLER / POSSESSION (Multiplayer prep) =====
    
    /**
     * Attach a controller (human) to this entity.
     * Called when player connects or respawns.
     * 
     * @param controllerId Unique ID of controlling client (UUID)
     */
    public void possess(String controllerId) {
        this.controlledBy = controllerId;
    }
    
    /**
     * Detach controller from this entity.
     * Entity becomes AI-controlled (Scavenger behavior).
     * Called on disconnect or death.
     */
    public void relinquish() {
        this.controlledBy = null;
    }
    
    /**
     * Check if this entity is currently controlled by a human.
     * 
     * @return true if possessed by player, false if AI
     */
    public boolean isPossessed() {
        return controlledBy != null;
    }
    
    /**
     * Get the ID of the controlling client.
     * 
     * @return Controller ID (UUID) or null if AI-controlled
     */
    public String getControllerId() {
        return controlledBy;
    }
    
    
    // ===== PLAYER INFO (UI / Multiplayer) =====
    
    /**
     * Set player's display name.
     * 
     * @param name New name
     */
    public void setPlayerName(String name) {
        this.playerName = name;
    }
    
    /**
     * Get player's display name.
     * 
     * @return Player name
     */
    public String getPlayerName() {
        return playerName;
    }
    
    
    // ===== NETWORK STATE (Future) =====
    
    /**
     * Get serializable state for network sync.
     * Phase 3: send over network to clients.
     * 
     * @return PlayerState object with x, y, health, name, etc.
     */
    public Object getNetworkState() {
        // Phase 3 implementation
        // return new PlayerState(x, y, health, playerName, controlledBy);
        return null;  // Stub for Phase 1
    }
    
    /**
     * Apply network state from server.
     * Phase 3: client receives and applies.
     * 
     * @param state PlayerState from server
     */
    public void applyNetworkState(Object state) {
        // Phase 3 implementation
    }
    
    
    // ===== PHASE 2 STUBS =====
    
    /**
     * Phase 2: Initialize inventory with starting gear.
     * Called when player spawns.
     */
    public void initStarterGear() {
        // Phase 2: 
        // - Add arm guard (3 layers)
        // - Add duct tape (2 rolls)
        // - Add basic melee weapon (axe)
        // - Add empty backpack (optional)
    }
    
    /**
     * Phase 2: Handle player death.
     * Overrides BaseEntity.onDeath behavior.
     */
    public void onDeath() {
        // Drop inventory as loot
        // Respawn logic (later)
        // Relinquish controller
        relinquish();
    }
}