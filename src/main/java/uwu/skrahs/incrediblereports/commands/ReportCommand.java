package uwu.skrahs.incrediblereports.commands;

import com.google.inject.Inject;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import uwu.skrahs.incrediblereports.manager.ConfigManager;
import uwu.skrahs.incrediblereports.utils.ChatUtils;
import com.velocitypowered.api.permission.PermissionSubject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportCommand implements SimpleCommand {

    private final ProxyServer proxy;
    private final ConfigManager configManager;

    @Inject
    public ReportCommand(ProxyServer proxy, ConfigManager configManager) {
        this.proxy = proxy;
        this.configManager = configManager;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        YamlDocument config = configManager.getConfig("config").get();

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.color(config.getString("messages.player_only_command")));
            return;
        }

        Player player = (Player) sender;

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
        Component reportMessage = ChatUtils.color(config.getString("messages.alert")
                .replace("%player%", player.getUsername())
                .replace("%reported%", reportedPlayer.getUsername())
                .replace("%reason%", reason)
        );
        proxy.getAllPlayers().stream()
                .filter(p -> p.hasPermission("incrediblereports.receivereports"))
                .forEach(p -> p.sendMessage(reportMessage));

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