package com.craftmend.openaudiomc.modules.configuration;

import com.craftmend.openaudiomc.OpenAudioMc;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigurationModule {

    @Getter private String server = "http://192.168.2.25";
    @Getter private FileConfiguration mainConfig;
    @Getter private FileConfiguration dataConfig;

    public ConfigurationModule(OpenAudioMc openAudioMc) {
        //save default
        openAudioMc.saveConfig();
        openAudioMc.saveResource("data.yml", false);

        dataConfig = YamlConfiguration.loadConfiguration(new File("plugins/OpenAudioMc/data.yml"));

        System.out.println("dataconfig is " + dataConfig);
        mainConfig = openAudioMc.getConfig();

        System.out.println(OpenAudioMc.getLOG_PREFIX() + "Starting configurateion module");
    }

    public void saveAll() throws IOException {
        OpenAudioMc.getInstance().saveConfig();
        dataConfig.save("plugins/OpenAudioMc/data.yml");
    }

}