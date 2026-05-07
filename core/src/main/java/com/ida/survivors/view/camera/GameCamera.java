// FILE: view/camera/GameCamera.java
// OWNER: IDA
// DOC_VERSION: 2026-05-04-v1
// See Camera_help.txt for contract

package com.ida.survivors.view.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.ida.survivors.lib.entity.BaseEntity;

/**
 * GameCamera - Camera that follows the player with smooth movement.
 * 
 * Responsibilities:
 * - Follow target entity (player)
 * - Smooth camera movement (lerp) for professional feel
 * - Handle screen shakes (damage, explosions)
 * - Convert screen to world coordinates (for mouse aiming)
 * - Handle window resizing
 * 
 * Phase 1: Simple follow (no smoothing, no shake)
 * Phase 2: Smooth follow + screen shake
 * Phase 3: Zoom in/out (sniper, map view)
 * 
 * Black Box Doctrine:
 * - Camera only needs target's position (getX(), getY())
 * - It does NOT need to know what the target is (Player, Zombie, etc.)
 * - It follows any BaseEntity
 */
public class GameCamera {
    
    // ===== FIELDS =====
    
    /** LibGDX orthographic camera */
    private OrthographicCamera camera;
    
    /** The entity to follow (player, or null for free camera) */
    private BaseEntity target;
    
    /** Camera world position (smooth follows target) */
    private float posX, posY;
    
    /** Camera width (in world units) */
    private float viewportWidth;
    
    /** Camera height (in world units) */
    private float viewportHeight;
    
    /** Smoothing factor (0 = no smoothing, 1 = instant, 0.05 = very smooth) */
    private float smoothFactor = 0.1f;
    
    /** Dead zone (pixels, camera doesn't move until target leaves this zone) */
    private float deadZoneX = 0;
    private float deadZoneY = 0;
    
    /** Screen shake variables */
    private float shakeIntensity = 0;
    private float shakeDuration = 0;
    private float shakeTimer = 0;
    
    /** Minimum and maximum zoom (Phase 3) */
    private float minZoom = 0.5f;
    private float maxZoom = 2.0f;
    private float targetZoom = 1.0f;
    
    /** World boundaries (Phase 2, prevent seeing outside map) */
    private float minWorldX = -Float.MAX_VALUE;
    private float maxWorldX = Float.MAX_VALUE;
    private float minWorldY = -Float.MAX_VALUE;
    private float maxWorldY = Float.MAX_VALUE;
    
    /** Temporary vector for calculations (avoid GC) */
    private Vector3 tempVector = new Vector3();
    
