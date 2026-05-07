// FILE: entities/human/player/PlayerSprite.java
// OWNER: IDA
// DOC_VERSION: 2026-05-04-v1
// See Player_help.txt for contract

package com.ida.survivors.entities.human.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.ida.survivors.lib.sprite.BaseSprite;

/**
 * PlayerSprite - Visual representation of the player entity.
 * 
 * Renders the player on screen using LibGDX.
 * 
 * Phase 1: Simple colored rectangle (no asset loading yet)
 * Phase 2: Load actual PNG texture from assets/
 * Phase 3: Animations (walking, idle, hurt, death)
 * 
 * This sprite is owned by the renderer and called every frame.
 * It reads the player's position from PlayerEntity and draws at that location.
 * 
 * Separation of concerns:
 * - PlayerEntity: Logic (position, health, inventory)
 * - PlayerSprite: Rendering (textures, animations, particles)
 * - No logic in sprite. No rendering in entity.
 */
public class PlayerSprite extends BaseSprite {
    
    // ===== FIELDS =====
    
    /** Reference to the logic entity (read-only for position/data) */
    private PlayerEntity player;
    
    /** Phase 1: Rectangle renderer (fallback before texture loads) */
    private ShapeRenderer shapeRenderer;
    
    /** Phase 2: Texture for player sprite */
    private Texture texture;
    
    /** Phase 2: Texture file path */
    private static final String TEXTURE_PATH = "player.png";
    
    /** Sprite dimensions (pixels) */
    private static final int SPRITE_WIDTH = 32;
    private static final int SPRITE_HEIGHT = 32;
    
    /** Color for debug rectangle (Phase 1) */
    private static final Color PLAYER_COLOR = Color.GREEN;
    
    /** Flag to use texture (false in Phase 1, true in Phase 2) */
    private boolean useTexture = false;
    
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Create sprite for player entity.
     * Phase 1: Uses ShapeRenderer (no assets needed)
     * 
     * @param player The player entity to render
     */
    public PlayerSprite(PlayerEntity player) {
        this.player = player;
        this.shapeRenderer = new ShapeRenderer();
    }
    
    /**
     * Create sprite with texture loading.
     * Phase 2: Load actual PNG from assets folder.
     * 
     * @param player The player entity to render
     * @param loadTexture If true, attempt to load texture from assets
     */
    public PlayerSprite(PlayerEntity player, boolean loadTexture) {
        this(player);
        if (loadTexture) {
            try {
                this.texture = new Texture(TEXTURE_PATH);
                this.useTexture = true;
                this.shapeRenderer = null;  // Clean up, not needed
            } catch (Exception e) {
                System.err.println("Failed to load " + TEXTURE_PATH + ", using fallback rectangle");
                this.useTexture = false;
            }
        }
    }
    
    
    // ===== RENDERING =====
    
    /**
     * Render the player at its current position.
     * Called by GameRenderer every frame.
     * 
     * @param batch SpriteBatch for texture rendering
     */
    @Override
    public void render(SpriteBatch batch) {
        if (useTexture && texture != null) {
            renderTexture(batch);
        } else {
            renderRectangle();
        }
    }
    
    /**
     * Render using texture (Phase 2+).
     * Requires active SpriteBatch.
     * 
     * @param batch SpriteBatch from GameRenderer
     */
    private void renderTexture(SpriteBatch batch) {
        float x = player.getX();
        float y = player.getY();
        
        // Draw texture at entity position
        // Offset by half width/height to center on entity position
        batch.draw(texture, 
                   x - SPRITE_WIDTH / 2, 
                   y - SPRITE_HEIGHT / 2,
                   SPRITE_WIDTH, 
                   SPRITE_HEIGHT);
    }
    
    /**
     * Render using ShapeRenderer (Phase 1 fallback).
     * No SpriteBatch needed — draws colored rectangle.
     * Useful for debugging before art assets exist.
     */
    private void renderRectangle() {
        float x = player.getX();
        float y = player.getY();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(PLAYER_COLOR);
        shapeRenderer.rect(x - SPRITE_WIDTH / 2, 
                          y - SPRITE_HEIGHT / 2, 
                          SPRITE_WIDTH, 
                          SPRITE_HEIGHT);
        shapeRenderer.end();
    }
    
    
    // ===== HEALTH UI (Phase 2) =====
    
    /**
     * Render health bar above player.
     * Called optionally by GameRenderer or UI system.
     * 
     * @param batch SpriteBatch for UI rendering
     */
    public void renderHealthBar(SpriteBatch batch) {
        // Phase 2: Draw a red/green bar above player head
        // Uses player.getHealth() to show current health percentage
        // Stub for Phase 1
    }
    
    
    // ===== NAME TAG (Multiplayer) =====
    
    /**
     * Render player name above character.
     * Called optionally for multiplayer or UI.
     * 
     * @param batch SpriteBatch for font rendering
     * @param font LibGDX font (loaded separately)
     */
    public void renderNameTag(Object batch, Object font) {
        // Phase 3: Draw player.getPlayerName() above sprite
        // Stub for Phase 1
    }
    
    
    // ===== ANIMATION STUBS (Phase 3) =====
    
    /**
     * Update animation frame (called each frame).
     * Phase 3: Walking animation based on movement velocity.
     * 
     * @param deltaTime Time since last frame (seconds)
     */
    public void updateAnimation(float deltaTime) {
        // Phase 3: 
        // - Track movement direction
        // - Update frame timer
        // - Change texture region for walking/running/idle
    }
    
    /**
     * Play hit reaction animation.
     * Called when player takes damage.
     */
    public void playHitAnimation() {
        // Phase 3: Flash white, knockback visual, blood particle
    }
    
    /**
     * Play death animation.
     * Called when player dies.
     */
    public void playDeathAnimation() {
        // Phase 3: Fall down, fade out, respawn later
    }
    
    
    // ===== CLEANUP =====
    
    /**
     * Dispose of textures and shape renderer.
     * Called when game shuts down or screen changes.
     */
    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
    }
    
    
    // ===== GETTERS =====
    
    /**
     * Get sprite width in pixels.
     * 
     * @return Width in pixels
     */
    public int getWidth() {
        return SPRITE_WIDTH;
    }
    
    /**
     * Get sprite height in pixels.
     * 
     * @return Height in pixels
     */
    public int getHeight() {
        return SPRITE_HEIGHT;
    }
    
    /**
     * Check if using texture or fallback rectangle.
     * 
     * @return true if texture loaded, false if using ShapeRenderer
     */
    public boolean isUsingTexture() {
        return useTexture;
    }
    
    /**
     * Get reference to player entity (for renderer).
     * 
     * @return Player entity being rendered
     */
    public PlayerEntity getPlayer() {
        return player;
    }
}