package fr.milekat.cite_claim.commands;

import fr.milekat.cite_claim.MainClaim;
import fr.milekat.cite_core.MainCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class TabsRegion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            return MainCore.getTabArgs(args[0],"add", "claim", "sign", "tool");
        } else {
            if (args[0].equalsIgnoreCase("claim") || args[0].equalsIgnoreCase("sign") ||
                    args[0].equalsIgnoreCase("tool")) {
                return MainCore.getTabArgs(args[1], MainClaim.regions.keySet().toArray(new String[0]));
            }
        }
        return null;
    }
}
