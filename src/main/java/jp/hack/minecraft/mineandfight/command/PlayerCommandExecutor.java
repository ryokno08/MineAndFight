package jp.hack.minecraft.mineandfight.command;

import jp.hack.minecraft.mineandfight.core.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PlayerCommandExecutor implements CommandExecutor, TabExecutor {
    private final Game game;

    public PlayerCommandExecutor(Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
