package mchorse.mclib.utils.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.resource.Resource;
import net.minecraft.resource.manager.ResourceManager;
import net.minecraft.client.resource.metadata.serializer.ResourceMetadataSerializer;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.resource.Identifier;
import java.io.InputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class RLUtils {
	public static Resource getStreamForMultiskin(final MultiResourceLocation multi) throws IOException {
		if (multi.children.isEmpty()) {
			throw new IOException("Multi-skin is empty!");
		}
		try {
			final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			final java.awt.image.BufferedImage read = null;
			final Graphics graphics = read.getGraphics();
			for (int i = 1; i < multi.children.size(); ++i) {
				final Identifier location = multi.children.get(i);
				try {
					
				} catch (final Exception ex) {
				}
			}
			graphics.dispose();
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(read, "png", output);
			return new Resource() {
				@Override
				public void close() throws IOException {}
				@Override
				public Identifier getLocation() { return multi; }
				@Override
				public InputStream asStream() { return new ByteArrayInputStream(output.toByteArray()); }
				public boolean hasMetadata() { return false; }
				public <T> T getMetadata(ResourceMetadataSerializer<T> reader) { return null; }
				@Override
				public String getSourceName() { return "McLib multiskin handler"; }
			};
		} catch (final IOException ex2) {
			throw ex2;
		} catch (final Exception ex3) {
			throw new IOException(ex3.getMessage());
		}
	}

	public static Identifier create(String string) {
		if (string.startsWith("blockbuster.actors:")) {
			string = "b.a" + string.substring(18);
		}
		return new Identifier(string);
	}

	public static Identifier create(String s, final String s2) {
		if (s.equals("blockbuster.actors")) {
			s = "b.a";
		}
		return new Identifier(s, s2);
	}

	public static Identifier create(final NbtElement NbtElement) {
		if (NbtElement instanceof NbtList) {
			final NbtList tagList = (NbtList) NbtElement;
			if (!tagList.isEmpty()) {
				final MultiResourceLocation multi = new MultiResourceLocation(tagList.getString(0));
				for (int i = 1; i < tagList.size(); ++i) {
					multi.children.add(create(tagList.getString(i)));
				}
				return multi;
			}
		} else if (NbtElement instanceof NbtString) {
			return create(((NbtString) NbtElement).asString());
		}
		return null;
	}

	public static Identifier create(final JsonElement jsonElement) {
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

	public static NbtElement writeNbt(final Identifier location) {
		if (location instanceof MultiResourceLocation) {
			final MultiResourceLocation multi = (MultiResourceLocation) location;
			final NbtList tagList = new NbtList();
			final Iterator iterator = multi.children.iterator();
			while (iterator.hasNext()) {
				tagList.add(new NbtString(((Identifier) iterator.next()).toString()));
			}
			return tagList;
		}
		if (location != null) {
			return new NbtString(location.toString());
		}
		return null;
	}

	public static JsonElement writeJson(final Identifier location) {
		if (location instanceof MultiResourceLocation) {
			final MultiResourceLocation multi = (MultiResourceLocation) location;
			final JsonArray jsonArray = new JsonArray();
			final Iterator iterator = multi.children.iterator();
			while (iterator.hasNext()) {
				jsonArray.add(new JsonPrimitive(((Identifier) iterator.next()).toString()));
			}
			return jsonArray;
		}
		if (location != null) {
			return new JsonPrimitive(location.toString());
		}
		return JsonNull.INSTANCE;
	}

	public static Identifier clone(final Identifier location) {
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

	public static Identifier createActor(final String str, final String str2) {
		if (str.isEmpty()) {
			return null;
		}
		if (str.indexOf(":") == -1) {
			return create("b.a", ((str.indexOf("/") == -1) ? (str2 + "/") : "") + str);
		}
		return create(str);
	}

	public static String getFileName(final Identifier location) {
		if (location == null) {
			return "";
		}
		if (location.getNamespace().equals("b.a")) {
			final String[] split = location.getPath().split("/");
			return split[split.length - 1];
		}
		return location.toString();
	}
}


