package fr.milekat.cite_claim.ascenseur.events;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.ascenseur.obj.Ascenseur;
import fr.milekat.cite_core.MainCore;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class AscenseurListener implements Listener {
    private BukkitTask task;

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerClickAscenseur(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && MainClaim.boutonAscenseur.containsKey(event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
            Ascenseur ascenseur = MainClaim.boutonAscenseur.get(event.getClickedBlock().getLocation());
            if (!ascenseur.isComplete()) {
                event.getPlayer().sendMessage(MainCore.prefixCmd + "§cAscenseur hors service.");
                return;
            }
            if (ascenseur.isWork()) {
                event.getPlayer().sendMessage(MainCore.prefixCmd + "§cAscenseur en mouvement.");
                return;
            }
            if (ascenseur.getBoutonDown().equals(event.getClickedBlock().getLocation())) {
                if (ascenseur.getFloor() == 0) {
                    // Faire monter
                    monter(ascenseur);
                    event.getPlayer().sendMessage(MainCore.prefixCmd + "§6Place toi dans la cabine, je monte dans 5s.");
                } else {
                    // Faire Descendre
                    descendre(ascenseur);
                    event.getPlayer().sendMessage(MainCore.prefixCmd + "§6J'appel l'ascenseur.");
                }
            } else {
                if (ascenseur.getFloor() == 1) {
                    // Faire Descendre
                    descendre(ascenseur);
                    event.getPlayer().sendMessage(MainCore.prefixCmd + "§6Place toi dans la cabine, je descend dans 5s.");
                } else {
                    // Faire monter
                    monter(ascenseur);
                    event.getPlayer().sendMessage(MainCore.prefixCmd + "§6J'appel l'ascenseur.");
                }
            }
        }
    }

    /**
     *      Faire monter l'ascensseur
     */
    private void monter(Ascenseur ascenseur) {
        NPC npc = CitizensAPI.getNPCRegistry().getById(ascenseur.getNpcId());
        for (Block block : ascenseur.getDoorDown()) block.setType(Material.AIR);
        ascenseur.setWork(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(MainClaim.getInstance(), ascenseur::closeDoor,80L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(MainClaim.getInstance(), () -> {
            ((LivingEntity) npc.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,
                    500, 5, false, false, false));
            ArrayList<Player> passengers = new ArrayList<>();
            Bukkit.getScheduler().scheduleSyncDelayedTask(MainClaim.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers())
                    if (player.getLocation().distance(npc.getEntity().getLocation()) <= 2) {
                        passengers.add(player);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,
                                500, 5, false, false, false));
                    }
                for (Block block: getFullFloorUp(ascenseur)) block.setType(Material.AIR);
            },2L);
            for (Block block : ascenseur.getDoorDown()) block.setType(Material.OAK_FENCE);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (npc.getEntity().getLocation().getY() > ascenseur.getFloorUpLoc().getBlockY() + 0.5D) {
                        for (Block block : getFullFloorUp(ascenseur)) block.setType(Material.BARRIER);
                        Location location = ascenseur.getFloorUpLoc().clone();
                        location.setY(location.getBlockY() + 1);
                        location.setYaw(ascenseur.getYawUp());
                        leavePassengers(ascenseur, npc, location, passengers);
                        for (Block block : ascenseur.getDoorUp()) block.setType(Material.AIR);
                        ascenseur.openDoor();
                        ascenseur.setFloor(1);
                        cancel();
                    }
                }
            }.runTaskTimer(MainClaim.getInstance(),50L,5L);
        },100L);
    }

    /**
     *      Faire descendre l'ascensseur
     */
    private void descendre(Ascenseur ascenseur) {
        NPC npc = CitizensAPI.getNPCRegistry().getById(ascenseur.getNpcId());
        for (Block block : ascenseur.getDoorUp()) block.setType(Material.AIR);
        ascenseur.setWork(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(MainClaim.getInstance(), ascenseur::closeDoor,85L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(MainClaim.getInstance(), () -> {
            ((LivingEntity) npc.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,
                    500, -5, false, false, false));
            ArrayList<Player> passengers = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers())
                if (player.getLocation().distance(npc.getEntity().getLocation()) <= 2) {
                    passengers.add(player);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,
                            500, -5, false, false, false));
                }
            Bukkit.getScheduler().scheduleSyncDelayedTask(MainClaim.getInstance(), () -> {
                for (Block block: getFullFloorUp(ascenseur)) block.setType(Material.AIR);
                for (Player player: passengers) {
                    Location location = player.getLocation();
                    location.setY(location.getY() + 0.4);
                    player.teleport(location);
                }
            },5L);
            for (Block block : ascenseur.getDoorUp()) block.setType(Material.OAK_FENCE);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (npc.getEntity().getLocation().getY() < ascenseur.getFloorDownLoc().clone().getBlockY() + 1.01D) {
                        for (Block block : getFullFloorUp(ascenseur)) block.setType(Material.BARRIER);
                        Location location = ascenseur.getFloorDownLoc().clone();
                        location.setY(location.getBlockY() + 1);
                        location.setYaw(ascenseur.getYawDown());
                        leavePassengers(ascenseur, npc, location, passengers);
                        for (Block block : ascenseur.getDoorDown()) block.setType(Material.AIR);
                        ascenseur.openDoor();
                        ascenseur.setFloor(0);
                        cancel();
                    }
                }
            }.runTaskTimer(MainClaim.getInstance(),50L,5L);
        },100L);
    }

    private void leavePassengers(Ascenseur ascenseur, NPC npc, Location location, ArrayList<Player> passengers) {
        for (Player player: passengers) {
            player.removePotionEffect(PotionEffectType.LEVITATION);
            Location playerLocation = player.getLocation();
            playerLocation.setY(location.getBlockY());
            player.teleport(playerLocation);
        }
        ((LivingEntity) npc.getEntity()).removePotionEffect(PotionEffectType.LEVITATION);
        npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        ascenseur.setWork(false);
    }


    /**
     *      Récupère les 9 blocs du floorUp
     */
    private ArrayList<Block> getFullFloorUp(Ascenseur ascenseur) {
        //First of all, we create the list:
        ArrayList<Block> blocks = new ArrayList<>();
        //Next we will name each coordinate
        int x1 = ascenseur.getFloorUpLoc().getBlockX() + 1;
        int z1 = ascenseur.getFloorUpLoc().getBlockZ() + 1;
        int x2 = ascenseur.getFloorUpLoc().getBlockX() - 1;
        int z2 = ascenseur.getFloorUpLoc().getBlockZ() - 1;
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
            for(z = zMin; z <= zMax; z ++){
                blocks.add(new Location(ascenseur.getFloorUpLoc().getWorld(), x, ascenseur.getFloorUpLoc().getBlockY(), z).getBlock());
            }
        }
        //And last but not least, we return with the list
        return blocks;
    }
}
