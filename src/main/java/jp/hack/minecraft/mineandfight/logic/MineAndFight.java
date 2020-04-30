package jp.hack.minecraft.mineandfight.logic;

import jp.hack.minecraft.mineandfight.core.Game;
import jp.hack.minecraft.mineandfight.core.Player;
import jp.hack.minecraft.mineandfight.core.Scoreboard;
import jp.hack.minecraft.mineandfight.core.Team;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

public class MineAndFight implements Listener {
    private Logger logger;

    private final Game game;
    public MineAndFight(Game game) {
        this.game = game;
        this.logger = game.getPlugin().getLogger();
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        logger.info(String.format("onLogin: %s", event.getPlayer().getName()));
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event){
        logger.info(String.format("onBlockBreakEvent: %s", event.getPlayer().getName()));

        Player breaker = game.findPlayer(event.getPlayer().getUniqueId());

        final String oreName = Material.EMERALD_ORE.getData().getName();
        String blockName = event.getBlock().getBlockData().getMaterial().getData().getName();

        if(blockName == oreName){
            breaker.setScore( breaker.getScore() + ( breaker.getBounty() + 1 ) );
            breaker.setBounty(0);
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killed = game.findPlayer(event.getEntity().getUniqueId());
            Player killer = game.findPlayer(event.getEntity().getKiller().getUniqueId());

            logger.info(String.format("onPlayerDeathEvent: %s -> %s", killed, killer);

            Team killerTeam = new Team(killer.getTeamId());

            killer.setScore(killer.getScore() + killed.getBounty());
            killer.setBounty(killer.getBounty() + 1);
            killerTeam.setScore(killerTeam.getScore() + killed.getBounty());
            killed.setBounty(0);

            Scoreboard killerScoreboard = new Scoreboard(killer);
            killerScoreboard.setScore(killer.getScore());
            killerScoreboard.setTeamScore(killer.getUuid(), killerTeam.getScore());
        }
    }
}
