package fr.milekat.cite_claim.engines;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.events.RegionMarket;
import fr.milekat.cite_claim.obj.Region;
import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_core.core.obj.Team;
import fr.milekat.cite_core.utils_tools.LocToString;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegionsTask {
    public BukkitTask runTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Connection connection = MainCore.sql.getConnection();
                try {
                    PreparedStatement q = connection.prepareStatement("SELECT `rg_id`, `rg_name`, `rg_type`, `rg_sign`, " +
                            "`rg_prix`, `team_id` FROM `" + MainCore.SQLPREFIX + "regions`;");
                    q.execute();
                    while (q.getResultSet().next()) {
                        String name = q.getResultSet().getString("rg_name");
                        Team team = null;
                        Sign sign = null;
                        if (!(q.getResultSet().getInt("team_id") == 0)) {
                            team = MainCore.teamHashMap.get(q.getResultSet().getInt("team_id"));
                        }
                        if (!(q.getResultSet().getString("rg_sign") == null)) {
                            sign = (Sign) getBlock(q.getResultSet().getString("rg_sign")).getState();
                        }
                        Region region = new Region(q.getResultSet().getInt("rg_id"),
                                name,
                                q.getResultSet().getInt("rg_type"),
                                sign,
                                q.getResultSet().getInt("rg_prix"),
                                team);
                        MainClaim.regions.put(name, region);
                        if (region.getSign() != null) {
                            writeSign(region, name);
                        }
                    }
                    q.close();
                } catch (SQLException throwables) {
                    Bukkit.getLogger().warning("Impossible d'update les habitations.");
                    throwables.printStackTrace();
                }
            }
        }.runTaskTimer(MainClaim.getInstance(),0L,6000L);
    }

    private void writeSign(Region region, String name) {
        Sign sign = region.getSign();
        sign.setLine(0, RegionMarket.PREFIX);
        sign.setLine(1, name);
        if (region.getTeam() != null) {
            sign.setLine(2, region.getTeam().getName());
            sign.setLine(3, RegionMarket.VENDU);
        } else {
            sign.setLine(2, "§6Prix§c: §2" + region.getPrix());
            sign.setLine(3, RegionMarket.AVENDRE);
        }
        sign.update();
    }

    private Block getBlock(String location) {
        return LocToString.getLocationString("world:" + location).getBlock();
    }
}
