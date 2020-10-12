package fr.milekat.cite_claim;

import fr.milekat.cite_claim.commands.CommandsRegion;
import fr.milekat.cite_claim.commands.ModeBuilder;
import fr.milekat.cite_claim.commands.TabsRegion;
import fr.milekat.cite_claim.engines.AgenceUpdate;
import fr.milekat.cite_claim.engines.RegionsTask;
import fr.milekat.cite_claim.engines.SpeedBoost;
import fr.milekat.cite_claim.events.EventsCite;
import fr.milekat.cite_claim.events.OpenAgence;
import fr.milekat.cite_claim.events.RegionMarket;
import fr.milekat.cite_claim.obj.Region;
import fr.milekat.cite_claim.utils.RegionsBlocksLoad;
import fr.milekat.cite_core.MainCore;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainClaim extends JavaPlugin implements Listener {
    // Init des var static, pour tous le projet
    public static String prefixConsole = "[Balkoura-claim] ";
    public static LinkedHashMap<String, Region> regions = new LinkedHashMap<>();
    public static HashMap<Location, String> regionsBlocks = new HashMap<>();
    public static ItemStack bookAgence;
    private static MainClaim mainClaim;
    private BukkitTask regionsEngine;
    private BukkitTask speedEngine;
    private BukkitTask agenceEngine;

    @Override
    public void onEnable() {
        mainClaim = this;
        // Reload de la map des blocks
        new RegionsBlocksLoad().reloadRegions();
        new RegionsTask().updateRegion();
        regionsEngine = new RegionsTask().runTask();
        speedEngine = new SpeedBoost().runTask();
        agenceEngine = new AgenceUpdate().runTask();
        // Events
        getServer().getPluginManager().registerEvents(new EventsCite(),this);
        getServer().getPluginManager().registerEvents(new RegionMarket(),this);
        getServer().getPluginManager().registerEvents(new OpenAgence(),this);
        // Commandes
        getCommand("build").setExecutor(new ModeBuilder());
        getCommand("region").setExecutor(new CommandsRegion());
        // Tabs
        getCommand("region").setTabCompleter(new TabsRegion());
    }

    @Override
    public void onDisable(){
        regionsEngine.cancel();
        speedEngine.cancel();
        agenceEngine.cancel();
    }

    public static MainClaim getInstance(){
        return mainClaim;
    }

    public static boolean isBuildMods(Player player) {
        if (!MainCore.profilHashMap.containsKey(player.getUniqueId())) return false;
        return MainCore.profilHashMap.get(player.getUniqueId()).isMods_build();
    }
}