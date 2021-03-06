package jp.hack.minecraft.mineandfight.core;

import jp.hack.minecraft.mineandfight.core.utils.WorldEditorUtil;
import jp.hack.minecraft.mineandfight.logic.MineAndFightLogic;
import jp.hack.minecraft.mineandfight.utils.GameConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class GameManager implements Listener {
    private static class SingletonHolder {
        private static final AtomicInteger ids = new AtomicInteger();
        private static final GameManager singleton = new GameManager();
        private SingletonHolder() { }
    }
    public static synchronized GameManager getInstance(){
        return SingletonHolder.singleton;
    }

    private final ExecutorService pool;
    private Map<String, Game> games = new HashMap<>();
    private Map<String, Game> runningGames = new HashMap<>();
    private GameGenerator generator;

    private GameManager(){
        pool = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "game thread " + SingletonHolder.ids.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        });
    }

    public void release() {
        if(pool!=null) pool.shutdown();
        games.clear();
        runningGames.values().stream().forEach(game->{
            game.cancel();
        });
        runningGames.clear();

    }


    public Game getGame(String id){
        return games.get(id);
    }

    public List<String> getGameNames() {
        return new ArrayList<>(games.keySet());
    }

    public Game createGame(GamePlugin plugin, String id){
        Game game =  generator.createGame(plugin, id);
        games.put(id, game);
        return game;
    }

    public void deleteGame(String id) {
        games.remove(id);
    }

    public void setGenerator(GameGenerator generator){
        this.generator = generator;
    }

    public void loadGame(GamePlugin plugin){
        if(generator!=null) {
            plugin.getConfiguration().getGameList().stream().forEach(gameId -> {
                if (!games.containsKey(gameId)) {
                    games.put(gameId, generator.createGame(plugin, gameId));
                }
            });
        }
    }

    public Game findGame(UUID playerID){
        for(Iterator<Game> ite = games.values().iterator(); ite.hasNext(); ){
            Game g = ite.next();
            if(g.findPlayer(playerID) != null) return g;
        }
        return null;
    }


    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        for(Iterator<Game> ite=runningGames.values().iterator(); ite.hasNext();){
            Game g = ite.next();
            if(g.findPlayer(event.getPlayer().getUniqueId())!=null) {
                g.onLogin(event);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerMoveEvent​(PlayerMoveEvent event​) {
        //ゲームエリア外にでた場合は処理をキャンセルする
        for(Iterator<Game> ite=runningGames.values().iterator(); ite.hasNext();) {
            Game g = ite.next();
            if(g.findPlayer(event​.getPlayer().getUniqueId())!=null){
                if(g.findPlayer(event​.getPlayer().getUniqueId()).isPlayingGame()) {
                    if (g.getGameArea().contains(event​.getTo().toVector()) != true) {
                        event​.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }


    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        for(Iterator<Game> ite=runningGames.values().iterator(); ite.hasNext();) {
            Game g = ite.next();
            if(g.getGameArea().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                g.onBlockBreakEvent(event);
                break;
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        for(Iterator<Game> ite=runningGames.values().iterator(); ite.hasNext();){
            Game g = ite.next();
            Player player = g.findPlayer(event.getEntity().getUniqueId());
            if (player != null && player.isPlayingGame()) {
                g.onPlayerDeathEvent(event);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        for(Iterator<Game> ite=runningGames.values().iterator(); ite.hasNext();){
            Game g = ite.next();
            Player player = g.findPlayer(event.getPlayer().getUniqueId());
            if (player != null) {
                g.onRespawnEvent(event);
                break;
            }
        }
    }

    public boolean isCreated(String gameId){
        return games.containsKey(gameId);
    }

    public boolean isRunning(String gameId){
        return runningGames.containsKey(gameId);
    }

    public Collection<Player> getPlayers(String gameId){
        Game game = games.get(gameId);
        if(game!=null){
            return game.getJoinPlayers();
        }

        return new ArrayList<Player>();
    }


    public void start(String gameId){
        Game game = games.get(gameId);
        // Scoreboard scoreboard = new Scoreboard(gameId, ChatColor.GREEN +"SCORE");

        for (Player player : game.getJoinPlayers()) {
            // org.bukkit.entity.Player bukkitPlayer = Bukkit.getPlayer(player.getName());
            // scoreboard.setScoreboard(bukkitPlayer);
            // scoreboard.setScore(player.getName(),0);
            player.setPlayingGame(true);
        }
        if(game!=null) {
            Future future = game.start(pool);
            if (future != null) {
                runningGames.put(gameId, game);
            }
        }
    }

    public void stop(String id){
        Game game = remove(id);
        if(game!=null){
            game.cancel();
            game.onStop();
        }
    }

    Game remove(String id){
        return runningGames.remove(id);
    }


    public interface GameGenerator {
        Game createGame(GamePlugin plugin, String gameID);
    }
}
