package fr.milekat.cite_claim.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.engines.RegionsTask;
import fr.milekat.cite_claim.obj.Region;
import fr.milekat.cite_claim.utils.RegionsBlocksLoad;
import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_libs.MainLibs;
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
import java.util.HashMap;
import java.util.List;

public class CommandsRegion implements CommandExecutor {
    private final Material CLAIMBLOCK = Material.BEDROCK;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true; /* Commande désactivée pour la console */
        if (sender.hasPermission("modo.claim.command.claim")) {
            if (args.length >= 2 && args[0].equalsIgnoreCase("add")) {
                try {
                    Region region = addRegion((Player) sender, args[1]);
                    if (args.length >= 3) {
                        try {
                            updatePrix((Player) sender, region, Integer.parseInt(args[3]));
                        } catch (NumberFormatException exception) {
                            sender.sendMessage(MainCore.prefixCmd + "§cMerci de mettre un prix valide.");
                        }
                    }
                    if (args.length == 4) {
                        updateQuartier((Player) sender, region, args[3]);
                    }
                } catch (SQLException throwables) {
                    Bukkit.getLogger().warning("Erreur dans la création d'une nouvelle région.");
                    sender.sendMessage(MainCore.prefixCmd + "§cErreur dans la création d'une nouvelle région.");
                    throwables.printStackTrace();
                }
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("update")) {
                if (args.length >= 3 && !MainClaim.regions.containsKey(args[2])) {
                    sender.sendMessage(MainCore.prefixCmd + "§cRégion non reconnue.");
                } else if (args.length == 4 && args[1].equalsIgnoreCase("prix")) {
                    try {
                        updatePrix((Player) sender, MainClaim.regions.get(args[2]), Integer.parseInt(args[3]));
                    } catch (NumberFormatException exception) {
                        sender.sendMessage(MainCore.prefixCmd + "§cMerci de mettre un prix valide.");
                    }
                } else if (args.length == 3 && args[1].equalsIgnoreCase("claim")) {
                    updateClaim((Player) sender, MainClaim.regions.get(args[2]));
                } else if (args.length == 4 && args[1].equalsIgnoreCase("quartier")) {
                    updateQuartier((Player) sender, MainClaim.regions.get(args[2]), args[3]);
                } else if (args.length == 3 && args[1].equalsIgnoreCase("sign")) {
                    updateSign((Player) sender, MainClaim.regions.get(args[2]));
                }
            } else if (args.length==2 && args[0].equalsIgnoreCase("tool")) {
                if (MainClaim.regions.containsKey(args[1])) {
                    ItemMeta meta = ((Player) sender).getInventory().getItemInMainHand().getItemMeta();
                    if (meta != null) meta.setDisplayName(args[1]);
                    ((Player) sender).getInventory().getItemInMainHand().setItemMeta(meta);
                } else {
                    sender.sendMessage(MainCore.prefixCmd + "§cRégion non reconnue.");
                }
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
                new RegionsTask().updateRegion();
                new RegionsBlocksLoad().reloadRegions();
                sender.sendMessage("Reload effectué.");
            } else sendHelp(sender, label);
            return true;
        }
        sender.sendMessage(MainCore.prefixCmd + "§cCommande pour les modos.");
        return true;
    }

    /**
     *      Envoie la liste d'help de la commande
     * @param sender joueur qui exécute la commande
     */
    private void sendHelp(CommandSender sender, String prefix){
        sender.sendMessage(MainCore.prefixCmd);
        sender.sendMessage("§6/" + prefix + " add <nom_region> [<prix>] [<nom_du_quartier>]:§r Ajoute une région à la liste.");
        sender.sendMessage("§6/" + prefix + " update prix <nom_region> <prix>:§r défini le prix de la région.");
        sender.sendMessage("§6/" + prefix + " update claim <nom_region>:§r claim la zone (Avoir une selection WE).");
        sender.sendMessage("§6/" + prefix + " update quartier <nom_region> <nom_du_quartier:§r défini le quartier de la région.");
        sender.sendMessage("§6/" + prefix + " update sign <nom_region>:§r défini le panneau (Regarder le panneau).");
        sender.sendMessage("§6/" + prefix + " tool <nom_region>:§r défini l'outil (Avoir une blaze rod en main).");
        sender.sendMessage("§6/" + prefix + " reload:§r recharge toutes les régions.");
    }

    /**
     *      Ajout d'une région dans la base SQL
     */
    private Region addRegion(Player player, String name) throws SQLException {
        Connection connection = MainLibs.getSql();
        PreparedStatement q = connection.prepareStatement(
                "INSERT INTO `" + MainCore.SQLPREFIX + "regions`(`rg_name`) VALUES (?) RETURNING `rg_id`;");
        q.setString(1, name);
        q.execute();
        q.getResultSet().last();
        Region region = new Region(q.getResultSet().getInt("rg_id"),
                name, "", null, 0, null, new ArrayList<>());
        MainClaim.regions.put(name, region);
        q.close();
        player.sendMessage(MainCore.prefixCmd + "§6Région §b" + name + "§6 créée.");
        return region;
    }

    /**
     *      Définition / Redéfinition de blocks de la claim
     */
    private void updatePrix(Player player, Region region, Integer prix) {
        try {
            updateSQLRegion(region,"prix",null,prix);
            region.setPrix(prix);
            player.sendMessage(MainCore.prefixCmd + "§6La région §b" + region.getName() +
                    " §6coûte désormais §2" + prix + "Émeraudes§c.");
        } catch (SQLException throwables) {
            player.sendMessage(MainCore.prefixCmd + "§6Erreur SQL.");
            Bukkit.getLogger().warning(MainClaim.prefixConsole + "Erreur de l'update du prix de: " + region.getName());
            throwables.printStackTrace();
        }
    }

    /**
     *      Définition / Redéfinition de blocks de la claim
     */
    private void updateClaim(Player player, Region region) {
        try {
            WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            if (worldEdit == null) {
                player.sendMessage(MainCore.prefixCmd + "§cErreur WorlEdit");
                return;
            }
            com.sk89q.worldedit.regions.Region selection;
            try {
                 selection = worldEdit.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
            } catch (IncompleteRegionException ignore) {
                player.sendMessage(MainCore.prefixCmd + "§cMerci de faire une selection WE.");
                return;
            }
            CuboidRegion cuboid = new CuboidRegion(selection.getMaximumPoint(),selection.getMinimumPoint());
            Location pos1 = new Location(player.getWorld(),cuboid.getPos1().getX(),cuboid.getPos1().getY(),cuboid.getPos1().getZ());
            Location pos2 = new Location(player.getWorld(),cuboid.getPos2().getX(),cuboid.getPos2().getY(),cuboid.getPos2().getZ());
            StringBuilder pos = new StringBuilder();
            HashMap<Location, String> blocks = new HashMap<>();
            try {
                for (Block block : getBlocks(pos1,pos2,player.getWorld())) {
                    pos.append(";");
                    pos.append(block.getLocation().getBlockX());
                    pos.append(":");
                    pos.append(block.getLocation().getBlockY());
                    pos.append(":");
                    pos.append(block.getLocation().getBlockZ());
                    blocks.put(block.getLocation(), region.getName());
                    block.setType(Material.AIR);
                }
                updateSQLRegion(region, "locs", pos.substring(1), 0);
                MainClaim.regionsBlocks.putAll(blocks);
                region.setBlocks(new ArrayList<>(blocks.keySet()));
                player.sendMessage(MainCore.prefixCmd + "§6Claim mise à jour pour la région §b" + region.getName() + "§c.");
            } catch (SQLException throwables) {
                player.sendMessage(MainCore.prefixCmd + "§cErreur SQL.");
                Bukkit.getLogger().warning(MainClaim.prefixConsole + "Erreur de l'update du claim de: " + region.getName());
                throwables.printStackTrace();
            } catch (StringIndexOutOfBoundsException ignore) {
                player.sendMessage(MainCore.prefixCmd + "§cAucun block trouvé, opération annulée.");
            }
        } catch (ClassCastException  exception) {
            player.sendMessage(MainCore.prefixCmd + "§cErreur WorlEdit");
            exception.printStackTrace();
        }
    }

    /**
     *      Définition / Redéfinition de blocks de la claim
     */
    private void updateQuartier(Player player, Region region, String quartier) {
        try {
            updateSQLRegion(region,"quartier",quartier,0);
            region.setQuartier(quartier);
            player.sendMessage(MainCore.prefixCmd + "§6La région §b" + region.getName() +
                    " §6est désormais dans le quartier §b" + quartier + "§c.");
        } catch (SQLException throwables) {
            player.sendMessage(MainCore.prefixCmd + "§cErreur SQL.");
            Bukkit.getLogger().warning(MainClaim.prefixConsole + "Erreur de l'update du quartier de: " + region.getName());
            throwables.printStackTrace();
        }
    }

    /**
     *      Défini l'emplacement du sign de la région
     */
    private void updateSign(Player player, Region region) {
        Block block = player.getTargetBlockExact(5);
        if (block != null && block.getState() instanceof Sign) {
            StringBuilder pos = new StringBuilder();
            pos.append(block.getLocation().getBlockX());
            pos.append(":");
            pos.append(block.getLocation().getBlockY());
            pos.append(":");
            pos.append(block.getLocation().getBlockZ());
            try {
                updateSQLRegion(region, "sign", pos.toString(), 0);
                region.setSign((Sign) block.getState());
                player.sendMessage(MainCore.prefixCmd + "§6Panneau mis à jour pour la région §b" + region.getName() + "§c.");
            } catch (SQLException exception) {
                player.sendMessage(MainCore.prefixCmd + "§6Erreur SQL.");
                Bukkit.getLogger().warning(MainClaim.prefixConsole + "Erreur update Sign de: " + region.getName());
                exception.printStackTrace();
            }
        } else player.sendMessage(MainCore.prefixCmd + "§cMerci de regarder un panneau vierge.");
    }

    /**
     *      Update d'un paramètre dans le SQL, si String null, intValue sera utilisé
     */
    private void updateSQLRegion(Region region, String column, String stringValue, Integer intValue) throws SQLException {
        Connection connection = MainLibs.getSql();
        PreparedStatement q = connection.prepareStatement("UPDATE `" + MainCore.SQLPREFIX +
                "regions` SET `rg_" + column + "` = ? WHERE `rg_id` = ?;");
        q.setObject(1, stringValue == null ? intValue : stringValue);
        q.setInt(2, region.getId());
        q.execute();
        q.close();
    }

    /**
     *      Snipet from https://bukkit.org/threads/get-blocks-between-two-locations.262499/
     *      Permet de loop tous les blocks between 2 pos ! (Filtré ici sur sur type=CLAIMBLOCK)
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
