package fr.milekat.cite_claim.events;

import fr.milekat.cite_libs.utils_tools.LocToString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceEvent implements Listener {

    @EventHandler
    public void PlaceEvent(BlockPlaceEvent event){
        Block theblock = event.getBlock();
        Material thematerial = theblock.getType();
        Location theloc = theblock.getLocation();
        Player thep = event.getPlayer();
        if (thematerial.equals(Material.TNT)){
            String theloc2 = LocToString.getStringLocation(theloc);
            thep.sendMessage("Loc:" + theloc2);
            event.setCancelled(true);
        }
    }
}
