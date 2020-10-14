package fr.milekat.cite_claim.ascenseur.sql;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.ascenseur.obj.Ascenseur;
import fr.milekat.cite_core.MainCore;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Sign;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Save {
    public void saveAscenseur() {
        Connection connection = MainCore.getSQL().getConnection();
        try {
            PreparedStatement q = connection.prepareStatement("");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadAscenseur() {
        Connection connection = MainCore.getSQL().getConnection();
        try {
            PreparedStatement q = connection.prepareStatement("SELECT * FROM `" + MainCore.SQLPREFIX + "ascenseurs`;");
            q.execute();
            while (q.getResultSet().next()) {
                Integer[] pos = toIntArray(q.getResultSet().getString("floorDownLoc").split(":"));
                Location floorDownLoc = new Location(Bukkit.getWorld("world"), pos[0],pos[1],pos[2]);
                pos = toIntArray(q.getResultSet().getString("floorUpLoc").split(":"));
                Location floorUpLoc = new Location(Bukkit.getWorld("world"), pos[0],pos[1],pos[2]);
                Integer yawDown = q.getResultSet().getInt("yawDown");
                Integer yawUp = q.getResultSet().getInt("yawUp");
                pos = toIntArray(q.getResultSet().getString("boutonDown").split(":"));
                Location boutonDown = new Location(Bukkit.getWorld("world"), pos[0],pos[1],pos[2]);
                pos = toIntArray(q.getResultSet().getString("boutonUp").split(":"));
                Location boutonUp = new Location(Bukkit.getWorld("world"), pos[0],pos[1],pos[2]);
                pos = toIntArray(q.getResultSet().getString("signDown").split(":"));
                Sign signDown = (Sign) new Location(Bukkit.getWorld("world"), pos[0],pos[1],pos[2]).getBlock().getState();
                pos = toIntArray(q.getResultSet().getString("signUp").split(":"));
                Sign signUp = (Sign) new Location(Bukkit.getWorld("world"), pos[0],pos[1],pos[2]).getBlock().getState();
                MainClaim.ascenseurHashMap.put(q.getResultSet().getString("asc_name"),new Ascenseur(
                        q.getResultSet().getString("asc_name"),
                        CitizensAPI.getNPCRegistry().getById(q.getResultSet().getInt("npc_id")),
                        floorDownLoc, floorUpLoc, yawDown, yawUp, boutonDown, boutonUp, signDown, signUp));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private Integer[] toIntArray(String[] strings) {
        Integer[] intArray = new Integer[strings.length];
        for (int loop = 0; loop < strings.length; loop++) {
            intArray[loop] = Integer.parseInt(strings[loop]);
        }
        return intArray;
    }
}
