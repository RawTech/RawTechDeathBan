package me.rawtech.deathban.tasks;

import me.rawtech.deathban.RawTechDeathBan;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskDatabaseJoin extends BukkitRunnable {
	private String name;

	public TaskDatabaseJoin(String name) {
		this.name = name;
	}

	public void run() {
		RawTechDeathBan.DatabaseAPI.playerJoin(this.name, "DB");
	}
}