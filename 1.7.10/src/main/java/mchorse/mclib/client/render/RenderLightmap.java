package mchorse.mclib.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderLightmap
        extends RendererLivingEntity {
    private static RenderLightmap instance;

    public static void create() {
        instance = new RenderLightmap(null, 0.0f);
        instance.setRenderManager(RenderManager.instance);
    }

    public static boolean canRenderNamePlate(EntityLivingBase livingBase) {
        return instance.func_110813_b(livingBase);
    }

    public static boolean set(EntityLivingBase livingBase, float partialTicks) {
        int brightness = livingBase.getBrightnessForRender(partialTicks);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (brightness % 65536), (float) (brightness / 65536));
        return true;
    }

    public static void unset() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) OpenGlHelper.lastBrightnessX, (float) OpenGlHelper.lastBrightnessY);
    }

    public static void renderNameplate(EntityLivingBase livingBase, String name, double x, double y, double z) {
        instance.func_147906_a(livingBase, name, x, y, z, 64);
    }

    public RenderLightmap(ModelBase modelBase, float f) {
        super(modelBase, f);
    }

    @Override
    protected int getColorMultiplier(EntityLivingBase entity, float f, float f2) {
        return 0;
    }

    protected ResourceLocation getEntityTexture(EntityLivingBase entity) {
        return null;
    }

    public /* synthetic */ ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityLivingBase) entity);
    }
}
