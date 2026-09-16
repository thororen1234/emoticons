package mchorse.emoticons.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class PopcornParticle extends EntityFX {
    public static ModelRenderer kernel1;
    public static ModelRenderer kernel2;
    public static ModelRenderer kernel3;
    protected int color;

    public PopcornParticle(World world, double x, double y, double z, double motionY) {
        super(world, x, y, z);
        this.particleGravity = 0.5f;
        this.particleScale = 0.5f;
        this.particleMaxAge = 20 + this.rand.nextInt(10);
        this.motionX = this.rand.nextFloat() * 0.05f;
        this.motionZ = this.rand.nextFloat() * 0.05f;
        this.motionY = motionY == 0.0 ? motionY : this.rand.nextDouble() * 0.1f + motionY;

        if (kernel1 == null) {
            PopcornParticleModelBase model = new PopcornParticleModelBase();
            model.textureWidth = 64;
            model.textureHeight = 64;

            kernel1 = new ModelRenderer(model, 0, 2);
            kernel1.addBox(-0.5f, -0.5f, 0.5f, 1, 1, 1);
            kernel2 = new ModelRenderer(model, 0, 4);
            kernel2.addBox(-0.5f, -0.5f, 0.5f, 1, 1, 1);
            kernel3 = new ModelRenderer(model, 0, 6);
            kernel3.addBox(-0.5f, -0.5f, 0.5f, 1, 1, 1);
        }

        this.color = this.rand.nextInt(2);
    }

    @Override
    public void renderParticle(WorldRenderer worldRenderer, Entity viewEntity, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

        int remaining = this.particleMaxAge - this.particleAge;
        float scale = 0.75f * (remaining < 5 ? (float) remaining / 5.0f : 1.0f);

        ModelRenderer kernel = kernel1;
        if (this.color == 1) {
            kernel = kernel2;
        } else if (this.color == 2) {
            kernel = kernel3;
        }

        GlStateManager.enableTexture2D();
        Minecraft.getMinecraft().getTextureManager().bindTexture(SaltParticle.PARTICLES);

        int brightness = viewEntity.getBrightnessForRender(partialTicks);
        if (viewEntity.isBurning()) {
            brightness = 0xF000F0;
        }

        int lightU = brightness % 65536;
        int lightV = brightness / 65536;

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightU, (float) lightV);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(scale, scale, scale);
        RenderHelper.enableStandardItemLighting();
        kernel.render(0.0625f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
