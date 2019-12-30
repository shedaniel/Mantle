package slimeknights.mantle.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import slimeknights.mantle.Mantle;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class ExtraHeartRenderHandler {
    
    private static final Identifier ICON_HEARTS = new Identifier(Mantle.modId, "textures/gui/hearts.png");
    private static final Identifier ICON_ABSORB = new Identifier(Mantle.modId, "textures/gui/absorb.png");
    private static final Identifier ICON_VANILLA = DrawableHelper.GUI_ICONS_LOCATION;
    
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    private int playerHealth = 0;
    private int lastPlayerHealth = 0;
    private long healthUpdateCounter = 0;
    private long lastSystemTime = 0;
    private Random rand = new Random();
    
    private int height;
    private int width;
    private int regen;
    
    private static int left_height = 39;
    
    public void blit(int x, int y, int textureX, int textureY, int width, int height) {
        MinecraftClient.getInstance().inGameHud.blit(x, y, textureX, textureY, width, height);
    }
    
    /* HUD */
    public void renderHealthbar() {
        Entity renderViewEnity = this.mc.getCameraEntity();
        if (!(renderViewEnity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) renderViewEnity;
        
        // extra setup stuff from us
        //        left_height = ForgeIngameGui.left_height;
        this.width = this.mc.window.getScaledWidth();
        this.height = this.mc.window.getScaledHeight();
        int updateCounter = this.mc.inGameHud.getTicks();
        
        // start default forge/mc rendering
        // changes are indicated by comment
        this.mc.getProfiler().push("health");
        GlStateManager.enableBlend();
        
        int health = MathHelper.ceil(player.getHealth());
        boolean highlight = this.healthUpdateCounter > (long) updateCounter && (this.healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;
        
        if (health < this.playerHealth && player.timeUntilRegen > 0) {
            this.lastSystemTime = SystemUtil.getMeasuringTimeMs();
            this.healthUpdateCounter = updateCounter + 20;
        } else if (health > this.playerHealth && player.timeUntilRegen > 0) {
            this.lastSystemTime = SystemUtil.getMeasuringTimeMs();
            this.healthUpdateCounter = updateCounter + 10;
        }
        
        if (SystemUtil.getMeasuringTimeMs() - this.lastSystemTime > 1000L) {
            this.playerHealth = health;
            this.lastPlayerHealth = health;
            this.lastSystemTime = SystemUtil.getMeasuringTimeMs();
        }
        
        this.playerHealth = health;
        int healthLast = this.lastPlayerHealth;
        
        EntityAttributeInstance attrMaxHealth = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        float healthMax = (float) attrMaxHealth.getValue();
        float absorb = MathHelper.ceil(player.getAbsorptionAmount());
        
        // CHANGE: simulate 10 hearts max if there's more, so vanilla only renders one row max
        healthMax = Math.min(healthMax, 20f);
        health = Math.min(health, 20);
        absorb = Math.min(absorb, 20);
        
        int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        
        this.rand.setSeed(updateCounter * 312871);
        
        int left = this.width / 2 - 91;
        int top = this.height - left_height;
        left_height += (healthRows * rowHeight);
        if (rowHeight != 10) {
            left_height += 10 - rowHeight;
        }
        
        this.regen = -1;
        if (player.hasStatusEffect(StatusEffects.REGENERATION)) {
            this.regen = updateCounter % 25;
        }
        
        final int TOP = 9 * (this.mc.world.getLevelProperties().isHardcore() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        int MARGIN = 16;
        if (player.hasStatusEffect(StatusEffects.POISON)) {
            MARGIN += 36;
        } else if (player.hasStatusEffect(StatusEffects.WITHER)) {
            MARGIN += 72;
        }
        float absorbRemaining = absorb;
        
        for (int i = MathHelper.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
            //int b0 = (highlight ? 1 : 0);
            int row = MathHelper.ceil((float) (i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * rowHeight;
            
            if (health <= 4) {
                y += this.rand.nextInt(2);
            }
            if (i == this.regen) {
                y -= 2;
            }
            
            this.blit(x, y, BACKGROUND, TOP, 9, 9);
            
            if (highlight) {
                if (i * 2 + 1 < healthLast) {
                    this.blit(x, y, MARGIN + 54, TOP, 9, 9); //6
                } else if (i * 2 + 1 == healthLast) {
                    this.blit(x, y, MARGIN + 63, TOP, 9, 9); //7
                }
            }
            
            if (absorbRemaining > 0.0F) {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                    this.blit(x, y, MARGIN + 153, TOP, 9, 9); //17
                    absorbRemaining -= 1.0F;
                } else {
                    this.blit(x, y, MARGIN + 144, TOP, 9, 9); //16
                    absorbRemaining -= 2.0F;
                }
            } else {
                if (i * 2 + 1 < health) {
                    this.blit(x, y, MARGIN + 36, TOP, 9, 9); //4
                } else if (i * 2 + 1 == health) {
                    this.blit(x, y, MARGIN + 45, TOP, 9, 9); //5
                }
            }
        }
        
        this.renderExtraHearts(left, top, player);
        this.renderExtraAbsorption(left, top - rowHeight, player);
        
        this.mc.getTextureManager().bindTexture(ICON_VANILLA);
        // TODO figure out left_height
        //        ForgeIngameGui.left_height += 10;
        //        if (absorb > 0) {
        //            ForgeIngameGui.left_height += 10;
        //        }
        
        GlStateManager.disableBlend();
        this.mc.getProfiler().pop();
    }
    
    private void renderExtraHearts(int xBasePos, int yBasePos, PlayerEntity player) {
        int potionOffset = this.getPotionOffset(player);
        
        // Extra hearts
        this.mc.getTextureManager().bindTexture(ICON_HEARTS);
        
        int hp = MathHelper.ceil(player.getHealth());
        this.renderCustomHearts(xBasePos, yBasePos, potionOffset, hp, false);
    }
    
    private void renderCustomHearts(int xBasePos, int yBasePos, int potionOffset, int count, boolean absorb) {
        int regenOffset = absorb ? 10 : 0;
        for (int iter = 0; iter < count / 20; iter++) {
            int renderHearts = (count - 20 * (iter + 1)) / 2;
            int heartIndex = iter % 11;
            if (renderHearts > 10) {
                renderHearts = 10;
            }
            for (int i = 0; i < renderHearts; i++) {
                int y = this.getYRegenOffset(i, regenOffset);
                if (absorb) {
                    this.blit(xBasePos + 8 * i, yBasePos + y, 0, 54, 9, 9);
                }
                this.blit(xBasePos + 8 * i, yBasePos + y, 18 * heartIndex, potionOffset, 9, 9);
            }
            if (count % 2 == 1 && renderHearts < 10) {
                int y = this.getYRegenOffset(renderHearts, regenOffset);
                if (absorb) {
                    this.blit(xBasePos + 8 * renderHearts, yBasePos + y, 0, 54, 9, 9);
                }
                this.blit(xBasePos + 8 * renderHearts, yBasePos + y, 9 + 18 * heartIndex, potionOffset, 9, 9);
            }
        }
    }
    
    private int getYRegenOffset(int i, int offset) {
        return i + offset == this.regen ? -2 : 0;
    }
    
    private int getPotionOffset(PlayerEntity player) {
        int potionOffset = 0;
        StatusEffectInstance potion = player.getStatusEffect(StatusEffects.WITHER);
        if (potion != null) {
            potionOffset = 18;
        }
        potion = player.getStatusEffect(StatusEffects.POISON);
        if (potion != null) {
            potionOffset = 9;
        }
        if (this.mc.world.getLevelProperties().isHardcore()) {
            potionOffset += 27;
        }
        return potionOffset;
    }
    
    private void renderExtraAbsorption(int xBasePos, int yBasePos, PlayerEntity player) {
        int potionOffset = this.getPotionOffset(player);
        
        // Extra hearts
        this.mc.getTextureManager().bindTexture(ICON_ABSORB);
        
        int absorb = MathHelper.ceil(player.getAbsorptionAmount());
        this.renderCustomHearts(xBasePos, yBasePos, potionOffset, absorb, true);
    }
}
