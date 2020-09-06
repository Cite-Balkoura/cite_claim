package fr.milekat.cite_claim.events;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.utils.ClaimInfo;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BoundClaim implements Listener {

    @EventHandler
    public void onLeftBound(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("modo.claim.event.claim")) {
            if (event.getItem()==null || event.getClickedBlock()==null) return;
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK))
                if (event.getItem().getType().equals(Material.WOODEN_AXE)) {
                    MainClaim.boundRegionleft.put(event.getPlayer().getUniqueId(),event.getClickedBlock().getLocation());
                }
        }
    }

    @EventHandler
    public void onRightBound(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("modo.claim.event.claim")) {
            if (event.getItem()==null || event.getClickedBlock()==null) return;
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            if (event.getItem().getType().equals(Material.WOODEN_AXE)) {
                MainClaim.boundRegionright.put(event.getPlayer().getUniqueId(),event.getClickedBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onRightCheckBound(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("modo.claim.event.claim")) {
            if (event.getItem()==null || event.getClickedBlock()==null) return;
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                if (event.getItem().getType().equals(Material.BLAZE_ROD)) {
                    ClaimInfo.getRegion(event.getClickedBlock().getLocation());
                }
        }
    }
}