    /** Debug flag */
    private boolean debugMode = false;
    
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Create camera with default viewport size (screen size).
     * Camera center = (0, 0) initially.
     */
    public GameCamera() {
        this(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    /**
     * Create camera with custom viewport size.
     * 
     * @param viewportWidth Camera width in world units
     * @param viewportHeight Camera height in world units
     */
    public GameCamera(float viewportWidth, float viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.camera = new OrthographicCamera(viewportWidth, viewportHeight);
        this.camera.position.set(0, 0, 0);
        this.camera.update();
        this.posX = 0;
        this.posY = 0;
    }
    
    
    // ===== TARGET FOLLOWING =====
    
    /**
     * Set entity for camera to follow.
     * Camera will track this entity's position.
     * 
     * @param target Entity to follow (PlayerEntity, etc.)
     */
    public void setTarget(BaseEntity target) {
        this.target = target;
        // Snap camera to target position immediately
        if (target != null) {
            posX = target.getX();
            posY = target.getY();
            updateCameraPosition();
        }
    }
    
    /**
     * Get current target entity.
     * 
     * @return Current target, or null if none
     */
    public BaseEntity getTarget() {
        return target;
    }
    
    /**
     * Remove target (free camera mode).
     * Camera stays at current position.
     */
    public void clearTarget() {
        this.target = null;
    }
    
    
    // ===== CAMERA UPDATE (Called each frame) =====
    
    /**
     * Update camera position to follow target.
     * Called every frame by GameOrchestrator.
     * 
     * @param deltaTime Time since last frame (seconds)
     */
    public void update(float deltaTime) {
        // Update screen shake
        updateShake(deltaTime);
        
        // Update target position (if target exists)
        if (target != null) {
            float targetX = target.getX();
            float targetY = target.getY();
            
            // Apply dead zone (camera doesn't move until target leaves zone)
            float dx = targetX - posX;
            float dy = targetY - posY;
            
            if (Math.abs(dx) > deadZoneX) {
                posX += dx * smoothFactor;
            }
            if (Math.abs(dy) > deadZoneY) {
                posY += dy * smoothFactor;
            }
            
            // Clamp to world boundaries
            posX = MathUtils.clamp(posX, minWorldX, maxWorldX);
            posY = MathUtils.clamp(posY, minWorldY, maxWorldY);
        }
        
        // Apply screen shake offset
        float shakeX = 0;
        float shakeY = 0;
        if (shakeTimer > 0) {
            shakeX = (float) (Math.random() - 0.5) * shakeIntensity * 2;
            shakeY = (float) (Math.random() - 0.5) * shakeIntensity * 2;
        }
        
        // Update camera position
        camera.position.set(posX + shakeX, posY + shakeY, 0);
        camera.update();
    }
    
    /**
     * Update camera position immediately (no smoothing).
     * Used for teleports and respawns.
     */
    private void updateCameraPosition() {
        if (target != null) {
            posX = target.getX();
            posY = target.getY();
        }
        
        camera.position.set(posX, posY, 0);
        camera.update();
    }
    
    
    // ===== SCREEN SHAKE =====
    
    /**
     * Start screen shake effect.
     * 
     * @param intensity Shake strength (pixels, e.g., 5 = small shake)
     * @param duration How long shake lasts (seconds, e.g., 0.3)
     */
    public void shake(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
        this.shakeTimer = duration;
    }
    
    /**
     * Update screen shake timer.
     * 
     * @param deltaTime Time since last frame
     */
    private void updateShake(float deltaTime) {
        if (shakeTimer > 0) {
            shakeTimer -= deltaTime;
            if (shakeTimer <= 0) {
                shakeIntensity = 0;
            }
        }
    }
    
    /**
     * Check if camera is currently shaking.
     * 
     * @return true if shaking
     */
    public boolean isShaking() {
        return shakeTimer > 0;
    }
    
    
    // ===== ZOOM CONTROL (Phase 3) =====
    
    /**
     * Set camera zoom level.
     * 
     * @param zoom Zoom factor (1.0 = normal, 0.5 = zoomed out, 2.0 = zoomed in)
     */
    public void setZoom(float zoom) {
        targetZoom = MathUtils.clamp(zoom, minZoom, maxZoom);
        camera.zoom = targetZoom;
        camera.update();
    }
    
    /**
     * Smoothly zoom camera over time.
     * Phase 3: Animated zoom.
     * 
     * @param zoom Target zoom level
     * @param duration Transition time (seconds)
     */
    public void smoothZoom(float zoom, float duration) {
        // Phase 3 implementation
        this.targetZoom = MathUtils.clamp(zoom, minZoom, maxZoom);
        // Would need interpolation timer
    }
    
    /**
     * Get current zoom level.
     * 
     * @return Zoom factor
     */
    public float getZoom() {
        return camera.zoom;
    }
    
    
    // ===== WORLD BOUNDARIES (Phase 2) =====
    
    /**
     * Set world boundaries (prevents camera from showing outside map).
     * 
     * @param minX Minimum world X
     * @param maxX Maximum world X
     * @param minY Minimum world Y
     * @param maxY Maximum world Y
     */
    public void setWorldBounds(float minX, float maxX, float minY, float maxY) {
        this.minWorldX = minX;
        this.maxWorldX = maxX;
        this.minWorldY = minY;
        this.maxWorldY = maxY;
    }
    
    
    // ===== COORDINATE CONVERSION (For mouse aiming) =====
    
    /**
     * Convert screen coordinates to world coordinates.
     * Used for: mouse aiming, UI placement, debug clicks.
     * 
     * @param screenX Screen X coordinate (pixels)
     * @param screenY Screen Y coordinate (pixels)
     * @return Vector3 with world coordinates (z = 0)
     */
    public Vector3 unproject(int screenX, int screenY) {
        tempVector.set(screenX, screenY, 0);
        camera.unproject(tempVector);
        return tempVector;
    }
    
    /**
     * Convert world coordinates to screen coordinates.
     * Used for: UI positioning above entities (health bars).
     * 
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @return Vector3 with screen coordinates (pixels)
     */
    public Vector3 project(float worldX, float worldY) {
        tempVector.set(worldX, worldY, 0);
        camera.project(tempVector);
        return tempVector;
    }
    
    
    // ===== WINDOW RESIZE =====
    
    /**
     * Handle window resize.
     * Called by GameOrchestrator when window size changes.
     * 
     * @param width New window width (pixels)
     * @param height New window height (pixels)
     */
    public void resize(int width, int height) {
        viewportWidth = width;
        viewportHeight = height;
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    
    
    // ===== SMOOTHING SETTINGS =====
    
    /**
     * Set camera smoothing factor.
     * 
     * @param smoothFactor 0 = no smoothing (teleport), 1 = instant, 
     *                     0.1 = smooth default
     */
    public void setSmoothFactor(float smoothFactor) {
        this.smoothFactor = MathUtils.clamp(smoothFactor, 0.01f, 1.0f);
    }
    
    /**
     * Set dead zone (camera doesn't move until target leaves this area).
     * 
     * @param deadZoneX Dead zone width in world units
     * @param deadZoneY Dead zone height in world units
     */
    public void setDeadZone(float deadZoneX, float deadZoneY) {
        this.deadZoneX = deadZoneX;
        this.deadZoneY = deadZoneY;
    }
    
    
    // ===== GETTERS =====
    
    /**
     * Get LibGDX OrthographicCamera (for renderer).
     * 
     * @return LibGDX camera
     */
    public OrthographicCamera getCamera() {
        return camera;
    }
    
    /**
     * Get camera X position (world coordinates).
     * 
     * @return Camera center X
     */
    public float getX() {
        return posX;
    }
    
    /**
     * Get camera Y position (world coordinates).
     * 
     * @return Camera center Y
     */
    public float getY() {
        return posY;
    }
    
    /**
     * Get viewport width (world units visible).
     * 
     * @return Viewport width
     */
    public float getViewportWidth() {
        return viewportWidth;
    }
    
    /**
     * Get viewport height (world units visible).
     * 
     * @return Viewport height
     */
    public float getViewportHeight() {
        return viewportHeight;
    }
    
    /**
     * Get left edge of camera view (world coordinates).
     * 
     * @return Minimum visible X
     */
    public float getLeft() {
        return posX - viewportWidth / 2;
    }
    
    /**
     * Get right edge of camera view (world coordinates).
     * 
     * @return Maximum visible X
     */
    public float getRight() {
        return posX + viewportWidth / 2;
    }
    
    /**
     * Get bottom edge of camera view (world coordinates).
     * 
     * @return Minimum visible Y
     */
    public float getBottom() {
        return posY - viewportHeight / 2;
    }
    
    /**
     * Get top edge of camera view (world coordinates).
     * 
     * @return Maximum visible Y
     */
    public float getTop() {
        return posY + viewportHeight / 2;
    }
    
    /**
     * Enable/disable debug mode (prints camera position).
     * 
     * @param debug true for debug output
     */
    public void setDebugMode(boolean debug) {
        this.debugMode = debug;
    }
    
    /**
     * Clean up camera resources.
     * (OrthographicCamera has no resources, but method exists for API consistency)
     */
    public void dispose() {
        // Nothing to dispose (OrthographicCamera has no native resources)
        if (debugMode) {
            System.out.println("GameCamera disposed");
        }
    }
}