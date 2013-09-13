package me.rawtech.deathban.tasks;

import me.rawtech.deathban.instances.PlayerInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskSendMessage extends BukkitRunnable {
	private Player player;
	private String message;

	public TaskSendMessage(PlayerInstance pi, String message) {
		this.player = pi.getPlayer();
		this.message = message;
	}

	public TaskSendMessage(Player player, String message) {
		this.player = player;
		this.message = message;
	}

	public void run() {
		this.player.sendMessage(this.message);
	}
}