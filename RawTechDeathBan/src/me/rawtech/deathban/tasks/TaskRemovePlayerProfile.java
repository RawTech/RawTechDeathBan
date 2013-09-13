package me.rawtech.deathban.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class TaskRemovePlayerProfile extends BukkitRunnable {
	private String name;

	public TaskRemovePlayerProfile(String name) {
		this.name = name;
	}

	public void run() {
		try {
			File file = new File("./world/players/" + this.name + ".dat");
			file.delete();
		} catch (Exception e) {
		}
	}
}