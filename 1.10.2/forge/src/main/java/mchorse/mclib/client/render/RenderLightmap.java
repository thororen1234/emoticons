package mchorse.mclib.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderLightmap
        extends RenderLivingBase {
    private static RenderLightmap instance;

    public static void create() {
        instance = new RenderLightmap(Minecraft.getMinecraft().getRenderManager(), null, 0.0f);
    }

    public static boolean canRenderNamePlate(EntityLivingBase livingBase) {
        return instance.canRenderName(livingBase);
    }

    public static boolean set(EntityLivingBase livingBase, float f) {
        return instance.setBrightness(livingBase, f, true);
    }

    public static void unset() {
        instance.unsetBrightness();
    }

    public static void renderNameplate(EntityLivingBase livingBase, String name, double x, double y, double z) {
        instance.renderLivingLabel(livingBase, name, x, y, z, 64);
    }

    public RenderLightmap(RenderManager renderManager, ModelBase modelBase, float f) {
        super(renderManager, modelBase, f);
    }

    @Override
    protected int getColorMultiplier(EntityLivingBase entity, float f, float f2) {
        return 0;
    }

    protected ResourceLocation getEntityTexture(EntityLivingBase entity) {
        return null;
    }

    protected /* synthetic */ ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityLivingBase) entity);
    }
}
