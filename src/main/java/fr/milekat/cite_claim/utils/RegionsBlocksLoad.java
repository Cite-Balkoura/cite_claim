package fr.milekat.cite_claim.utils;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_core.MainCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegionsBlocksLoad {
    /**
     *      Injections des régions dans la hashmap
     */
    public void reloadRegions() {
        Connection connection = MainCore.getSQL().getConnection();
        try {
            PreparedStatement q = connection.prepareStatement("SELECT `rg_name`, `rg_locs` FROM `" + MainCore.SQLPREFIX
                    + "regions`;");
            q.execute();
            MainClaim.regionsBlocks.clear();
            while (q.getResultSet().next()) {
                if (!(q.getResultSet().getString("rg_locs") == null)) {
                    for (String loc : q.getResultSet().getString("rg_locs").split(";")) {
                        String[] xyz = loc.split(":");
                        Location location = new Location(Bukkit.getServer().getWorld("world"),
                                Integer.parseInt(xyz[0]),Integer.parseInt(xyz[1]),Integer.parseInt(xyz[2]));
                        MainClaim.regionsBlocks.put(location,q.getResultSet().getString("rg_name"));
                    }
                }
            }
            q.close();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(MainClaim.prefixConsole + "Erreur SQL lors de la récupération des régions.");
            e.printStackTrace();
        }
    }
}
