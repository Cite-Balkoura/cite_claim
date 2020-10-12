package fr.milekat.cite_claim.utils;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.obj.Region;
import fr.milekat.cite_core.core.obj.Profil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ClaimInfo {
    /**
     *      Récupère le nom de la région à la pos (BlockPos)
     * @param location du block !
     * @return nom de la région (cite par défaut)
     */
    public static Region getRegion(Location location) {
        return MainClaim.regions.getOrDefault(MainClaim.regionsBlocks.get(location),null);
    }

    /**
     *      Check si le joueur peut intéragir avec le block
     * @param location pos du block
     * @param p joueur
     * @return autorisé true/false
     */
    public static boolean canInterract(Location location, Player p){
        if (MainClaim.isBuildMods(p)) return true;
        Region region = getRegion(location);
        if (region == null) return false;
        if (region.getName().equalsIgnoreCase("interact-ok")) return true;
        return canBuild(location,p);
    }

    /**
     *      Check si le joueur peut consuitre à la pos du block (==Il fait parti de l'équipe qui poscède la zone)
     * @param location pos du block
     * @param p joueur
     * @return autorisé true/false
     */
    public static boolean canBuild(Location location, Player p) {
        if (MainClaim.isBuildMods(p)) return true;
        Region region = getRegion(location);
        if (region == null) return false;
        if (region.getTeam() == null) return false;
        for (Profil profil : region.getTeam().getMembers()) {
            if (profil.getUuid().equals(p.getUniqueId())) return true;
        }
        return false;
    }
}
