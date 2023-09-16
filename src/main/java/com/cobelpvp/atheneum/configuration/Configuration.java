package com.cobelpvp.atheneum.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cobelpvp.atheneum.configuration.annotations.ConfigData;
import com.cobelpvp.atheneum.configuration.annotations.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {
    private YamlConfiguration config;
    private JavaPlugin plugin;
    private File file;
    private File directory;

    public Configuration(JavaPlugin plugin) {
        this(plugin, "config.yml");
    }

    public Configuration(JavaPlugin plugin, String filename) {
        this(plugin, filename, plugin.getDataFolder().getPath());
    }

    public Configuration(JavaPlugin plugin, String filename, String directory) {
        this.plugin = plugin;
        this.directory = new File(directory);
        this.file = new File(directory, filename);
        this.config = new YamlConfiguration();
        this.createFile();
    }

    public void createFile() {
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }

        try {
            this.config.load(this.file);
        } catch (InvalidConfigurationException | IOException var2) {
            var2.printStackTrace();
        }

    }

    public void save() {
        Field[] toSave = this.getClass().getDeclaredFields();
        Field[] var2 = toSave;
        int var3 = toSave.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Field f = var2[var4];
            if (f.isAnnotationPresent(ConfigData.class)) {
                ConfigData configData = (ConfigData)f.getAnnotation(ConfigData.class);

                try {
                    f.setAccessible(true);
                    Object saveValue = f.get(this);
                    Object configValue = null;
                    if (f.isAnnotationPresent(ConfigSerializer.class)) {
                        ConfigSerializer serializer = (ConfigSerializer)f.getAnnotation(ConfigSerializer.class);
                        if (saveValue instanceof List) {
                            configValue = new ArrayList();
                            Iterator var10 = ((List)configValue).iterator();

                            while(var10.hasNext()) {
                                Object o = var10.next();
                                AbstractSerializer as = (AbstractSerializer)serializer.serializer().newInstance();
                                ((List)configValue).add(as.toString(o));
                            }
                        } else {
                            AbstractSerializer as = (AbstractSerializer)serializer.serializer().newInstance();
                            configValue = as.toString(saveValue);
                        }
                    } else if (saveValue instanceof List) {
                        configValue = new ArrayList();
                        Iterator var15 = ((List)saveValue).iterator();

                        while(var15.hasNext()) {
                            Object o = var15.next();
                            ((List)((List)configValue)).add(o.toString());
                        }
                    }

                    if (configValue == null) {
                        configValue = saveValue;
                    }

                    this.config.addDefault(configData.path(), configValue);
                    this.config.set(configData.path(), configValue);
                    System.out.println("Setting: " + configData.path() + " to " + saveValue);
                } catch (InstantiationException | IllegalAccessException var14) {
                    var14.printStackTrace();
                }
            }
        }

        try {
            this.config.save(this.file);
        } catch (IOException var13) {
            var13.printStackTrace();
        }

    }

    public void load() {
        Field[] toLoad = this.getClass().getDeclaredFields();
        Field[] var2 = toLoad;
        int var3 = toLoad.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Field f = var2[var4];
            f.setAccessible(true);
            System.out.println("Loading field: " + f.getName());
            if (f.isAnnotationPresent(ConfigData.class)) {
                ConfigData configData = (ConfigData)f.getAnnotation(ConfigData.class);
                System.out.println("Loading data: " + configData.path());
                if (this.config.contains(configData.path())) {
                    f.setAccessible(true);
                    if (!f.isAnnotationPresent(ConfigSerializer.class)) {
                        try {
                            System.out.println("Setting data: " + this.config.get(configData.path()));
                            if (this.config.isList(configData.path())) {
                                f.set(this, this.config.getList(configData.path()));
                            } else {
                                f.set(this, this.config.get(configData.path()));
                            }
                        } catch (IllegalAccessException var11) {
                            var11.printStackTrace();
                        }
                    } else if (this.config.isList(configData.path())) {
                        try {
                            List<String> list = this.config.getStringList(configData.path());
                            List<Object> deserializedList = new ArrayList();
                            Iterator var9 = list.iterator();

                            while(var9.hasNext()) {
                                String s = (String)var9.next();
                                deserializedList.add(this.deserializeValue(f, s));
                            }

                            f.set(this, deserializedList);
                        } catch (InstantiationException | IllegalAccessException var13) {
                            System.out.println("Error reading list in configuration file: " + this.config.getName() + " path: " + configData.path());
                            var13.printStackTrace();
                        }
                    } else {
                        try {
                            Object object = this.config.get(configData.path());
                            f.set(this, this.deserializeValue(f, object.toString()));
                        } catch (InstantiationException | IllegalAccessException var12) {
                            System.out.println("Error reading value in configuration file: " + this.config.getName() + " path: " + configData.path());
                            var12.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    public File getFile() {
        return this.file;
    }

    public Object deserializeValue(Field f, Object value) throws IllegalAccessException, InstantiationException {
        AbstractSerializer serializer = (AbstractSerializer)((ConfigSerializer)f.getAnnotation(ConfigSerializer.class)).serializer().newInstance();
        return serializer.fromString(value.toString());
    }
}
