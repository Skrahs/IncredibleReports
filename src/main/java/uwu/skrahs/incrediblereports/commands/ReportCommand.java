package uwu.skrahs.incrediblereports.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import uwu.skrahs.incrediblereports.manager.ConfigManager;
import uwu.skrahs.incrediblereports.utils.ChatUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReportCommand implements SimpleCommand {

    private final ProxyServer proxy;
    private final ConfigManager configManager;
    private final Cache<UUID, Long> cooldown;

    @Inject
    public ReportCommand(ProxyServer proxy, ConfigManager configManager) {
        this.proxy = proxy;
        this.configManager = configManager;
        this.cooldown = CacheBuilder.newBuilder().expireAfterWrite(configManager.getConfig("config").get().getInt("cooldown"), TimeUnit.SECONDS).build();
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        YamlDocument config = configManager.getConfig("config").get();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtils.color(config.getString("messages.player_only_command")));
            return;
        }

        if(cooldown.asMap().containsKey(player.getUniqueId())){
            player.sendMessage(ChatUtils.color(config.getString("messages.cooldown")));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ChatUtils.color(config.getString("messages.correct_usage")));
            return;
        }

        String reportedPlayerName = args[0];
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Player reportedPlayer = proxy.getPlayer(reportedPlayerName).orElse(null);
        if (reportedPlayer == null) {
            player.sendMessage(ChatUtils.color(String.format(config.getString("messages.player_not_online"), reportedPlayerName)));
            return;
        }

        if(reportedPlayer == player){
            player.sendMessage(ChatUtils.color(config.getString("messages.self_report")));
            return;
        }

        Component reportMessage = ChatUtils.color(config.getString("messages.alert")
                .replace("%player%", player.getUsername())
                .replace("%reported%", reportedPlayer.getUsername())
                .replace("%reason%", reason)
        );
        proxy.getAllPlayers().stream()
                .filter(p -> p.hasPermission("incrediblereports.receivereports"))
                .forEach(p -> p.sendMessage(reportMessage));

        cooldown.put(player.getUniqueId(), config.getInt("cooldown")*1000L);

        player.sendMessage(ChatUtils.color(config.getString("messages.report_sent_successfully")));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> list = new ArrayList<>();
        for(Player player : proxy.getAllPlayers()){
            list.add(player.getUsername());
        }
        return list;
    }
}