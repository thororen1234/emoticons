package mchorse.mclib.utils.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class RLUtils {
    public static IResource getStreamForMultiskin(final MultiResourceLocation multi) throws IOException {
        if (multi.children.isEmpty()) {
            throw new IOException("Multi-skin is empty!");
        }
        try {
            final IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
            final BufferedImage read = ImageIO.read(resourceManager.getResource(multi.children.get(0)).getInputStream());
            final Graphics graphics = read.getGraphics();
            for (int i = 1; i < multi.children.size(); ++i) {
                final ResourceLocation location = multi.children.get(i);
                try {
                    graphics.drawImage(ImageIO.read(resourceManager.getResource(location).getInputStream()), 0, 0, null);
                } catch (final Exception ex) {
                }
            }
            graphics.dispose();
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(read, "png", output);
            return new SimpleResource(multi, new ByteArrayInputStream(output.toByteArray()), null, null);
        } catch (final IOException ex2) {
            throw ex2;
        } catch (final Exception ex3) {
            throw new IOException(ex3.getMessage());
        }
    }

    public static ResourceLocation create(String string) {
        if (string.startsWith("blockbuster.actors:")) {
            string = "b.a" + string.substring(18);
        }
        return new ResourceLocation(string);
    }

    public static ResourceLocation create(String s, final String s2) {
        if (s.equals("blockbuster.actors")) {
            s = "b.a";
        }
        return new ResourceLocation(s, s2);
    }

    public static ResourceLocation create(final NBTBase NBTBase) {
        if (NBTBase instanceof NBTTagList) {
            final NBTTagList tagList = (NBTTagList) NBTBase;
            if (tagList.tagCount() > 0) {
                final MultiResourceLocation multi = new MultiResourceLocation(tagList.getStringTagAt(0));
                for (int i = 1; i < tagList.tagCount(); ++i) {
                    multi.children.add(create(tagList.getStringTagAt(i)));
                }
                return multi;
            }
        } else if (NBTBase instanceof NBTTagString) {
            return create(((NBTTagString) NBTBase).func_150285_a_());
        }
        return null;
    }

    public static ResourceLocation create(final JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            final JsonArray asJsonArray = jsonElement.getAsJsonArray();
            final int size = asJsonArray.size();
            if (size > 0) {
                final JsonElement value = asJsonArray.get(0);
                if (value.isJsonPrimitive()) {
                    final MultiResourceLocation multi = new MultiResourceLocation(value.getAsString());
                    for (int i = 1; i < size; ++i) {
                        multi.children.add(create(asJsonArray.get(i)));
                    }
                    return multi;
                }
            }
        } else if (jsonElement.isJsonPrimitive()) {
            return create(jsonElement.getAsString());
        }
        return null;
    }

    public static NBTBase writeNbt(final ResourceLocation location) {
        if (location instanceof MultiResourceLocation) {
            final MultiResourceLocation multi = (MultiResourceLocation) location;
            final NBTTagList tagList = new NBTTagList();
            final Iterator iterator = multi.children.iterator();
            while (iterator.hasNext()) {
                tagList.appendTag(new NBTTagString(((ResourceLocation) iterator.next()).toString()));
            }
            return tagList;
        }
        if (location != null) {
            return new NBTTagString(location.toString());
        }
        return null;
    }

    public static JsonElement writeJson(final ResourceLocation location) {
        if (location instanceof MultiResourceLocation) {
            final MultiResourceLocation multi = (MultiResourceLocation) location;
            final JsonArray jsonArray = new JsonArray();
            final Iterator iterator = multi.children.iterator();
            while (iterator.hasNext()) {
                jsonArray.add(new JsonPrimitive(((ResourceLocation) iterator.next()).toString()));
            }
            return jsonArray;
        }
        if (location != null) {
            return new JsonPrimitive(location.toString());
        }
        return JsonNull.INSTANCE;
    }

    public static ResourceLocation clone(final ResourceLocation location) {
        if (location instanceof MultiResourceLocation) {
            final MultiResourceLocation multi = (MultiResourceLocation) location;
            final MultiResourceLocation multi2 = new MultiResourceLocation(multi.toString());
            multi2.children.clear();
            multi2.children.addAll(multi.children);
            return multi2;
        }
        if (location != null) {
            return create(location.toString());
        }
        return null;
    }

    public static ResourceLocation createActor(final String str, final String str2) {
        if (str.isEmpty()) {
            return null;
        }
        if (str.indexOf(":") == -1) {
            return create("b.a", ((str.indexOf("/") == -1) ? (str2 + "/") : "") + str);
        }
        return create(str);
    }

    public static String getFileName(final ResourceLocation location) {
        if (location == null) {
            return "";
        }
        if (location.getResourceDomain().equals("b.a")) {
            final String[] split = location.getResourcePath().split("/");
            return split[split.length - 1];
        }
        return location.toString();
    }
}


