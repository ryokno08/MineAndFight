package jp.hack.minecraft.mineandfight.command.subCommand;

import jp.hack.minecraft.mineandfight.core.*;
import jp.hack.minecraft.mineandfight.core.utils.I18n;
import jp.hack.minecraft.mineandfight.utils.GameConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
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

        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) sender;
        GameManager gameManager = GameManager.getInstance();
        Boolean isPlayerJoinedGame = false;
        Scoreboard scoreboard = new Scoreboard(gameId,ChatColor.GREEN +"PLAYERS");

        Game game = gameManager.getGame(gameId);
        GameConfiguration configuration = game.getConfiguration();

        for(String gameName : gameManager.getGameNames()) {
            isPlayerJoinedGame = gameManager.getGame(gameName).isTherePlayer(bukkitPlayer.getUniqueId());
            if (isPlayerJoinedGame) break;
        }

        if(!isPlayerJoinedGame) {
            if (configuration.isCreated()) {
                //ソロの場合は、チームは全員違うチームになるのでプレイヤー数をいれている。
                //チームが複数ある場合は プレイヤー数%チーム数で　自動的に割り振りができる
                int teamId = 0;

                Player player = new Player(bukkitPlayer.getUniqueId());
                game.addPlayer(player);
                bukkitPlayer.sendMessage(ChatColor.GREEN +"Succeed: You joined "+gameId+".");
                game.getJoinPlayers().stream().forEach(p->{
                    org.bukkit.entity.Player bP = Bukkit.getPlayer(p.getUuid());
                    bP.sendMessage(ChatColor.GREEN +player.getName()+" joined us!");
                });

                //Scoreboard
                for(Player p : game.getJoinPlayers()){
                    scoreboard.setScoreboard(Bukkit.getPlayer(p.getName()));
                    scoreboard.setScore(p.getName(),0);
                }
                return true;
            } else {
                bukkitPlayer.sendMessage(I18n.tl("error.command.uncreated.game", gameId));
            }
        } else {
            bukkitPlayer.sendMessage(I18n.tl("error.command.joined.game", gameId));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        GameManager gameManager = GameManager.getInstance();
        List<String> games = gameManager.getGameNames();
        if (args.length == 1) {
            if (args[0].length() > 0) {
                return games.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
            }
            return games;
        }
        return new ArrayList<>();
    }
}
