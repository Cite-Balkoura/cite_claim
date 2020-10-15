package fr.milekat.cite_claim.engines;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.events.RegionMarket;
import fr.milekat.cite_claim.obj.Region;
import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_core.core.obj.Team;
import fr.milekat.cite_libs.utils_tools.LocToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class RegionsTask {
    public BukkitTask runTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                updateRegion();
            }
        }.runTaskTimer(MainClaim.getInstance(),6000L,6000L);
    }

    public void updateRegion() {
        Connection connection = MainCore.getSQL().getConnection();
        try {
            PreparedStatement q = connection.prepareStatement(
                    "SELECT * FROM `" + MainCore.SQLPREFIX + "regions` ORDER BY `rg_id` ASC;");
            q.execute();
            while (q.getResultSet().next()) {
                String name = q.getResultSet().getString("rg_name");
                Team team = null;
                Sign sign = null;
                ArrayList<Location> blocks = new ArrayList<>();
                if (!(q.getResultSet().getInt("team_id") == 0)) {
                    team = MainCore.teamHashMap.get(q.getResultSet().getInt("team_id"));
                }
                if (q.getResultSet().getString("rg_sign") != null) {
                    sign = (Sign) getBlock(q.getResultSet().getString("rg_sign")).getState();
                }
                if (!(q.getResultSet().getString("rg_locs") == null)) {
                    for (String loc : q.getResultSet().getString("rg_locs").split(";")) {
                        String[] xyz = loc.split(":");
                        blocks.add(new Location(Bukkit.getServer().getWorld("world"),
                                Integer.parseInt(xyz[0]),Integer.parseInt(xyz[1]),Integer.parseInt(xyz[2])));
                    }
                }
                Region region = new Region(q.getResultSet().getInt("rg_id"),
                        name,
                        q.getResultSet().getString("rg_quartier"),
                        sign,
                        q.getResultSet().getInt("rg_prix"),
                        team,
                        blocks);
                MainClaim.regions.put(name, region);
                if (region.getSign() != null) {
                    writeSign(region, name);
                }
            }
            q.close();
        } catch (SQLException exception) {
            Bukkit.getLogger().warning("Impossible d'update les habitations.");
            exception.printStackTrace();
        } catch (ClassCastException exception) {
            Bukkit.getLogger().warning(MainClaim.prefixConsole + "Erreur chargement ascenseur, le block n'est pas un Sign.");
        }
    }

    /**
     *      Mise à jour du contenu des panneaux
     */
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
