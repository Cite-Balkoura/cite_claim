package fr.milekat.cite_claim.ascenseur.commands;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_core.MainCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AscenseurTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            return MainCore.getTabArgs(args[0], new ArrayList<>(Arrays.asList("add", "update", "save", "reload")));
        } else {
            if (args[0].equalsIgnoreCase("update")) {
                if (args.length <= 2) {
                    return MainCore.getTabArgs(args[1], new ArrayList<>(MainClaim.ascenseurs.keySet()));
                } else if (args.length <= 3) {
                    return MainCore.getTabArgs(args[2],
                            new ArrayList<>(Arrays.asList("floor", "bouton", "sign", "yaw", "door", "npc")));
                } else if (args.length <= 4 && (args[2].equalsIgnoreCase("floor") ||
                        args[2].equalsIgnoreCase("bouton") ||
                        args[2].equalsIgnoreCase("sign") ||
                        args[2].equalsIgnoreCase("yaw") ||
                        args[2].equalsIgnoreCase("door"))) {
                    return MainCore.getTabArgs(args[3], new ArrayList<>(Arrays.asList("haut", "bas")));
                }
            } else if (args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("reload")) {
                if (args.length <= 2) {
                    ArrayList<String> returnlist = new ArrayList<>(Collections.singletonList("all"));
                    returnlist.addAll(new ArrayList<>(MainClaim.ascenseurs.keySet()));
                    return MainCore.getTabArgs(args[1], returnlist);
                }
            }
        }
        return null;
    }
}
