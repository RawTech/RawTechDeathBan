package me.rawtech.deathban;

import me.rawtech.deathban.instances.PlayerInstance;
import me.rawtech.deathban.tasks.TaskCombatTimer;
import me.rawtech.global.RawTechNetDB;
import me.rawtech.global.tasks.TaskTipDeployer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RawTechDeathBan extends JavaPlugin {
	public static Plugin plugin;
	public static RawTechNetDB DatabaseAPI;
	public static Map<String, PlayerInstance> players = new HashMap<String, PlayerInstance>();
	public static Map<String, Integer> loginWait = new HashMap<String, Integer>();
	private BukkitTask tipDeployerTask;
	private BukkitTask combatTimeTask;

	public static ArrayList<PlayerInstance> getAllPlayers() {
		ArrayList<PlayerInstance> groupedplayers = new ArrayList<PlayerInstance>();
		for(PlayerInstance pi : players.values()) {
			groupedplayers.add(pi);
		}
		return groupedplayers;
	}

	public static PlayerInstance getPlayer(Player player) {
		return players.get(player.getName());
	}

	public static PlayerInstance getPlayer(String player) {
		return players.get(player);
	}

	public void onEnable() {
		DatabaseAPI = new RawTechNetDB(this);
		ChatHandler.init();
		plugin = this;
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		getServer().getPluginManager().registerEvents(new VulnerabilityListener(), this);

		ArrayList<String> tips = new ArrayList<String>();

		tips.add("Other players cannot see your name tag.");
		tips.add("Extra lives can be bought from the store: " + ChatColor.DARK_PURPLE + "rawt.co.uk/store");
		tips.add("If you die, you'll be banned from this server for 5 days. (3 days for gold)");

		this.tipDeployerTask = new TaskTipDeployer(tips).runTaskTimer(plugin, 1200L, 6000L);
		this.combatTimeTask = new TaskCombatTimer().runTaskTimer(plugin, 20L, 20L);
	}

	public void onDisable() {
		DatabaseAPI.offlineAll("DB");
		this.tipDeployerTask.cancel();
		this.combatTimeTask.cancel();
	}
}