package mchorse.emoticons.skin_n_bones.api.animation;

import com.google.common.collect.ImmutableSet;

import net.minecraft.resource.Identifier;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class AnimationPack {
	public static Set<String> DOMAINS = ImmutableSet.of("s&b");
	public File config;
	public InputStream stream;

	public AnimationPack(File file) {
		this.config = file;
		this.config.mkdirs();
	}

	public InputStream getInputStream(Identifier location) throws IOException {
		if (this.stream != null) {
			InputStream inputStream = this.stream;
			this.stream = null;
			return inputStream;
		}
		return new FileInputStream(new File(this.config, location.getPath()));
	}

	public boolean resourceExists(Identifier Identifier) {
		if (new File(this.config, Identifier.getPath()).exists()) {
			return true;
		}
		this.stream = AnimationPack.class
				.getResourceAsStream("/assets/skin_n_bones/models/" + Identifier.getPath());
		return this.stream != null;
	}

	public Set<String> getResourceDomains() {
		return DOMAINS;
	}

	public Object getPackMetadata(Object serializer, String string) throws IOException {
		return null;
	}

	public BufferedImage getPackImage() throws IOException {
		return null;
	}

	public String getPackName() {
		return "Skin&Bones animation pack";
	}
}
