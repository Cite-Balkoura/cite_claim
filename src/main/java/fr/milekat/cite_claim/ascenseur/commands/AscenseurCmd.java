package fr.milekat.cite_claim.ascenseur.commands;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.ascenseur.obj.Ascenseur;
import fr.milekat.cite_claim.ascenseur.sql.AscenseurManager;
import fr.milekat.cite_core.MainCore;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AscenseurCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Commende pour un joueur seulement.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) sendHelp(sender, label);
        else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2]));
            if (npc!=null) MainClaim.ascenseurs.put(args[1], new Ascenseur(
                    args[1], Integer.parseInt(args[2]),
                    null, null,
                    0,0,
                    null, null,
                    null, null, null, null));
            else sender.sendMessage(MainCore.prefixCmd + "§cNPC non trouvé.");
        } else if (args.length >= 4 && args[0].equalsIgnoreCase("update")) {
            if (MainClaim.ascenseurs.containsKey(args[1])) {
                Ascenseur ascenseur = MainClaim.ascenseurs.get(args[1]);
                Block block = player.getTargetBlockExact(10);
                if (block!=null && args[2].equalsIgnoreCase("floor")) {
                    if (args[3].equalsIgnoreCase("haut")) ascenseur.setFloorUpLoc(block.getLocation());
                    else if (args[3].equalsIgnoreCase("bas")) ascenseur.setFloorDownLoc(block.getLocation());
                } else if (block!=null && args[2].equalsIgnoreCase("bouton")) {
                    if (args[3].equalsIgnoreCase("haut")) ascenseur.setBoutonUp(block.getLocation());
                    else if (args[3].equalsIgnoreCase("bas")) ascenseur.setBoutonDown(block.getLocation());
                } else if (block!=null && args[2].equalsIgnoreCase("sign")) {
                    if (block.getState() instanceof Sign) {
                        if (args[3].equalsIgnoreCase("haut")) ascenseur.setSignUp(block.getLocation());
                        else if (args[3].equalsIgnoreCase("bas")) ascenseur.setSignDown(block.getLocation());
                    } else sender.sendMessage(MainCore.prefixCmd + "§cMerci de regarder un panneau");
                } else if (args.length == 5 && block!=null && args[2].equalsIgnoreCase("yaw")) {
                    if (args[3].equalsIgnoreCase("haut")) ascenseur.setYawUp(Integer.parseInt(args[4]));
                    else if (args[3].equalsIgnoreCase("bas")) ascenseur.setYawDown(Integer.parseInt(args[4]));
                } else if (block!=null && args[2].equalsIgnoreCase("door")) {
                    if (args[3].equalsIgnoreCase("haut")){
                        ArrayList<Block> door = ascenseur.getDoorUp();
                        door.add(block);
                        ascenseur.setDoorUp(door);
                    } else if (args[3].equalsIgnoreCase("bas")){
                        ArrayList<Block> door = ascenseur.getDoorDown();
                        door.add(block);
                        ascenseur.setDoorDown(door);
                    }
                } else if (block!=null && args[2].equalsIgnoreCase("npc")) {
                    NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3]));
                    if (npc!=null) ascenseur.setNpcId(npc.getId());
                }
            } else sender.sendMessage(MainCore.prefixCmd + "§cAscenseur non trouvé.");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reload")) {
            if (args[1].equalsIgnoreCase("all")) {
                new AscenseurManager().loadAscenseur(null);
            } else {
                if (MainClaim.ascenseurs.containsKey(args[1]))
                    new AscenseurManager().loadAscenseur(MainClaim.ascenseurs.get(args[1]).getName());
                else sender.sendMessage(MainCore.prefixCmd + "§cAscenseur non trouvé.");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("save")) {
            if (args[1].equalsIgnoreCase("all")) {
                new AscenseurManager().saveAllAscenseur();
            } else {
                if (MainClaim.ascenseurs.containsKey(args[1]))
                    new AscenseurManager().saveAscenseur(MainClaim.ascenseurs.get(args[1]));
                else sender.sendMessage(MainCore.prefixCmd + "§cAscenseur non trouvé.");
            }
        } else sendHelp(sender, label);
        return true;
    }

    /**
     *      Envoie la liste d'help de la commande
     * @param sender joueur qui exécute la commande
     */
    private void sendHelp(CommandSender sender, String prefix) {
        sender.sendMessage(MainCore.prefixCmd);
        sender.sendMessage("§6/" + prefix + " add <nom_ascenseur> <NPC_ID>:§r Ajoute un ascenseur avec son groom.");
        sender.sendMessage("§6/" + prefix +
                " update <nom_ascenseur> floor <haut/bas>:§r Définit le bloc visé comme étant le sol.");
        sender.sendMessage("§6/" + prefix +
                " update <nom_ascenseur> bouton <haut/bas>:§r Définit le bloc visé comme étant le bouton d'appel.");
        sender.sendMessage("§6/" + prefix +
                " update <nom_ascenseur> sign <haut/bas>:§r Définit le bloc visé comme étant le panneau.");
        sender.sendMessage("§6/" + prefix +
                " update <nom_ascenseur> yaw <haut/bas> <rotation°>:§r Définit le bloc visé comme étant le panneau.");
        sender.sendMessage("§6/" + prefix + " update <nom_ascenseur> npc <NPC_ID>:§r Redéfini le groom.");
        sender.sendMessage("§6/" + prefix + " reload:§r Reload tout les ascenseur.");
    }
}
