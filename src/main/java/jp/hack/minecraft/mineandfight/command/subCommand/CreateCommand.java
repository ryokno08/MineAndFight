package jp.hack.minecraft.mineandfight.command.subCommand;

import jp.hack.minecraft.mineandfight.core.GamePlugin;
import jp.hack.minecraft.mineandfight.core.SubCommand;
import jp.hack.minecraft.mineandfight.core.utils.I18n;
import jp.hack.minecraft.mineandfight.core.utils.WorldEditorUtil;
import jp.hack.minecraft.mineandfight.utils.GameConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateCommand implements SubCommand {
    GamePlugin plugin;

    public CreateCommand(GamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            sender.sendMessage(I18n.tl("error.command.invalid.arguments"));
            return false;
        }
        String gameId = args[0];
        GameConfiguration configuration = GameConfiguration.create(plugin, gameId);

        boolean ret =  WorldEditorUtil.saveStage((org.bukkit.entity.Player) sender, configuration);
        if(ret){
            BoundingBox box = BoundingBox.of(configuration.getPos1(), configuration.getPos2());
            box = box.expand(-1, -1, -1);
            Vector max = box.getMax();
            Vector min = box.getMin();
            double y = min.getY() + (max.getY()-min.getY())/2;
            Vector p1 = new Vector(max.getX(), y, max.getZ());
            Vector p2 = new Vector(max.getX(), y, min.getZ());
            Vector p3 = new Vector(min.getX(), y, max.getZ());
            Vector p4 = new Vector(min.getX(), y, min.getZ());
            List<Vector> respawns = Arrays.asList(p1, p2, p3, p4);
            configuration.setRespawns(respawns);
            configuration.save();

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}