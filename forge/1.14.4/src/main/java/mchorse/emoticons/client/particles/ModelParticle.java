package mchorse.emoticons.client.particles;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Small textured cubes rendered in the particle manager's unbatched model pass.
 */
abstract class ModelParticle extends Particle
{
    public static final ResourceLocation PARTICLES = new ResourceLocation("emoticons", "textures/particles.png");

    private static final RendererModel[] MODELS = createModels();

    private final RendererModel model;
    private final float scale;

    protected ModelParticle(World world, double x, double y, double z, double motionY, float scale, int texture)
    {
        super(world, x, y, z);

        this.particleGravity = 0.5F;
        this.maxAge = 20 + this.rand.nextInt(10);
        this.motionX = this.rand.nextFloat() * 0.05F;
        this.motionZ = this.rand.nextFloat() * 0.05F;
        this.motionY = motionY;
        this.scale = scale;
        this.model = MODELS[texture];
    }

    private static RendererModel[] createModels()
    {
        EntityModel<Entity> base = new EntityModel<Entity>()
        {};

        RendererModel[] models = new RendererModel[4];

        for (int i = 0; i < models.length; i ++)
        {
            models[i] = new RendererModel(base, 0, i * 2);
            models[i].addBox(-0.5F, -0.5F, 0.5F, 1, 1, 1);
        }

        return models;
    }

    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.CUSTOM;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo camera, float partialTicks,
        float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float remaining = this.maxAge - this.age - partialTicks;
        float shrink = Math.max(0, Math.min(1, remaining / 5F));

        if (shrink == 0)
        {
            return;
        }

        int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean rescaleNormal = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);

        Minecraft.getInstance().getTextureManager().bindTexture(PARTICLES);

        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.pushMatrix();

        try
        {
            GlStateManager.translated(
                this.prevPosX + (this.posX - this.prevPosX) * partialTicks - Particle.interpPosX,
                this.prevPosY + (this.posY - this.prevPosY) * partialTicks - Particle.interpPosY,
                this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - Particle.interpPosZ);

            float size = this.scale * shrink;

            GlStateManager.scalef(size, size, size);
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableStandardItemLighting();

            this.model.render(1 / 16F);
        }
        finally
        {
            if (!lighting)
            {
                RenderHelper.disableStandardItemLighting();
            }

            if (!rescaleNormal)
            {
                GlStateManager.disableRescaleNormal();
            }

            GlStateManager.popMatrix();
            GlStateManager.bindTexture(previousTexture);
        }
    }
}
