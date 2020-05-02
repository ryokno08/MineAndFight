package jp.hack.minecraft.mineandfight.logic;

import jp.hack.minecraft.mineandfight.core.Game;
import jp.hack.minecraft.mineandfight.core.Player;
import jp.hack.minecraft.mineandfight.core.Scoreboard;
import jp.hack.minecraft.mineandfight.core.Team;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

public class MineAndFight extends Game implements Listener {
    protected static final Logger LOGGER = Logger.getLogger("MineAndFight");

    private long gametime = 5 * 1000 * 60;

    public MineAndFight(JavaPlugin plugin, String id) {
        super(plugin, id);
    }

    public void onBlockBreakEvent(BlockBreakEvent event){
        LOGGER.info(String.format("onBlockBreakEvent: %s", event.getPlayer().getName()));

        Player breaker = findPlayer(event.getPlayer().getUniqueId());

        final String oreName = Material.EMERALD_ORE.getData().getName();
        String blockName = event.getBlock().getBlockData().getMaterial().getData().getName();

        if(blockName.equals(oreName)){
            breaker.setScore( breaker.getScore() + ( breaker.getBounty() + 1 ) );
        }
    }

    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player killed = findPlayer(event.getEntity().getUniqueId());

        if (event.getEntity().getKiller() instanceof org.bukkit.entity.Player) {
            Player killer = findPlayer(event.getEntity().getKiller().getUniqueId());
            LOGGER.info(String.format("onPlayerDeathEvent: %s -> %s", event.getEntity().getName(), event.getEntity().getKiller().getName()));

            Team killerTeam = new Team(killer.getTeamId());

            killer.setScore(killer.getScore() + killed.getBounty());
            killer.setBounty(killer.getBounty() + 1);
            killerTeam.setScore(killerTeam.getScore() + killed.getBounty());
            killed.setBounty(0);

            ArrayList<Player> teamMate = (ArrayList<Player>) getTeamPlayers(killerTeam.getTeamId());
            for (int i = 0; i < teamMate.size(); i++) {
                Scoreboard playerScoreboard = new Scoreboard(teamMate.get(i).getUuid());
                playerScoreboard.setScore(teamMate.get(i).getScore());
                //playerScoreboard.setTeamScore(killerTeam.getScore());
                playerScoreboard.setScoreboard();
            }
        }
    }


    @Override
    public void onStart() {
        //TODO ゲームが開始されたら呼ばれます。

    }

    @Override
    public void onStop() {
        //TODO ゲームが停止されたら呼ばれます。

    }

    @Override
    public void onEnd() {
        //TODO ゲームが終了したら呼ばれます。

    }

    @Override
    public boolean onTask(long dt) {
        //TODO　１秒単位に呼ばれる処理　Flaseを返すとゲームは終了します。DTは経過時間（秒）
        if(dt < gametime) return true;
        return false;
    }
}
