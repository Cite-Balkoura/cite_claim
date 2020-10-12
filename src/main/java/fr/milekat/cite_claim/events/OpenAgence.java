package fr.milekat.cite_claim.events;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_core.MainCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.upperlevel.spigot.book.BookUtil;

public class OpenAgence implements Listener {
    private final Location locationAgenge = new Location(Bukkit.getWorld("world"),-11,155,-4);

    @EventHandler(priority = EventPriority.LOW)
    public void onOpenAgence(PlayerInteractEvent event) {
        if (event.getClickedBlock()!=null && event.getClickedBlock().getLocation().equals(locationAgenge)) {
            event.setCancelled(true);
            if (MainClaim.bookAgence!=null) {
                BookUtil.openPlayer(event.getPlayer(),MainClaim.bookAgence);
            } else event.getPlayer().sendMessage(MainCore.prefixCmd + "§cAgence fermée, contact les admins.");
        }
    }
}
