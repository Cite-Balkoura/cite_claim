package fr.milekat.cite_claim.commands;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_libs.MainLibs;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModeBuilder implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("build")){
            Player p = (Player) sender;
            boolean mode = !MainClaim.isBuildMods(p);
            setBuildForP(p, mode);
            MainCore.profilHashMap.get(p.getUniqueId()).setMods_build(mode);
            sender.sendMessage(MainCore.prefixCmd + "Mode build: " + mode);
        }
        return true;
    }

    /**
     *      Update SQL du Build pour le joueur
     * @param p cible
     * @param mode true/false
     */
    private void setBuildForP(Player p, boolean mode) {
        Connection connection = MainLibs.getSql();
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `" + MainCore.SQLPREFIX +
                    "player` SET buildon = ? WHERE `uuid` = ?;");
            q.setBoolean(1,mode);
            q.setString(2,p.getUniqueId().toString());
            q.execute();
            q.close();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(MainCore.prefixCmd + "Erreur lors de l'update du BuildOn de :"+p.getName()+".");
            e.printStackTrace();
        }
    }
}
