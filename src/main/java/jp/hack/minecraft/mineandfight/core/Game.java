package jp.hack.minecraft.mineandfight.core;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Game {
    private final JavaPlugin plugin;
    private Map<Integer, Team> teams = new ConcurrentHashMap<>();
    private Map<UUID, Player> players = new ConcurrentHashMap<>();

    public Game(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Collection<Player> getJoinPlayers(){
        return players.values();
    }

    public Collection<Player> getTeamPlayers(int teamId){
        return players.values().stream()
                .filter(player -> player.getTeamId() == teamId).collect(Collectors.toList());
    }

    public Player findPlayer(UUID uuid){
        return players.get(uuid);
    }

    public Collection<Team> getTeams(){
        return teams.values();
    }

    public Team getTeam(int teamId){
        return teams.get(teamId);
    }
}
