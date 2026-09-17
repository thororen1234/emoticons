package mchorse.emoticons.client.particles;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Small textured cubes rendered in the particle manager's unbatched model pass.
 */
abstract class ModelParticle extends Particle
{
    public static final ResourceLocation PARTICLES = new ResourceLocation("emoticons", "textures/particles.png");

    private static final ModelRenderer[] MODELS = createModels();

    private final ModelRenderer model;
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

    private static ModelRenderer[] createModels()
    {
        ModelBase base = new ModelBase()
        {};

        ModelRenderer[] models = new ModelRenderer[4];

        for (int i = 0; i < models.length; i ++)
        {
            models[i] = new ModelRenderer(base, 0, i * 2);
            models[i].addBox(-0.5F, -0.5F, 0.5F, 1, 1, 1);
        }

        return models;
    }

    /**
     * 1.13.2 predates {@code IParticleRenderType}: the particle manager still
     * dispatches on an int FX layer, where 3 is the unbatched "custom" pass that
     * lets this particle bind its own texture and render a model.
     */
    @Override
    public int getFXLayer()
    {
        return 3;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity camera, float partialTicks,
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
