
package me.rawtech.deathban.tasks;

import me.rawtech.deathban.instances.PlayerInstance;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskSetVulnerable extends BukkitRunnable {
	private PlayerInstance pi;
	private Boolean vulnerable;

	public TaskSetVulnerable(PlayerInstance pi, Boolean vulnerable) {
		this.pi = pi;
		this.vulnerable = vulnerable;
	}

	public void run() {
		this.pi.setVulnerable(this.vulnerable);
	}
}
