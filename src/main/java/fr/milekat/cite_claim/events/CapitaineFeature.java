package fr.milekat.cite_claim.events;

import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_core.core.bungee.ServersManager;
import fr.milekat.cite_core.core.bungee.ServersUpdate;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CapitaineFeature implements Listener {
    private final ArrayList<String> survies = (ArrayList<String>) Arrays.asList(new String[]{"prague", "sydney", "bogota"});

    @EventHandler
    public void onBoatCapitaineClick(NPCClickEvent event) {
        String loctype;
        if (event.getNPC().getId()==36) {
            loctype = "boat";
        } else if (event.getNPC().getId() >= 37 && event.getNPC().getId() <= 42) {
            loctype = "balloon";
        } else return;
        FastInv gui = new FastInv(InventoryType.HOPPER, ChatColor.DARK_AQUA + "Choisis ton cap !");
        gui.setItems(0, 4, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).build());
        for (Map.Entry<Integer, String> loop : MainCore.serveurPlayers.entrySet()) {
            if (survies.contains(loop.getValue())) {
                gui.addItem(new ItemBuilder(Material.GRASS_BLOCK).name(loop.getValue()).name("").addLore("Population " + loop.getKey())
                        .build(), e -> {
                    new ServersManager().sendPlayerToServer(((Player) e.getWhoClicked()), loop.getValue(), loctype);
                });
            }
        }
        gui.open(event.getClicker());
    }


}
