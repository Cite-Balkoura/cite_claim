package fr.milekat.cite_claim.events;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.obj.Region;
import fr.milekat.cite_claim.utils.ClaimInfo;
import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_libs.utils_tools.LocToString;
import net.craftersland.data.bridge.api.events.SyncCompleteEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class EventsCite implements Listener {
    private void denyMsg(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent("§cDésolé, vous ne pouvez pas effectuer cette action ici."));
    }

    @EventHandler
    public void onFirstJoin(SyncCompleteEvent event) {
        if (event.isNewPlayer()) event.getPlayer().teleport(new Location(Bukkit.getWorld("world"),-2,157,-8));
    }

    @EventHandler
    public void onPlaceWater(PlayerBucketEmptyEvent event) {
        if (!ClaimInfo.canBuild(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
        }
    }

    @EventHandler
    public void onWaterTake(PlayerBucketFillEvent event) {
        if (!ClaimInfo.canBuild(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!ClaimInfo.canBuild(event.getBlockPlaced().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (!ClaimInfo.canBuild(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
            return;
        }
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta!=null && itemMeta.isUnbreakable()) {
            event.setCancelled(true);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent("§cDésolé, le Hammer ne fonctionne pas ici."));
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock()==null || event.getHand()!=null && event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (player.hasPermission("modo.claim.event.checkclaim") &&
                player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) {
            toolClaim(player, event, block);
            return;
        }
        if (!event.getClickedBlock().getType().isInteractable()) return;
        if (block.getType().equals(Material.ENDER_CHEST)) return;
        if (block.getType().equals(Material.CRAFTING_TABLE)) return;
        if (block.getBlockData() instanceof Stairs) return;
        if (block.getState() instanceof Sign) return;
        if (!ClaimInfo.canInterract(block.getLocation(), player)) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            if (Objects.equals(event.getHand(), EquipmentSlot.HAND)) {
                denyMsg(player);
            }
        }
    }

    private void toolClaim(Player player, PlayerInteractEvent event, Block block) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) &&
                player.hasPermission("modo.claim.event.checkclaim") &&
                player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) {
            Region region;
            if (player.isSneaking()) {
                region = ClaimInfo.getRegion(block.getRelative(event.getBlockFace()).getLocation());
            } else {
                region = ClaimInfo.getRegion(block.getLocation());
            }
            if (region==null) {
                player.sendMessage(MainCore.prefixCmd + "§6Le block n'est pas claim");
            } else {
                player.sendMessage(MainCore.prefixCmd + "§6Région du block: §b" + region.getName());
            }
            event.setCancelled(true);
        } else if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                player.hasPermission("modo.claim.event.checkclaim")) &&
                player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD) &&
                player.isSneaking()) {
            ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
            if (meta!=null && MainClaim.regions.containsKey(meta.getDisplayName())) {
                Region region = MainClaim.regions.get(meta.getDisplayName());
                if (MainClaim.regionsBlocks.getOrDefault(block.getLocation(),"cite")
                        .equalsIgnoreCase("cite")) {
                    addToClaim(region, block, player);
                } else {
                    removeToClaim(block.getLocation(), player);
                }
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) return;
        if (!ClaimInfo.canInterract(event.getPlayer().getLocation().getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    private void addToClaim(Region region, Block block, Player player) {
        Connection connection = MainCore.getSQL().getConnection();
        try {
            Location loc = block.getLocation();
            PreparedStatement q = connection.prepareStatement("UPDATE `" + MainCore.SQLPREFIX + "regions` SET" +
                    " `rg_locs`=CONCAT(IFNULL(CONCAT(`rg_locs`,';'),''),?) WHERE `rg_id`=?;");
            q.setString(1, loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ());
            q.setInt(2, region.getId());
            q.execute();
            q.close();
            MainClaim.regionsBlocks.put(block.getLocation(), region.getName());
            player.sendMessage(MainCore.prefixCmd + "§6Block ajouté à §b" + region.getName());
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning(MainClaim.prefixConsole + "Erreur dans l'ajout d'un block à une région.");
            throwables.printStackTrace();
            player.sendMessage(MainCore.prefixCmd + "§cErreur dans l'ajout du block.");
        }
    }

    private void removeToClaim(Location loc, Player player) {
        Connection connection = MainCore.getSQL().getConnection();
        try {
            Region region = MainClaim.regions.get(MainClaim.regionsBlocks.get(loc));
            PreparedStatement q = connection.prepareStatement("SELECT `rg_locs` FROM `" + MainCore.SQLPREFIX +
                    "regions` WHERE `rg_id` = ?;");
            q.setInt(1, region.getId());
            q.execute();
            q.getResultSet().last();
            ArrayList<String> locs =
                    new ArrayList<>(Arrays.asList(q.getResultSet().getString("rg_locs").split(";")));
            locs.remove(loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ());
            String posSQL;
            if (locs.size()>0) {
                StringBuilder pos = new StringBuilder();
                for (String loop : locs) {
                    pos.append(loop);
                    pos.append(";");
                }
                posSQL = pos.substring(0, pos.length() - 1);
            } else {
                posSQL = null;
            }
            q.close();
            q = connection.prepareStatement("UPDATE `" + MainCore.SQLPREFIX +
                    "regions` SET `rg_locs` = ? WHERE `rg_id` = ?;");
            q.setString(1, posSQL);
            q.setInt(2, region.getId());
            q.execute();
            q.close();
            MainClaim.regionsBlocks.remove(loc);
            player.sendMessage(MainCore.prefixCmd + "§6Block retiré de la claim.");
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning(MainClaim.prefixConsole + "Erreur dans le remove d'un block à une région.");
            throwables.printStackTrace();
            player.sendMessage(MainCore.prefixCmd + "§cErreur dans le remove du block.");
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if (!event.getPlayer().getOpenInventory().getType().equals(InventoryType.MERCHANT)) {
            if (!MainClaim.isBuildMods(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemFrameDestroy(HangingBreakEvent event){
        if (event.getCause().equals(HangingBreakEvent.RemoveCause.OBSTRUCTION)) {
            event.setCancelled(true);
            event.getEntity().getLocation().getBlock().setType(Material.AIR);
            for (Player p : Bukkit.getOnlinePlayers()){
                if (MainClaim.isBuildMods(p)) {
                    p.sendMessage(MainCore.prefixCmd + "§cAttention ! Il ne faut pas poser de block sur une item frame (Pos:"+
                            LocToString.getStringLocation(event.getEntity().getLocation()) + ")");
                }
            }
        }
        else if (event.getCause().equals(HangingBreakEvent.RemoveCause.PHYSICS)) {
            event.setCancelled(true);
            for (Player p : Bukkit.getOnlinePlayers()){
                if (MainClaim.isBuildMods(p)) {
                    p.sendMessage(MainCore.prefixCmd + "§cAttention ! Item frame volante : "
                            + LocToString.getStringLocation(event.getEntity().getLocation()));
                }
            }
        }
    }

    @EventHandler
    public void onItemFrameBy(HangingBreakByEntityEvent event){
        if (!(event.getRemover() instanceof Player)) {
            event.setCancelled(true);
        } else if (!MainClaim.isBuildMods((Player) event.getRemover())) {
            event.setCancelled(true);
            denyMsg((Player) event.getRemover());
        }
    }

    @EventHandler
    public void onItemFromItemFrameBy(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof ItemFrame) {
            if (!(event.getDamager() instanceof Player)) {
                event.setCancelled(true);
            } else if (!MainClaim.isBuildMods((Player) event.getDamager())) {
                event.setCancelled(true);
                denyMsg((Player) event.getDamager());
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onItemTurnItemFrameBy(PlayerInteractEntityEvent event){
        if (event.getRightClicked() instanceof ItemFrame) {
            if (!MainClaim.isBuildMods(event.getPlayer())) {
                event.setCancelled(true);
                denyMsg(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event){
        if (event.getPlayer()==null) {
            event.setCancelled(true);
        } else if (!MainClaim.isBuildMods(event.getPlayer())) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
        } else if (!MainClaim.isBuildMods((Player) event.getEntity())) {
            event.setCancelled(true);
            denyMsg((Player) event.getEntity());
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || MainClaim.isBuildMods((Player) event.getDamager())) return;
        if (!ClaimInfo.canBuild(event.getEntity().getLocation(), (Player) event.getDamager())) {
            event.setCancelled(true);
            denyMsg((Player) event.getDamager());
        }
    }

    @EventHandler
    public void onSpawnPotionThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof ThrownPotion)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawnPotionSplash(PotionSplashEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPrimeExplode(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onSaturation(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (ClaimInfo.getRegion(block.getLocation())==null ||
                    ClaimInfo.getRegion(block.getLocation()).getName().equalsIgnoreCase("interract-ok")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (ClaimInfo.getRegion(block.getLocation())==null ||
                    ClaimInfo.getRegion(block.getLocation()).getName().equalsIgnoreCase("interract-ok")) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
