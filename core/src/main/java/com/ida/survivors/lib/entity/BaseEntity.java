// FILE: lib/entity/BaseEntity.java
// OWNER: IDA
// DOC_VERSION: 2026-05-04-v1
// See BaseEntity_help.txt for contract

package com.ida.survivors.lib.entity;

/**
 * BaseEntity - Root class for all living things in the game world.
 * 
 * Every entity has:
 * - Position (x, y)
 * - Health (0-100)
 * - Faction (HUMAN or INFECTED)
 * 
 * This class is extended by:
 * - Human (entities/human/Human.java)
 * - Infected (entities/infected/Infected.java)
 */
public abstract class BaseEntity {
    
    // ===== PROTECTED FIELDS =====
    // Child classes can access directly
    // Other classes must use getters/setters
    
    protected float x;          // World position X
    protected float y;          // World position Y
    protected int health;       // 0 = dead, 100 = full health
    protected Faction faction;  // HUMAN or INFECTED
    
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Create a new entity at position (x, y) with given faction.
     * Health starts at 100 (full).
     * 
     * @param x       Initial X position
     * @param y       Initial Y position
     * @param faction HUMAN or INFECTED
     */
    public BaseEntity(float x, float y, Faction faction) {
        this.x = x;
        this.y = y;
        this.health = 100;
        this.faction = faction;
    }
    
    
    // ===== MOVEMENT =====
    
    /**
     * Move entity by delta (dx, dy).
     * Called by controller (PlayerController or AI) each frame.
     * 
     * @param dx Change in X position
     * @param dy Change in Y position
     */
    public void move(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }
    
    /**
     * Teleport entity to absolute position.
     * Use sparingly (spawn points, respawn, etc.)
     * 
     * @param x New X position
     * @param y New Y position
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    
    // ===== HEALTH & DAMAGE =====
    
    /**
     * Apply damage to entity.
     * Called by HitReceiver.hit() implementation.
     * 
     * @param damage Amount of damage to apply
     */
    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
    }
    
    /**
     * Heal entity by amount.
     * Called by consumable items (food, medicine, duct tape).
     * 
     * @param amount Amount to heal
     */
    public void heal(int amount) {
        this.health += amount;
        if (this.health > 100) {
            this.health = 100;
        }
    }
    
    /**
     * Check if entity is dead.
     * 
     * @return true if health <= 0, false otherwise
     */
    public boolean isDead() {
        return this.health <= 0;
    }
    
    
    // ===== GETTERS =====
    // Public read-only access to fields
    
    public float getX() { return x; }
    public float getY() { return y; }
    public int getHealth() { return health; }
    public Faction getFaction() { return faction; }
    
    
    // ===== SETTERS (Limited) =====
    // Only for specific use cases
    
    /**
     * Set health directly.
     * Used by death logic and respawn systems.
     * Prefer takeDamage() or heal() for normal use.
     * 
     * @param health New health value (0-100)
     */
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(100, health));
    }
    
    /**
     * Set faction (rare — for infected turning, disguise, etc.)
     * 
     * @param faction New faction
     */
    protected void setFaction(Faction faction) {
        this.faction = faction;
    }
}