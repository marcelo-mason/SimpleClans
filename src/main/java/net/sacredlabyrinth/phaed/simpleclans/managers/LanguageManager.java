package net.sacredlabyrinth.phaed.simpleclans.managers;


import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public class LanguageManager {
    private File file;
    private HashMap<String, String> language;
    private String[] comments = new String[]{};

    public LanguageManager() {
        load();
    }

    public void load() {
        file = new File(SimpleClans.getInstance().getDataFolder() + File.separator + "language.yml");
        check();
    }

    private void check() {
        boolean exists = (file).exists();

        loadDefaults();

        if (exists) {
            loadFile();
        }

        saveFile();
    }

    private void loadDefaults() {
        try {
            InputStream defaultLanguage = getClass().getResourceAsStream("/language.yml");
            language = (HashMap<String, String>) new Yaml().load(defaultLanguage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        try {
            InputStream fileLanguage = new FileInputStream(file);
            language = (HashMap<String, String>) new Yaml().load(fileLanguage);
        } catch (FileNotFoundException e) {
            // file not found
        }
    }

    private void saveFile() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setWidth(99999999);
        options.setAllowUnicode(true);

        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

            try {
                new Yaml(options).dump(language, out);

                for (String comment : comments) {
                    out.write(comment);
                }
            } finally {
                out.close();
            }
        } catch (IOException e) {
            // could not save
            e.printStackTrace();
        }
    }

    public String get(String key) {
        Object o = language.get(key);

        if (o != null) {
            return o.toString();
        }

        return null;
    }
}
