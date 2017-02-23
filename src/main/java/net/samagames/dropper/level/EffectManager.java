package net.samagames.dropper.level;

import net.samagames.dropper.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Vialonyx
 */

public class EffectManager {

    private Dropper game;
    public EffectManager(Dropper game){
        this.game = game;
    }

    public void restoreDefaultEffects(Player player){
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 2));
    }

    public void addEffectsForLevel(Player player, DropperLevel level){

        if(level.getID() == 8){
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 4));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 600, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));
        } else if (level.getID() == 12){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9600, 4));
        } else if(level.getID() == 13) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 4000, 4));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6100, 4));
        } else if(level.getID() == 14){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000, 3));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10000, 0));
        } else if(level.getID() == 16){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
        }

    }

}