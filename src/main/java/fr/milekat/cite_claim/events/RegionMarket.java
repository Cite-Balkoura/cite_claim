package fr.milekat.cite_claim.events;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.obj.Region;
import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_core.core.obj.Profil;
import fr.milekat.cite_core.core.obj.Team;
import fr.milekat.cite_libs.MainLibs;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class RegionMarket implements Listener {
    public static final String PREFIX = "§8[§bBalkou§2Immo§8]";
    public static final String AVENDRE = "§b*§aà vendre§b*";
    public static final String VENDU = "§b*§cVendu§b*";
    public static final float RESELLPERCENT = 80;

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock()==null) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) event.getClickedBlock().getState();
        if (!sign.getLine(0).equalsIgnoreCase(PREFIX)) return;
        event.setCancelled(true);
        Profil profil = MainCore.profilHashMap.get(event.getPlayer().getUniqueId());
        if (profil.getTeam()==0) {
            event.getPlayer().sendMessage(MainCore.prefixCmd + "§cVous n'avez pas d'équipe.");
            return;
        }
        Team team = MainCore.teamHashMap.get(profil.getTeam());
        Region region = MainClaim.regions.get(sign.getLine(1));
        if (sign.getLine(3).equalsIgnoreCase(AVENDRE)) {
            if (team.getMoney()>=region.getPrix()) {
                if (event.getPlayer().isSneaking()) {
                    buyRegion(event.getPlayer(), team, region, sign);
                } else {
                    event.getPlayer().sendMessage(MainCore.prefixCmd + "§6Sneak pour acheter cette habitation !");
                }
            } else {
                event.getPlayer().sendMessage(MainCore.prefixCmd + "§cVotre équipe n'a pas assez d'argent.");
            }
        } else if (sign.getLine(3).equalsIgnoreCase(VENDU)) {
            if (region.getTeam() != null && profil.getTeam() != 0 && region.getTeam().getId() == profil.getTeam()) {
                if (event.getPlayer().isSneaking()) {
                    sellRegion(event.getPlayer(), team, region, sign);
                } else {
                    event.getPlayer().sendMessage(MainCore.prefixCmd + "§6Sneak pour vendre cette habitation !");
                }
            } else {
                event.getPlayer().sendMessage(MainCore.prefixCmd + "§cVotre équipe n'est pas propriétaire de la région.");
            }
        }
    }

    /**
     *      Application de l'achat de la région
     */
    private void buyRegion(Player player, Team team, Region region, Sign sign) {
        region.setTeam(team);
        team.setMoney(team.getMoney()-region.getPrix());
        sign.setLine(2,team.getName());
        sign.setLine(3,VENDU);
        sign.update();
        updateTranscation(team, region);
        player.sendMessage(MainCore.prefixCmd + "§6Vous venez d'acheter la région §b" + region.getName() + "§6.");
        Bukkit.getLogger().info(
                MainClaim.prefixConsole + "L'équipe " + team.getName() + " achète la région " + region.getName());
    }

    /**
     *      Application de la vente de la région, revente à RESELLPERCENT% du prix de base
     */
    private void sellRegion(Player player, Team team, Region region, Sign sign) {
        region.setTeam(null);
        team.setMoney(team.getMoney()+Math.round((region.getPrix()*RESELLPERCENT/100)));
        sign.setLine(2,"§6Prix§c: §2" + region.getPrix());
        sign.setLine(3,AVENDRE);
        sign.update();
        updateTranscation(team, region);
        player.sendMessage(MainCore.prefixCmd + "§6Vous venez de vendre la région §b" + region.getName() + "§6.");
        Bukkit.getLogger().info(
                MainClaim.prefixConsole + "L'équipe " + team.getName() + " vends la région " + region.getName());
    }

    /**
     *      Mise à jour de l'argent de l'équipe & de l'équipe titulaire de la région
     */
    private void updateTranscation(Team team, Region region) {
        Connection connection = MainLibs.getSql();
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `" + MainCore.SQLPREFIX +
                    "regions` SET `team_id` = ? WHERE `rg_id` = ?;" +
                    "UPDATE `" + MainCore.SQLPREFIX + "team` SET `money` = ? WHERE `team_id` = ?;");
            if (region.getTeam()==null) {
                q.setNull(1, Types.NULL);
            } else {
                q.setInt(1, region.getTeam().getId());
            }
            q.setInt(2, region.getId());
            q.setInt(3, team.getMoney());
            q.setInt(4, team.getId());
            q.execute();
            q.close();
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning("Erreur d'update SQL suite à transaction.");
            throwables.printStackTrace();
        }
    }
}
