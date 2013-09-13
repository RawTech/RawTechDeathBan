package me.rawtech.deathban.tasks;

import me.rawtech.deathban.RawTechDeathBan;
import me.rawtech.deathban.instances.PlayerInstance;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;

public class TaskCombatTimer extends BukkitRunnable {
	public void run() {
		for (PlayerInstance pi : RawTechDeathBan.getAllPlayers()) {
			if (pi.getPlayer().isDead()) {
				pi.setCombatTime(0);
			}
			int time = pi.getCombatTime();
			time--;
			if (time == 0) {
				pi.setCombatTime(0);
				pi.sendMessage("--------------- " + ChatColor.DARK_GREEN + "Safe to logout" + ChatColor.WHITE + " ---------------");
			} else if (time > 0) {
				pi.setCombatTime(time);
			}
		}

		Iterator it = RawTechDeathBan.loginWait.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (((Integer) entry.getValue()).intValue() < (int) (System.currentTimeMillis() / 1000L)) {
				RawTechDeathBan.loginWait.remove(entry.getKey());
			}
			it.remove();
		}
	}
}