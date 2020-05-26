package jp.hack.minecraft.mineandfight.command.subCommand;

import jp.hack.minecraft.mineandfight.core.*;
import jp.hack.minecraft.mineandfight.core.utils.I18n;
import jp.hack.minecraft.mineandfight.utils.GameConfiguration;
import jp.hack.minecraft.mineandfight.utils.MainConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class JoinSubCommand implements SubCommand {
    GamePlugin plugin;

    public JoinSubCommand(GamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String gameId = args[0];

        GameConfiguration configuration = GameConfiguration.create(plugin, gameId);
        GameManager gameManager = GameManager.getInstance();
        Game game = gameManager.getGame(gameId);
        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) sender;

        Boolean isThereAPlayer = false;

        if(!game.getJoinPlayers().isEmpty()) {
            isThereAPlayer =
                    game.getJoinPlayers().stream()
                            .filter(p -> p.getUuid() == bukkitPlayer.getUniqueId())
                            .findAny()
                            .isPresent();
        }

        if(!isThereAPlayer) {
            if (configuration.isCreated()) {
                //ソロの場合は、チームは全員違うチームになるのでプレイヤー数をいれている。
                //チームが複数ある場合は プレイヤー数%チーム数で　自動的に割り振りができる
                int teamId = 0;

                Player player = new Player(bukkitPlayer.getUniqueId());
                player.setTeamId(teamId);
                game.addPlayer(player);
                return true;
            } else {
                sender.sendMessage(I18n.tl("error.command.uncreated.game", gameId));
            }
        }
        return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        MainConfiguration configuration = plugin.getConfiguration();
        List<String> games = configuration.getGameList();
        if(args.length>0 && args[0].length()>0){
            return games.stream().filter(s->s.startsWith(args[0])).collect(Collectors.toList());
        }else{
            return games;
        }
    }
}
