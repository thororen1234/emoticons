package mchorse.mclib.utils.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import java.io.IOException;

public class RLUtils {
	public static Identifier create(String string) {
		if (string.startsWith("blockbuster.actors:")) {
			string = "b.a" + string.substring(18);
		}
		return Identifier.of(string);
	}

	public static Identifier create(String s, final String s2) {
		if (s.equals("blockbuster.actors")) {
			s = "b.a";
		}
		return Identifier.of(s, s2);
	}

	public static Identifier create(final NbtElement element) {
		if (element instanceof NbtList) {
			final NbtList tagList = (NbtList) element;
			if (!tagList.isEmpty()) {
				return create(tagList.getString(0));
			}
		} else if (element instanceof NbtString) {
			return create(((NbtString) element).asString());
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
					return create(value.getAsString());
				}
			}
		} else if (jsonElement.isJsonPrimitive()) {
			return create(jsonElement.getAsString());
		}
		return null;
	}

	public static NbtElement writeNbt(final Identifier location) {
		if (location != null) {
			return NbtString.of(location.toString());
		}
		return null;
	}

	public static JsonElement writeJson(final Identifier location) {
		if (location != null) {
			return new JsonPrimitive(location.toString());
		}
		return JsonNull.INSTANCE;
	}

	public static Identifier clone(final Identifier location) {
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