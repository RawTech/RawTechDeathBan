package me.rawtech.deathban.instances;

import me.rawtech.deathban.RawTechDeathBan;
import me.rawtech.global.instances.PlayerInstanceBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerInstance extends PlayerInstanceBase {
	private Boolean vulnerable = Boolean.valueOf(false);
	private int combatTime = 0;

	public PlayerInstance(Player player) {
		init(player);
		getUserSettings();
	}

	public Boolean isVulnerable() {
		return this.vulnerable;
	}

	public void setVulnerable(Boolean temp) {
		this.vulnerable = temp;
	}

	public int getCombatTime() {
		return this.combatTime;
	}

	public void setCombatTime(int time) {
		if ((this.combatTime == 0) && (!getPlayer().isDead())) {
			sendMessage("-------------- " + ChatColor.DARK_RED + "Unsafe to logout" + ChatColor.WHITE + " --------------");
		}
		this.combatTime = time;
	}

	public void remove() {
		long timeOnline = System.currentTimeMillis() / 1000L - getLoginTime();
		RawTechDeathBan.DatabaseAPI.removePlayer(getPlayer().getName(), Long.valueOf(timeOnline));
		try {
			finalize();
		} catch (Throwable e) {
		}
	}
}