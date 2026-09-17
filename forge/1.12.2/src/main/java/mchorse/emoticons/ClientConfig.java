package mchorse.emoticons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Client preferences stored independently of Minecraft's key bindings.
 *
 * Backport of the {@code ClientConfig} used by the newer ports of this mod,
 * persisted as {@code config.json} next to {@code keys.json} inside the mod's
 * configuration folder.
 */
public final class ClientConfig
{
    public static ClientConfig instance = new ClientConfig();

    public boolean disableAnimations = false;
    public boolean stopOnMove = true;
    public boolean thirdPerson = true;
    public boolean sounds = true;
    public float volume = 1.0F;
    public String model = "default";

    private static Gson gson()
    {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    private static File file()
    {
        return new File(ClientProxy.configFolder, "config.json");
    }

    public static void load()
    {
        File file = file();

        if (file.isFile())
        {
            Reader reader = null;

            try
            {
                reader = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));

                ClientConfig config = gson().fromJson(reader, ClientConfig.class);

                if (config != null)
                {
                    instance = config;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                close(reader);
            }
        }
        else
        {
            /* One time migration of the volume which used to live in
             * keys.json, so that existing users don't lose their setting */
            if (ClientProxy.keys != null)
            {
                instance.volume = ClientProxy.keys.volume;
            }

            save();
        }

        sanitize();
    }

    public static void save()
    {
        Writer writer = null;

        try
        {
            writer = new OutputStreamWriter(new FileOutputStream(file()), Charset.forName("UTF-8"));
            gson().toJson(instance, writer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(writer);
        }
    }

    private static void sanitize()
    {
        if (instance.model == null || instance.model.isEmpty())
        {
            instance.model = "default";
        }

        instance.volume = Math.max(0.0F, Math.min(1.0F, instance.volume));
    }

    private static void close(java.io.Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {}
        }
    }
}
