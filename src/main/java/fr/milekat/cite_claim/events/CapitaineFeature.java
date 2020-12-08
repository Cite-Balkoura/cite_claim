package fr.milekat.cite_claim.events;

import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_core.core.bungee.ServersManagerSendPlayer;
import fr.milekat.cite_libs.MainLibs;
import fr.milekat.cite_libs.utils_tools.Jedis.JedisServer;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Arrays;

public class CapitaineFeature implements Listener {
    private final ArrayList<String> survies = new ArrayList<>(Arrays.asList("survie_prague", "survie_sydney", "survie_bogota"));

    @EventHandler
    public void onBoatCapitaineClick(NPCRightClickEvent event) {
        String loctype;
        if (event.getNPC().getId()==36) {
            loctype = "boat";
        } else if (event.getNPC().getId() >= 37 && event.getNPC().getId() <= 42) {
            loctype = "balloon";
        } else return;
        FastInv gui = new FastInv(InventoryType.HOPPER, ChatColor.DARK_AQUA + "Choisis ton cap !");
        gui.setItem(0, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build(), e -> e.setCancelled(true));
        gui.setItem(4, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build(), e -> e.setCancelled(true));
        for (String loop : MainCore.serveurPlayers.keySet()) {
            if (survies.contains(loop)) {
                Integer players = MainCore.serveurPlayers.get(loop);
                JedisServer server = MainLibs.jedisServers.get(loop);
                gui.addItem(new ItemBuilder(server.getMaterial()).name(server.getName()).addLore("Population " + players).build(), e -> {
                    e.setCancelled(true);
                    new ServersManagerSendPlayer().sendPlayerToServer(((Player) e.getWhoClicked()),server.getChannel(),loctype);
                });
            }
        }
        gui.open(event.getClicker());
    }


}
