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
            return MainCore.getTabArgs(args[0], new ArrayList<>(Arrays.asList("add", "claim", "sign", "tool")));
        } else {
            if (args[0].equalsIgnoreCase("claim") || args[0].equalsIgnoreCase("sign") ||
                    args[0].equalsIgnoreCase("tool")) {
                return MainCore.getTabArgs(args[1], new ArrayList<>(MainClaim.regions.keySet()));
            }
        }
        return null;
    }
}
