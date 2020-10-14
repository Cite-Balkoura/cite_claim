package fr.milekat.cite_claim.ascenseur.commands;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_claim.ascenseur.obj.Ascenseur;
import fr.milekat.cite_core.MainCore;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AscenseurCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) sender.sendMessage("Commende pour un joueur seulement.");
        Player player = (Player) sender;
        if (args.length < 1) sendHelp(sender, label);
        else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2]));
            if (npc!=null) MainClaim.ascenseurHashMap.put(args[1], new Ascenseur(
                    args[1], npc,
                    null, null,
                    0,0,
                    null, null,
                    null, null));
            else sender.sendMessage(MainCore.prefixCmd + "§cNPC non trouvé.");
        } else if (args.length >= 4 && args[0].equalsIgnoreCase("update")) {
            if (MainClaim.ascenseurHashMap.containsKey(args[1])) {
                Ascenseur ascenseur = MainClaim.ascenseurHashMap.get(args[1]);
                Block block = player.getTargetBlockExact(10);
                if (block!=null && args[2].equalsIgnoreCase("floor")) {
                    if (args[3].equalsIgnoreCase("haut")) ascenseur.setFloorDownLoc(block.getLocation());
                    else if (args[3].equalsIgnoreCase("bas")) ascenseur.setFloorUpLoc(block.getLocation());
                } else if (block!=null && args[2].equalsIgnoreCase("bouton")) {
                    if (args[3].equalsIgnoreCase("haut")) ascenseur.setBoutonUp(block.getLocation());
                    else if (args[3].equalsIgnoreCase("bas")) ascenseur.setBoutonDown(block.getLocation());
                } else if (block!=null && args[2].equalsIgnoreCase("sign")) {
                    if (block.getState() instanceof Sign) {
                        if (args[3].equalsIgnoreCase("haut")) ascenseur.setSignUp((Sign) block.getState());
                        else if (args[3].equalsIgnoreCase("bas")) ascenseur.setSignDown((Sign) block.getState());
                    } else sender.sendMessage(MainCore.prefixCmd + "§cMerci de regarder un panneau");
                } else if (args.length == 5 && block!=null && args[2].equalsIgnoreCase("yaw")) {
                    if (args[3].equalsIgnoreCase("haut")) ascenseur.setYawUp(Integer.parseInt(args[4]));
                    else if (args[3].equalsIgnoreCase("bas")) ascenseur.setYawDown(Integer.parseInt(args[4]));
                } else if (block!=null && args[2].equalsIgnoreCase("npc")) {
                    NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3]));
                    if (npc!=null) ascenseur.setNpc(npc);
                }
            } else sender.sendMessage(MainCore.prefixCmd + "§cAscenseur non trouvé.");
        } else if (args[0].equalsIgnoreCase("reload")) {
            sendHelp(sender, label);
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
