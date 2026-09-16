package mchorse.emoticons.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class SaltParticle extends EntityFX {
    public static final ResourceLocation PARTICLES = new ResourceLocation("emoticons", "textures/particles.png");
    public static ModelRenderer salt;

    public SaltParticle(World world, double x, double y, double z, double motionY) {
        super(world, x, y, z);
        this.particleGravity = 0.5f;
        this.particleScale = 0.5f;
        this.particleMaxAge = 20 + this.rand.nextInt(10);
        this.motionX = this.rand.nextFloat() * 0.05f;
        this.motionZ = this.rand.nextFloat() * 0.05f;
        this.motionY = motionY;

        if (salt == null) {
            SaltParticleModelBase model = new SaltParticleModelBase();
            model.textureWidth = 64;
            model.textureHeight = 64;

            salt = new ModelRenderer(model, 0, 0);
            salt.addBox(-0.25f, -0.25f, 0.25f, 1, 1, 1);
        }
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float rotationX, float rotationZ,
                               float rotationYZ, float rotationXY, float rotationXZ) {
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

        int remaining = this.particleMaxAge - this.particleAge;
        float scale = 0.5f * (remaining < 5 ? (float) remaining / 5.0f : 1.0f);

        Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLES);

        int brightness = this.getBrightnessForRender(partialTicks);
        if (this.isBurning()) {
            brightness = 0xF000F0;
        }

        int lightU = brightness % 65536;
        int lightV = brightness / 65536;

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightU, (float) lightV);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glScalef(scale, scale, scale);
        RenderHelper.enableStandardItemLighting();
        salt.render(0.0625f);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
