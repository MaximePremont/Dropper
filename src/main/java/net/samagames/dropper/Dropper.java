package net.samagames.dropper;

import java.util.*;
import net.samagames.dropper.events.LevelQuitEvent;
import net.samagames.dropper.level.DropperLevel;
import net.samagames.dropper.level.LevelCooldown;
import net.samagames.tools.ProximityUtils;
import net.samagames.tools.Titles;
import net.samagames.tools.chat.ActionBarAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.google.gson.JsonObject;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.tools.LocationUtils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class Dropper extends Game<DropperPlayer> {
	
	private DropperMain instance;
	private List<DropperLevel> registeredLevels;
	
	public static final ItemStack ITEM_MODE_FREE = stackBuilder(ChatColor.GRAY + "Mode " + ChatColor.GREEN + "Entrainement", null, Material.BANNER, (byte) 2);
	public static final ItemStack ITEM_MODE_COMPETITION = stackBuilder(ChatColor.GRAY + "Mode " + ChatColor.RED + "Compétition", null, Material.BANNER, (byte) 1);
	public static final ItemStack ITEM_QUIT_GAME = stackBuilder(ChatColor.WHITE + "Quitter le mode de jeu", null, Material.BIRCH_DOOR_ITEM, (byte) 0);
	public static final ItemStack ITEM_QUIT_LEVEL = stackBuilder(ChatColor.RED + "Quitter le niveau", null, Material.BARRIER, (byte) 0);;
	public static final ItemStack ITEM_SELECTGUI = stackBuilder(ChatColor.WHITE + "Sélectionner un niveau", null, Material.BOOK, (byte) 0);
	
	 public Dropper(String gameCodeName, String gameName, String gameDescription, Class<DropperPlayer> gamePlayerClass, DropperMain instance) {
		 super(gameCodeName, gameName, gameDescription, gamePlayerClass);
		 
		 this.instance = instance;

		 // Registering levels
		 this.registeredLevels = new ArrayList<>();
		 this.registeredLevels.add(new DropperLevel(1, "Rainbow", "n/a"));
		 this.registeredLevels.add(new DropperLevel(2, "Isengard", "n/a"));
		 this.registeredLevels.add(new DropperLevel(3, "Neo", "n/a"));
		 this.registeredLevels.add(new DropperLevel(4, "Symbols", "n/a"));
		 this.registeredLevels.add(new DropperLevel(5, "The Three", "n/a"));
		 this.registeredLevels.add(new DropperLevel(6, "Embryo", "n/a"));
		 this.registeredLevels.add(new DropperLevel(7, "Brain", "n/a"));
		 this.registeredLevels.add(new DropperLevel(8, "Dimension Jumper", "n/a"));
		 this.registeredLevels.add(new DropperLevel(9, "BeetleJuice", "n/a"));
		 this.registeredLevels.add(new DropperLevel(10, "Web", "n/a"));
		 this.registeredLevels.add(new DropperLevel(11, "Armor", "n/a"));
		 this.registeredLevels.add(new DropperLevel(12, "Dracula's Bedroom", "n/a"));
		 this.registeredLevels.add(new DropperLevel(13, "DNA", "n/a"));
		 this.registeredLevels.add(new DropperLevel(14, "Minecraft is huge", "n/a"));
		 this.registeredLevels.add(new DropperLevel(15, "Hardware", "n/a"));
		 this.registeredLevels.add(new DropperLevel(16, "Moria", "n/a"));
		 

		 // Start proximity tasks
		 BukkitScheduler bukkitScheduler = this.instance.getServer().getScheduler();		 
		 for(DropperLevel level : this.getRegisteredLevels()){
			 ProximityUtils.onNearbyOf(this.instance, level.getSecretEnd(), 1.0D, 1.0D, 1.0D, Player.class, player -> bukkitScheduler.runTask(this.instance,
			() -> this.usualLevelLeave(player)));
		 }

	 }
	 
	 @Override 
	 public void handleLogin(Player player){
		 super.handleLogin(player);
		 player.teleport(this.getMapHub());
		 player.getInventory().clear();
		 player.getInventory().setItem(3, this.ITEM_MODE_FREE);
		 player.getInventory().setItem(5, this.ITEM_MODE_COMPETITION);
		 player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 2));
	 }

	 public DropperMain getInstance(){
	 	return this.instance;
	 }

	 public List<DropperLevel> getRegisteredLevels(){
	 	return this.registeredLevels;
	 }

	public Location getMapHub(){
		JsonObject object = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs();
		return LocationUtils.str2loc(object.get("world-name").getAsString() + ", " + object.get("map-hub").getAsString());
	}

	 public DropperLevel getDropperLevel(int ref){
	 	return this.registeredLevels.get(ref);
	 }
	 
	 public void usualGameTypeUpdate(Player player, GameType newGameType){

		 this.getPlayer(player.getUniqueId()).updatePlayerGameType(newGameType);
		 player.getInventory().clear();

		 if(! newGameType.equals(GameType.UNSELECTED)){
			 SamaGamesAPI.get().getGameManager().getCoherenceMachine().getMessageManager()
					 .writeCustomMessage("" + ChatColor.BLUE + ChatColor.BOLD + player.getName() + ChatColor.RESET + " joues désormais en mode " + this.getGameTypeFormatColor(newGameType),true);
		 }

		 if(newGameType.equals(GameType.FREE)){
			 player.getInventory().setItem(3, this.ITEM_SELECTGUI);
			 player.getInventory().setItem(5, this.ITEM_QUIT_GAME);

		 } else if(newGameType.equals(GameType.COMPETITION)){
			 player.getInventory().clear();
			 player.getInventory().setItem(0,this.ITEM_QUIT_GAME);

		 }

	 }

	 public void usualLevelJoin(Player player, int levelRef) {
		 DropperPlayer dpPlayer = this.getPlayer(player.getUniqueId());
		 DropperLevel level = this.getDropperLevel(levelRef);

         player.getInventory().clear();
         player.getInventory().setItem(4, this.ITEM_QUIT_LEVEL);
         dpPlayer.updateCurrentLevel(level);

         if(! dpPlayer.hasActiveCooldown()){
			 new LevelCooldown(this, player, level).runTaskTimer(this.instance, 0L, 20L);
		 }

	 }

	 public void usualLevelLeave(Player player){
		 DropperPlayer dpPlayer = this.getPlayer(player.getUniqueId());
		 DropperLevel level = dpPlayer.getCurrentLevel();

		 if(dpPlayer.hasActiveCooldown()){
		 	dpPlayer.getActiveCooldown().cancel();
		 	dpPlayer.resetCooldownData();
		 	ActionBarAPI.sendMessage(player.getUniqueId(), ChatColor.DARK_RED + "Démarrage du niveau annulé !");
		 }

		 LevelQuitEvent levelQuitEvent = new LevelQuitEvent(player, level);
		 this.getInstance().getServer().getPluginManager().callEvent(levelQuitEvent);

		 SamaGamesAPI.get().getGameManager().getCoherenceMachine().getMessageManager()
		.writeCustomMessage("" + ChatColor.BLUE + ChatColor.BOLD + player.getName() + ChatColor.RESET + " a terminé le niveau " + ChatColor.RED + ChatColor.BOLD + "#" + level.getID() +  ChatColor.RED + "(" + ChatColor.ITALIC + level.getName() + ")" + ChatColor.RESET + " en mode " + this.getGameTypeFormatColor(dpPlayer.getGameType()),true);

	 }

	 public String getGameTypeFormatColor(GameType type){
	 	if(type.equals(GameType.UNSELECTED)){
	 		return ChatColor.GRAY + "Non sélectionné";
		} else if(type.equals(GameType.FREE)){
	 		return ChatColor.GREEN + "Entrainement";
		} else if(type.equals(GameType.COMPETITION)){
			return ChatColor.RED + "Compétition";
		}
		return "";
	 }
	 
	 public void usualGameLeave(Player player){
		 DropperPlayer dpPlayer = this.getPlayer(player.getUniqueId());

		 if(dpPlayer.hasActiveCooldown()){
			 dpPlayer.getActiveCooldown().cancel();
			 dpPlayer.resetCooldownData();
			 ActionBarAPI.sendMessage(player.getUniqueId(), ChatColor.RED + "Démarrage du niveau annulé !");
		 }

		 SamaGamesAPI.get().getGameManager().getCoherenceMachine().getMessageManager()
		.writeCustomMessage("" + ChatColor.BLUE + ChatColor.BOLD + player.getName() + ChatColor.RESET + " a quitté la partie en mode " + this.getGameTypeFormatColor(dpPlayer.getGameType()),true);

		 if(dpPlayer.getCurrentLevel() != null){
			 player.teleport(this.getMapHub());
		 }

		 dpPlayer.updatePlayerGameType(GameType.UNSELECTED);
		 dpPlayer.updateCurrentLevel(null);
		 player.getInventory().clear();
		 player.getInventory().setItem(3, this.ITEM_MODE_FREE);
		 player.getInventory().setItem(5, this.ITEM_MODE_COMPETITION);
		 
	 }

	 public DropperLevel getNextFromCurrent(DropperLevel current){
	 	int id = current.getID();
	 	if(id < this.registeredLevels.size() + 1){
			id++;
		}
		 return this.registeredLevels.get(id);
	 }
	 
	 private static ItemStack stackBuilder(String name, List<String> lore, Material material, byte data){
	        org.bukkit.inventory.ItemStack tmpStack = new ItemStack(material, 1, data); 
	        ItemMeta tmpStackMeta = tmpStack.getItemMeta(); 
	        tmpStackMeta.setDisplayName(name); 
	        tmpStackMeta.setLore(lore); 
	        tmpStack.setItemMeta(tmpStackMeta); 
	 
	        return tmpStack; 
	    }
	
}
