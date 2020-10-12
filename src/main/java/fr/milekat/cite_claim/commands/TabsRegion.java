package fr.milekat.cite_claim.commands;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_core.MainCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabsRegion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            return MainCore.getTabArgs(args[0], new ArrayList<>(Arrays.asList("add", "update", "tool", "reload")));
        } else {
            if (args[0].equalsIgnoreCase("update")) {
                if (args.length <= 2) {
                    return MainCore.getTabArgs(args[1], new ArrayList<>(Arrays.asList("prix", "claim", "quartier", "sign")));
                } else if (args.length <= 3 &&
                        (args[1].equalsIgnoreCase("prix") ||
                        args[1].equalsIgnoreCase("claim") ||
                        args[1].equalsIgnoreCase("quartier") ||
                        args[1].equalsIgnoreCase("sign"))) {
                    return MainCore.getTabArgs(args[2], new ArrayList<>(MainClaim.regions.keySet()));
                } else if (args.length <= 4 && args[1].equalsIgnoreCase("quartier")) {
                    return MainCore.getTabArgs(args[3], new ArrayList<>(Arrays.asList("Plateau", "Montagne", "Favélas")));
                }
            } else if (args[0].equalsIgnoreCase("tool")) {
                return MainCore.getTabArgs(args[2], new ArrayList<>(MainClaim.regions.keySet()));
            }
        }
        return null;
    }
}
