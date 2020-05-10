package jp.hack.minecraft.mineandfight.command.subCommand;

import jp.hack.minecraft.mineandfight.core.GameCommandExecutor;
import jp.hack.minecraft.mineandfight.core.GamePlugin;

public class GameSubCommand extends GameCommandExecutor {

    public GameSubCommand(GamePlugin plugin) {
        super(plugin);

        addSubCommand(new StartCommand());
        addSubCommand(new CreateCommand(plugin));
    }

    @Override
    public String getName() {
        return "game";
    }

    @Override
    public String getPermission() {
        return null;
    }
}