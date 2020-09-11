package fr.milekat.cite_claim.engines;

import fr.milekat.cite_claim.MainClaim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SpeedBoost {
    public BukkitTask runTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.SPEED);
                    if (potionEffect == null || potionEffect.getAmplifier() != 1 || potionEffect.getDuration() < 201) {
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.SPEED,219,1, false, false, false));
                    }
                }
            }
        }.runTaskTimer(MainClaim.getInstance(),0L,200L);
    }
}
