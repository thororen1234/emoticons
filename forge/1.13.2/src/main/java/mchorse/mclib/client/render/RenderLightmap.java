package mchorse.mclib.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/**
 * Borrows vanilla's living-entity renderer purely for its brightness and
 * nameplate helpers, which are otherwise protected.
 *
 * <p>Unlike the Ornithe 1.14.4 port — where incomplete Feather mappings forced
 * these to be no-op stubs (Ornithe 1.14.4 stubs) — MCP names the whole surface, so this is implemented
 * for real here.
 */
public class RenderLightmap extends RenderLivingBase<EntityLivingBase>
{
    private static RenderLightmap instance;

    public static void create()
    {
        instance = new RenderLightmap(Minecraft.getInstance().getRenderManager(), null, 0.0F);
    }

    public static boolean canRenderNamePlate(EntityLivingBase livingBase)
    {
        return instance != null && instance.canRenderName(livingBase);
    }

    public static boolean set(EntityLivingBase livingBase, float partialTicks)
    {
        return instance != null && instance.setBrightness(livingBase, partialTicks, true);
    }

    public static void unset()
    {
        if (instance != null)
        {
            instance.unsetBrightness();
        }
    }

    public static void renderNameplate(EntityLivingBase livingBase, String name, double x, double y, double z)
    {
        if (instance != null && instance.canRenderName(livingBase))
        {
            instance.renderLivingLabel(livingBase, name, x, y, z, 64);
        }
    }

    public RenderLightmap(RenderManager manager, ModelBase model, float shadowSize)
    {
        super(manager, model, shadowSize);
    }

    @Override
    protected int getColorMultiplier(EntityLivingBase entity, float brightness, float partialTicks)
    {
        return 0;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLivingBase entity)
    {
        return null;
    }
}
