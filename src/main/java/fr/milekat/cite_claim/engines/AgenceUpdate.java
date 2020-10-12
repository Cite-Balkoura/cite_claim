package fr.milekat.cite_claim.engines;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.obj.Region;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.ArrayList;

public class AgenceUpdate {
    public BukkitTask runTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                ArrayList<BaseComponent[]> pages = new ArrayList<>();
                for (Region region : MainClaim.regions.values()) {
                    if (region.getName().equalsIgnoreCase("interact-ok")) continue;
                    String rgLoc = "§cInconnue";
                    if (region.getSign() != null) {
                        Location location = region.getSign().getBlock().getLocation();
                        rgLoc = "§3" + location.getBlockX() + "§c, §3" + location.getBlockY() + "§c, §3" + location.getBlockZ();
                    }
                    if (region.getTeam() == null) {
                        pages.add(new BookUtil.PageBuilder()
                                .newLine()
                                .add(BookUtil.TextBuilder.of("  §7§m--§7[ §6Habitation §c" + (region.getId()-1) + " §7]§7§m--").build())
                                .newLine().newLine().newLine().newLine()
                                .add(BookUtil.TextBuilder.of("§8Nom§c: §3" + region.getName()).build())
                                .newLine()
                                .add(BookUtil.TextBuilder.of("§8Prix§c: §2" + region.getPrix()).build())
                                .newLine()
                                .add(BookUtil.TextBuilder.of("§8Disponibilité§c: §aÀ VENDRE").build())
                                .newLine().newLine()
                                .add(BookUtil.TextBuilder.of("§8Surface à build§c: §3" + region.getBlocks().size()).build())
                                .newLine()
                                .add(BookUtil.TextBuilder.of("§8Pos§c: §3" + rgLoc).build())
                                .newLine()
                                .add(BookUtil.TextBuilder.of("§8Quartier§c: §3" +
                                        (region.getQuartier()==null?"§cInconnue":region.getQuartier())).build())
                                .build());
                    } else pages.add(new BookUtil.PageBuilder()
                            .newLine()
                            .add(BookUtil.TextBuilder.of("  §7§m--§7[ §6Habitation §c" + (region.getId()-1) + " §7]§7§m--").build())
                            .newLine().newLine().newLine().newLine()
                            .add(BookUtil.TextBuilder.of("§8Nom§c: §3" + region.getName()).build())
                            .newLine()
                            .add(BookUtil.TextBuilder.of("§8Prix§c: §2" + region.getPrix()).build())
                            .newLine()
                            .add(BookUtil.TextBuilder.of("§8Disponibilité§c: §cVENDU").build())
                            .newLine().newLine()
                            .add(BookUtil.TextBuilder.of("§8Surface à build§c: §3" + region.getBlocks().size()).build())
                            .newLine()
                            .add(BookUtil.TextBuilder.of("§8Pos§c: §3" + rgLoc).build())
                            .newLine()
                            .add(BookUtil.TextBuilder.of("§8Quartier§c: §3" +
                                    (region.getQuartier()==null?"§cInconnue":region.getQuartier())).build())
                            .newLine()
                            .add(BookUtil.TextBuilder.of("§8Détenue par§c:").build())
                            .newLine()
                            .add(BookUtil.TextBuilder.of("§3" + region.getTeam().getName()).build())
                            .build());
                }
                MainClaim.bookAgence = BookUtil.writtenBook()
                        .author("MileKat")
                        .title("Liste des habitations")
                        .pages(pages)
                        .generation(BookMeta.Generation.TATTERED)
                        .build();
            }
        }.runTaskTimerAsynchronously(MainClaim.getInstance(),0L,1200L);
    }
}
