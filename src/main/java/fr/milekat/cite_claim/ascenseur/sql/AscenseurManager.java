package fr.milekat.cite_claim.ascenseur.sql;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.ascenseur.obj.Ascenseur;
import fr.milekat.cite_core.MainCore;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AscenseurManager {
    /**
     *      Save tous les ascenseurs
     */
    public void saveAllAscenseur() {
        for (Ascenseur ascenseur: MainClaim.ascenseurs.values()) saveAscenseur(ascenseur);
    }

    /**
     *      Fonction pour save un Ascenseur
     */
    public void saveAscenseur(Ascenseur ascenseur) {
        Connection connection = MainCore.getSQL().getConnection();
        try {
            PreparedStatement q = connection.prepareStatement("INSERT INTO `" + MainCore.SQLPREFIX + "ascenseurs` " +
                    "(`asc_name`, `npc_id`, `floorDownLoc`, " +
                    "`floorUpLoc`, `yawDown`, `yawUp`, `boutonDown`, `boutonUp`, `signDown`, `signUp`, `doorDown`, `doorUp`) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " +
                    "`asc_name` = ?, `npc_id` = ?, `floorDownLoc`= ?, `floorUpLoc`= ?, `yawDown`= ?, `yawUp`= ?, " +
                    "`boutonDown`= ?, `boutonUp`= ?, `signDown`= ?, `signUp` = ?, `doorDown` = ?, `doorUp` = ?;");
            q.setString(1, ascenseur.getName());
            q.setInt(2, ascenseur.getNpcId());
            q.setString(3, getStringLocation(ascenseur.getFloorDownLoc()));
            q.setString(4, getStringLocation(ascenseur.getFloorUpLoc()));
            q.setInt(5, ascenseur.getYawDown());
            q.setInt(6, ascenseur.getYawUp());
            q.setString(7, getStringLocation(ascenseur.getBoutonDown()));
            q.setString(8, getStringLocation(ascenseur.getBoutonUp()));
            q.setString(9, getStringLocation(ascenseur.getSignDown()));
            q.setString(10, getStringLocation(ascenseur.getSignUp()));
            StringBuilder doorDown = new StringBuilder();
            if (ascenseur.getDoorDown() != null)
                for (Block block: ascenseur.getDoorDown()) {
                    doorDown.append(getStringLocation(block.getLocation()));
                    doorDown.append(";");
                }
            StringBuilder doorUp = new StringBuilder();
            if (ascenseur.getDoorUp() != null)
                for (Block block: ascenseur.getDoorUp()) {
                    doorUp.append(getStringLocation(block.getLocation()));
                    doorUp.append(";");
                }
            q.setString(11, doorDown.toString());
            q.setString(12, doorUp.toString());
            q.setString(13, ascenseur.getName());
            q.setInt(14, ascenseur.getNpcId());
            q.setString(15, getStringLocation(ascenseur.getFloorDownLoc()));
            q.setString(16, getStringLocation(ascenseur.getFloorUpLoc()));
            q.setInt(17, ascenseur.getYawDown());
            q.setInt(18, ascenseur.getYawUp());
            q.setString(19, getStringLocation(ascenseur.getBoutonDown()));
            q.setString(20, getStringLocation(ascenseur.getBoutonUp()));
            q.setString(21, getStringLocation(ascenseur.getSignDown()));
            q.setString(22, getStringLocation(ascenseur.getSignUp()));
            q.setString(23, doorDown.toString());
            q.setString(24, doorUp.toString());
            q.execute();
            q.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     *      Chargement des Ascenseurs
     */
    public void loadAscenseur(String asc_name) {
        Connection connection = MainCore.getSQL().getConnection();
        try {
            PreparedStatement q;
            if (asc_name == null) {
                q = connection.prepareStatement("SELECT * FROM `" + MainCore.SQLPREFIX + "ascenseurs`;");
            }
            else {
                q = connection.prepareStatement("SELECT * FROM `" + MainCore.SQLPREFIX + "ascenseurs` WHERE `asc_name` = ?;");
                q.setString(1, asc_name);
            }
            q.execute();
            while (q.getResultSet().next()) {
                Location signDown = null;
                Location signUp = null;
                if (q.getResultSet().getString("signDown")!=null) signDown =
                        getLocationString(q.getResultSet().getString("signDown")).getBlock().getLocation();
                if (q.getResultSet().getString("signUp")!=null) signUp =
                        getLocationString(q.getResultSet().getString("signUp")).getBlock().getLocation();
                Location floorDownLoc = getLocationString(q.getResultSet().getString("floorDownLoc"));
                Location floorUpLoc = getLocationString(q.getResultSet().getString("floorUpLoc"));
                Location boutonDown = getLocationString(q.getResultSet().getString("boutonDown"));
                Location boutonUp = getLocationString(q.getResultSet().getString("boutonUp"));
                ArrayList<Block> doorDown = new ArrayList<>();
                if (q.getResultSet().getString("doorDown")!=null)
                    for (String locs: new ArrayList<>(Arrays.asList(q.getResultSet().getString("doorDown").split(";"))))
                        doorDown.add(getLocationString(locs).getBlock());
                ArrayList<Block> doorUp = new ArrayList<>();
                if (q.getResultSet().getString("doorUp")!=null)
                    for (String locs: new ArrayList<>(Arrays.asList(q.getResultSet().getString("doorUp").split(";"))))
                        doorUp.add(getLocationString(locs).getBlock());
                Ascenseur ascenseur = new Ascenseur(
                        q.getResultSet().getString("asc_name"), q.getResultSet().getInt("npc_id"),
                        floorDownLoc, floorUpLoc,
                        q.getResultSet().getInt("yawDown"), q.getResultSet().getInt("yawUp"),
                        boutonDown, boutonUp, doorDown, doorUp, signDown, signUp);
                MainClaim.ascenseurs.put(q.getResultSet().getString("asc_name"), ascenseur);
                if (boutonDown != null) MainClaim.boutonAscenseur.put(boutonDown, ascenseur);
                if (boutonUp != null) MainClaim.boutonAscenseur.put(boutonUp, ascenseur);
                if (floorDownLoc != null) {
                    Location npcbaseloc = floorDownLoc.clone();
                    npcbaseloc.setY(floorDownLoc.getBlockY() + 1);
                    NPC npc = CitizensAPI.getNPCRegistry().getById(q.getResultSet().getInt("npc_id"));
                    if (npc!=null) npc.teleport(npcbaseloc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    ascenseur.openDoor();
                    for (Block block : ascenseur.getDoorDown()) block.setType(Material.AIR);
                }
                if (floorUpLoc != null) {
                    for (Block block: ascenseur.getDoorUp()) block.setType(Material.OAK_FENCE);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        } catch (ClassCastException exception) {
            Bukkit.getLogger().warning(MainClaim.prefixConsole + "Erreur chargement ascenseur, le block n'est pas un Sign.");
        }
    }

    public String getStringLocation(final Location l) {
        if (l == null) return null;
        return Objects.requireNonNull(l.getWorld()).getName() + ":" + l.getX() + ":" + l.getY() + ":" + l.getZ();
    }

    public static Location getLocationString(final String s) {
        if (s == null || s.trim().equals("") || s.trim().equalsIgnoreCase("null")) {
            return null;
        }
        final String[] parts = s.split(":");
        if (parts.length == 4) {
            final World w = Bukkit.getServer().getWorld(parts[0]);
            final double x = Double.parseDouble(parts[1]);
            final double y = Double.parseDouble(parts[2]);
            final double z = Double.parseDouble(parts[3]);
            return new Location(w, x, y, z);
        }
        return null;
    }
}
