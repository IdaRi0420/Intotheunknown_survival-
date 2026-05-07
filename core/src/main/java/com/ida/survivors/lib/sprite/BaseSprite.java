// FILE: lib/sprite/BaseSprite.java
// OWNER: IDA
// DOC_VERSION: 2026-05-04-v1
// See BaseSprite_help.txt for contract

package com.ida.survivors.lib.sprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * BaseSprite - Abstract base class for all visual representations of entities.
 * 
 * Every entity that appears on screen needs a sprite class that extends BaseSprite.
 * The sprite is responsible for rendering the entity's visual appearance,
 * but contains NO game logic (no health, no AI, no inventory).
 * 
 * Separation of concerns:
 * - Entity (logic): position, health, inventory, AI
 * - Sprite (visual): textures, animations, particles, UI elements
 * 
 * This allows:
 * - Changing art style without touching game logic
 * - Testing logic without LibGDX (sprites not required)
 * - Multiple sprite types for same entity (e.g., PlayerSprite, PlayerDebugSprite)
 * - Network multiplayer: logic runs on server, sprites on client
 * 
 * All sprites must implement:
 * - render(SpriteBatch): Draw the entity at its current position
 * - dispose(): Free GPU resources (textures, etc.)
 * 
 * Optional methods (override as needed):
 * - update(float delta): Update animations, particle effects
 * - setVisible(boolean): Hide/show sprite (e.g., during death fade)
 * - isVisible(): Check if sprite should be rendered
 */
public abstract class BaseSprite {
    
    // ===== FIELDS =====
    
    /** Whether this sprite should be rendered (default: true) */
    protected boolean visible = true;
    
    /** Render layer/z-order (higher = drawn on top, default: 0) */
    protected int layer = 0;
    
    /** Alpha/opacity (0.0 = invisible, 1.0 = fully visible, default: 1.0) */
    protected float alpha = 1.0f;
    
    
    // ===== CORE METHODS (Must Implement) =====
    
    /**
     * Render the sprite at its entity's position.
     * Called every frame by GameRenderer or similar.
     * 
     * @param batch SpriteBatch from LibGDX (already begun)
     */
    public abstract void render(SpriteBatch batch);
    
    /**
     * Free GPU resources (textures, framebuffers, etc.).
     * Called when screen closes or game shuts down.
     * CRITICAL: Prevents memory leaks.
     */
    public abstract void dispose();
    
    
    // ===== OPTIONAL METHODS (Override as Needed) =====
    
    /**
     * Update sprite state (animations, effects, timers).
     * Called every frame BEFORE render.
     * 
     * Base implementation does nothing.
     * Override for animated sprites.
     * 
     * @param deltaTime Time since last frame (seconds)
     */
    public void update(float deltaTime) {
        // Default: no animation update
        // Child classes override for walking/running/idle animations
    }
    
    
    // ===== VISIBILITY CONTROLS =====
    
    /**
     * Set whether sprite should be rendered.
     * Used for: death fade-out, stealth invisibility, UI hide/show.
     * 
     * @param visible true = render, false = skip rendering
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Check if sprite should be rendered.
     * 
     * @return true if visible, false if hidden
     */
    public boolean isVisible() {
        return visible;
    }
    
    
    // ===== LAYER/Z-ORDER =====
    
    /**
     * Set render layer (z-index).
     * Higher layer = drawn on top of lower layers.
     * 
     * Typical layers:
     * - 0: Ground/tiles (below entities)
     * - 1: Entities (player, zombies, items)
     * - 2: Projectiles (bullets, arrows)
     * - 3: Effects (muzzle flash, blood)
     * - 4: UI (health bars, name tags)
     * 
     * @param layer Render layer (default: 0)
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }
    
    /**
     * Get current render layer.
     * Used by GameRenderer to sort draw order.
     * 
     * @return Layer number (higher = on top)
     */
    public int getLayer() {
        return layer;
    }
    
    
    // ===== TRANSPARENCY / ALPHA =====
    
    /**
     * Set alpha (opacity) for the sprite.
     * Used for: fade in/out, ghost effects, death dissolve.
     * 
     * @param alpha 0.0 = invisible, 1.0 = fully opaque
     */
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
    }
    
    /**
     * Get current alpha value.
     * 
     * @return Alpha (0.0 to 1.0)
     */
    public float getAlpha() {
        return alpha;
    }
    
    /**
     * Apply alpha to batch color.
     * Helper method for child classes to preserve existing color.
     * 
     * @param batch SpriteBatch to modify
     * @param originalColor Original batch color to restore
     */
    protected void applyAlpha(SpriteBatch batch, com.badlogic.gdx.graphics.Color originalColor) {
        batch.setColor(originalColor.r, originalColor.g, originalColor.b, alpha);
    }
    
    
    // ===== BOUNDING BOX (For Debug / Selection) =====
    
    /**
     * Get bounding rectangle X position (world coordinates).
     * Default = entity X (override for sprite offset).
     * 
     * @return X position of bounding box center
     */
    public float getBoundingX() {
        // Default: subclasses override if sprite has offset
        return 0;
    }
    
    /**
     * Get bounding rectangle Y position (world coordinates).
     * Default = entity Y (override for sprite offset).
     * 
     * @return Y position of bounding box center
     */
    public float getBoundingY() {
        // Default: subclasses override if sprite has offset
        return 0;
    }
    
    /**
     * Get bounding box width (pixels/world units).
     * Used for debug rendering and mouse picking.
     * 
     * @return Width of sprite
     */
    public float getBoundingWidth() {
        return 32;  // Default: 32x32, override in child
    }
    
    /**
     * Get bounding box height.
     * 
     * @return Height of sprite
     */
    public float getBoundingHeight() {
        return 32;  // Default: 32x32, override in child
    }
    
    
    // ===== RESOURCE MANAGEMENT =====
    
    /**
     * Check if sprite is loaded and ready to render.
     * Useful for async texture loading.
     * 
     * @return true if ready, false if still loading
     */
    public boolean isReady() {
        return true;  // Default: always ready
        // Override for async texture loading
    }
    
    /**
     * Reload textures (e.g., after graphics settings change).
     * Called when game resolution or quality changes.
     */
    public void reload() {
        // Default: do nothing
        // Override for dynamic texture reload
    }
}