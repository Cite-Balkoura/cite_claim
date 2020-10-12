package fr.milekat.cite_claim.commands;

import fr.milekat.cite_claim.utils.RegionsBlocksLoad;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceReloadRegions implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        new RegionsBlocksLoad().reloadRegions();
        sender.sendMessage("Reload effectué.");
        return true;
    }
}
