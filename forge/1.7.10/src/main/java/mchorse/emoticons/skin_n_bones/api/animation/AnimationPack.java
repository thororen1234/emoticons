package mchorse.emoticons.skin_n_bones.api.animation;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class AnimationPack
        implements IResourcePack {
    public static Set<String> DOMAINS = ImmutableSet.of("s&b");
    public File config;
    public InputStream stream;

    public AnimationPack(File file) {
        this.config = file;
        this.config.mkdirs();
    }

    @Override
    public InputStream getInputStream(ResourceLocation location) {
        if (this.stream != null) {
            InputStream inputStream = this.stream;
            this.stream = null;
            return inputStream;
        }
        try {
            return new FileInputStream(new File(this.config, location.getResourcePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean resourceExists(ResourceLocation resourceLocation) {
        if (new File(this.config, resourceLocation.getResourcePath()).exists()) {
            return true;
        }
        this.stream = AnimationPack.class
                .getResourceAsStream("/assets/skin_n_bones/models/" + resourceLocation.getResourcePath());
        return this.stream != null;
    }

    @Override
    public Set<String> getResourceDomains() {
        return DOMAINS;
    }

    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer serializer, String string) {
        return null;
    }

    @Override
    public BufferedImage getPackImage() {
        return null;
    }

    @Override
    public String getPackName() {
        return "Skin&Bones animation pack";
    }
}
