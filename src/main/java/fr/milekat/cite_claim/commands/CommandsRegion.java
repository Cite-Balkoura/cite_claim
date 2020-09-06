package fr.milekat.cite_claim.commands;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.obj.Region;
import fr.milekat.cite_core.MainCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandsRegion implements CommandExecutor {
    private final Material CLAIMBLOCK = Material.BEDROCK;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("modo.claim.command.claim")) {
            if (args.length==2 && args[0].equalsIgnoreCase("claim")) {
                if (MainClaim.boundRegionleft.get(((Player) sender).getUniqueId())==null ||
                        MainClaim.boundRegionright.get(((Player) sender).getUniqueId())==null) {
                    sender.sendMessage(MainCore.prefixCmd + "§cMerci de faire une selection WE.");
                } else {
                    if (MainClaim.regions.containsKey(args[1])) {
                        setClaim((Player) sender,args[1]);
                    } else {
                        sender.sendMessage(MainCore.prefixCmd + "§cRégion non reconnue.");
                    }
                }
            } else if (args.length==3 && args[0].equalsIgnoreCase("add")) {
                newRegion(sender, args[1], Integer.parseInt(args[2]));
                sender.sendMessage(MainCore.prefixCmd + "§6Région §b" + args[1] + "§6 ajoutée.");
            } else if (args.length==2 && args[0].equalsIgnoreCase("sign")) {
                if (MainClaim.regions.containsKey(args[1])) {
                    setSign(sender, args[1]);
                } else {
                    sender.sendMessage(MainCore.prefixCmd + "§cRégion non reconnue.");
                }
            } else if (args.length==2 && args[0].equalsIgnoreCase("tool")) {
                if (MainClaim.regions.containsKey(args[1])) {
                    ItemMeta meta = ((Player) sender).getInventory().getItemInMainHand().getItemMeta();
                    if (meta!=null) meta.setDisplayName(args[1]);
                    ((Player) sender).getInventory().getItemInMainHand().setItemMeta(meta);
                } else {
                    sender.sendMessage(MainCore.prefixCmd + "§cRégion non reconnue.");
                }
            } else {
                sendHelp(sender);
            }
        }
        return true;
    }

    /**
     *      Envoie la liste d'help de la commande
     * @param sender joueur qui exécute la commande
     */
    private void sendHelp(CommandSender sender){
        sender.sendMessage(MainCore.prefixCmd);
        sender.sendMessage("§6/region add <nom_region> <prix>:§r Ajoute une région à la liste.");
        sender.sendMessage("§6/region claim <nom_region>:§r claim la zone (Avoir une selection WE).");
        sender.sendMessage("§6/region sign <nom_region>:§r défini le panneau (Regarder le panneau).");
        sender.sendMessage("§6/region tool <nom_region>:§r défini l'outil (Avoir une blaze rod en main).");
    }

    /**
     *      Ajout d'une région dans la base SQL
     */
    private void newRegion(CommandSender sender, String name, int prix) {
        Connection connection = MainCore.sql.getConnection();
        try {
            PreparedStatement q = connection.prepareStatement("INSERT INTO `" + MainCore.SQLPREFIX +
                    "regions`(`rg_name`, `rg_prix`) VALUES (?,?) RETURNING `rg_id`;");
            q.setString(1,name);
            q.setInt(2,prix);
            q.execute();
            q.getResultSet().last();
            MainClaim.regions.put(name, new Region(q.getResultSet().getInt("rg_id"),name,1,null,prix,null));
            q.close();
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning("Erreur dans la création d'une nouvelle région.");
            sender.sendMessage(MainCore.prefixCmd + "§cImpossible de créer cette région avec ce nom.");
        }
    }

    /**
     *      Définition / Redéfinition de blocks de la claim
     */
    private void setClaim(Player player, String region) {
        Location pos1 = MainClaim.boundRegionleft.get((player).getUniqueId());
        Location pos2 = MainClaim.boundRegionright.get((player).getUniqueId());
        StringBuilder pos = new StringBuilder();
        for (Block block : getBlocks(pos1,pos2,Bukkit.getServer().getWorld("world"))) {
            pos.append(";");
            pos.append(block.getLocation().getBlockX());
            pos.append(":");
            pos.append(block.getLocation().getBlockY());
            pos.append(":");
            pos.append(block.getLocation().getBlockZ());
            MainClaim.regionsBlocks.put(block.getLocation(),region);
            block.setType(Material.AIR);
        }
        Connection connection = MainCore.sql.getConnection();
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `" + MainCore.SQLPREFIX +
                    "regions` SET `rg_locs` = ? WHERE `rg_name` = ?;");
            q.setString(1,pos.toString().substring(1));
            q.setString(2,region);
            q.execute();
            q.close();
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning("Erreur de l'update du claim de: " + region);
            throwables.printStackTrace();
        }
    }

    /**
     *      Défini l'emplacement du sign de la région
     */
    private void setSign(CommandSender sender, String region) {
        Player player = (Player) sender;
        Block block = player.getTargetBlockExact(5);
        if (block!=null && block.getState() instanceof Sign) {
            StringBuilder pos = new StringBuilder();
            pos.append(block.getLocation().getBlockX());
            pos.append(":");
            pos.append(block.getLocation().getBlockY());
            pos.append(":");
            pos.append(block.getLocation().getBlockZ());
            Connection connection = MainCore.sql.getConnection();
            try {
                PreparedStatement q = connection.prepareStatement("UPDATE `" + MainCore.SQLPREFIX +
                        "regions` SET `rg_sign` = ? WHERE `rg_name` = ?;");
                q.setString(1,pos.toString().substring(1));
                q.setString(2,region);
                q.execute();
                q.close();
                sender.sendMessage(MainCore.prefixCmd + "§6Panneau de la région §b" + region + "§6 défini.");
            } catch (SQLException throwables) {
                Bukkit.getLogger().warning("Erreur de l'update du sign de la région: " + region);
                throwables.printStackTrace();
            }
        } else {
            sender.sendMessage(MainCore.prefixCmd + "§cMerci de regarder un panneau vierge.");
        }
    }

    /**
     *      Snipet from https://bukkit.org/threads/get-blocks-between-two-locations.262499/
     *      Permet de loop tous les blocks between 2 pos ! (Filtré ici sur sur type=CLAIMBLOCK
     */
    private List<Block> getBlocks(Location loc1, Location loc2, World w){
        //First of all, we create the list:
        List<Block> blocks = new ArrayList<>();
        //Next we will name each coordinate
        int x1 = loc1.getBlockX();
        int y1 = loc1.getBlockY();
        int z1 = loc1.getBlockZ();
        int x2 = loc2.getBlockX();
        int y2 = loc2.getBlockY();
        int z2 = loc2.getBlockZ();
        //Then we create the following integers
        int xMin, yMin, zMin;
        int xMax, yMax, zMax;
        int x, y, z;
        //Now we need to make sure xMin is always lower then xMax
        if(x1 > x2){ //If x1 is a higher number then x2
            xMin = x2;
            xMax = x1;
        }else{
            xMin = x1;
            xMax = x2;
        }
        //Same with Y
        if(y1 > y2){
            yMin = y2;
            yMax = y1;
        }else{
            yMin = y1;
            yMax = y2;
        }
        //And Z
        if(z1 > z2){
            zMin = z2;
            zMax = z1;
        }else{
            zMin = z1;
            zMax = z2;
        }
        //Now it's time for the loop
        for(x = xMin; x <= xMax; x ++){
            for(y = yMin; y <= yMax; y ++){
                for(z = zMin; z <= zMax; z ++){
                    Block b = new Location(w, x, y, z).getBlock();
                    if (b.getType().equals(CLAIMBLOCK)) {
                        blocks.add(b);
                    }
                }
            }
        }
        //And last but not least, we return with the list
        return blocks;
    }
}
