package uwu.skrahs.incrediblereports.manager;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class ConfigManager {

    private Path directory;
    private HashMap<String, YamlDocument> configs = new HashMap<>();

    public ConfigManager(Path directory) {
        this.directory = directory;

        try {
            registerConfig("config");
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    private void registerConfig(String name) throws Exception {

        YamlDocument config = YamlDocument.create(new File(directory.toFile(), name + ".yml"),
                Objects.requireNonNull(getClass().getResourceAsStream("/" + name + ".yml")),
                GeneralSettings.DEFAULT,
                LoaderSettings.builder().setAutoUpdate(true).build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().setVersioning(new BasicVersioning("configuration-version"))
                        .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build());

        config.update();
        config.save();

        if (!configs.containsKey(name))
            configs.put(name, config);
    }

    public Optional<YamlDocument> getConfig(String name) {
        return Optional.ofNullable(this.configs.get(name));
    }

}