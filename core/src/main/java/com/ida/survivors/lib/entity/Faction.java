// FILE: lib/entity/Faction.java
// OWNER: IDA
// DOC_VERSION: 2026-05-04-v1
// See Faction_help.txt for contract

package com.ida.survivors.lib.entity;

/**
 * Faction - Allegiance/type classification for all entities.
 * 
 * Every entity belongs to exactly ONE faction.
 * Factions determine:
 * - AI targeting (who attacks whom)
 * - Hit effects (bullets hit everyone equally, but AI chooses targets)
 * - Team-based mechanics (future: reputation, alliances)
 * 
 * Current factions:
 * - HUMAN: Player, Bandit, Army, Scavenger
 * - INFECTED: Walker, Boomer, Viral, Jumper, Tank, Evolved
 * 
 * Future expansions (Phase 3+):
 * - NEUTRAL: Animals, environment
 * - PLAYER_FACTION: For PvP servers
 */
public enum Faction {
    
    /**
     * HUMAN faction.
     * Includes: Player, Bandit, Army, Scavenger.
     * 
     * AI behavior:
     * - Bandit attacks Army (different sub-factions within HUMAN)
     * - Army attacks Bandit
     * - Scavenger is neutral unless attacked
     * - Player can choose alliances
     * 
     * Faction checking is done in AI, NOT in HitReceiver.
     * Bullets hit all factions equally (realism).
     */
    HUMAN,
    
    /**
     * INFECTED faction.
     * Includes: Walker, Boomer, Viral, Jumper, Tank, Evolved.
     * 
     * AI behavior:
     * - Always attacks HUMAN
     * - Does not attack other INFECTED (unless special cases like Evolved controlling)
     * 
     * Bullets hit INFECTED same as HUMAN.
     * No special "undead" immunity — headshot kills.
     */
    INFECTED;
    
    /**
     * Check if this faction is hostile to another faction.
     * Used by AI to decide attack targets.
     * 
     * Rules:
     * - INFECTED vs HUMAN -> true
     * - HUMAN vs INFECTED -> true
     * - Same faction -> false (usually)
     * 
     * Note: Sub-faction hostility (Bandit vs Army) is handled in AI,
     * not at this level. This is for cross-faction only.
     * 
     * @param other The other faction to check
     * @return true if factions are naturally hostile
     */
    public boolean isHostileTo(Faction other) {
        // INFECTED vs HUMAN = always hostile
        if ((this == INFECTED && other == HUMAN) ||
            (this == HUMAN && other == INFECTED)) {
            return true;
        }
        
        // Same faction = not hostile (by default)
        // Sub-faction hostility handled in specific AI classes
        return false;
    }
    
    /**
     * Get display name for UI.
     * 
     * @return Human-readable faction name
     */
    @Override
    public String toString() {
        switch (this) {
            case HUMAN:
                return "Human";
            case INFECTED:
                return "Infected";
            default:
                return "Unknown";
        }
    }
}