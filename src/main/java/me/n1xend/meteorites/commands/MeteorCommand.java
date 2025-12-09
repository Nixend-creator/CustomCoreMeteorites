package me.n1xend.meteorites.commands;

import me.n1xend.meteorites.CustomMeteorites;
import me.n1xend.meteorites.config.ConfigManager;
import me.n1xend.meteorites.generator.MeteoriteGenerator;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MeteorCommand implements CommandExecutor {

    private final CustomMeteorites plugin;
    private final ConfigManager configManager;
    private final MeteoriteGenerator generator;

    public MeteorCommand(CustomMeteorites plugin,
                         ConfigManager configManager,
                         MeteoriteGenerator generator) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.generator = generator;
    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        if (!sender.hasPermission("custommeteorites.admin")) {
            sender.sendMessage("§cУ вас нет прав.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "spawn" -> {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage("§cТолько игрок может вызывать метеорит.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cИспользование: /meteor spawn <id>");
                    return true;
                }
                String id = args[1];
                Location loc = p.getLocation();
                generator.createMeteoriteAt(loc, id);
                sender.sendMessage("§aМетеорит '" + id + "' вызван.");
            }

            case "reload" -> {
                configManager.reload();
                sender.sendMessage("§aconfig.yml перезагружен.");
            }

            case "start" -> {
                plugin.startRandomMeteorites();
                sender.sendMessage("§aСлучайные метеориты запущены.");
            }

            case "stop" -> {
                plugin.stopRandomMeteorites();
                sender.sendMessage("§aСлучайные метеориты остановлены.");
            }

            default -> sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender s) {
        s.sendMessage("§eCustomMeteorites команды:");
        s.sendMessage("§e/meteor spawn <id> §7- вызвать метеорит");
        s.sendMessage("§e/meteor start §7- запустить случайные метеориты");
        s.sendMessage("§e/meteor stop §7- остановить случайные метеориты");
        s.sendMessage("§e/meteor reload §7- перезагрузить config.yml");
    }
}
