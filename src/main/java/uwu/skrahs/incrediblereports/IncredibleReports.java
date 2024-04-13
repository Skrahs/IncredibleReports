package uwu.skrahs.incrediblereports;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import uwu.skrahs.incrediblereports.commands.ReportCommand;
import uwu.skrahs.incrediblereports.manager.ConfigManager;

import java.nio.file.Path;

@Plugin(
        id = "incrediblereports",
        name = "IncredibleReports",
        version = BuildConstants.VERSION
)
public class IncredibleReports {

    private final ProxyServer proxy;
    private final Logger logger;
    private ConfigManager configManager;
    private Path directory;

    @Inject
    public IncredibleReports(ProxyServer proxy, Logger logger, @DataDirectory Path directory) {
        this.proxy = proxy;
        this.logger = logger;
        this.directory = directory;
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        configManager = new ConfigManager(directory);

        proxy.getCommandManager().register("report", new ReportCommand(proxy, configManager));
        logger.info("Il plugin e' stato caricato con successo");

    }
}
