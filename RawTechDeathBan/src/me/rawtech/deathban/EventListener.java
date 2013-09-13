package me.rawtech.deathban;

import me.rawtech.deathban.instances.PlayerInstance;
import me.rawtech.deathban.tasks.TaskDatabaseJoin;
import me.rawtech.deathban.tasks.TaskRemovePlayerProfile;
import me.rawtech.deathban.tasks.TaskSendMessage;
import me.rawtech.deathban.tasks.TaskSetVulnerable;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class EventListener
		implements Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();

		if (RawTechDeathBan.loginWait.containsKey(player.getName())) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Please wait a bit before connecting again.");
			return;
		}

		Boolean staff = false;
		Boolean premium = false;

		ResultSet r = RawTechDeathBan.DatabaseAPI.getUserRow(player.getName());
		try {
			while (r.next()) {
				if (r.getInt("premium") == 1) {
					premium = true;
				}
				if (r.getInt("rank") > 0)
					staff = true;
			}
		} catch (SQLException e) {
		}
		int currUnixTime = (int) (System.currentTimeMillis() / 1000L);
		int rows = RawTechDeathBan.DatabaseAPI.getNumRows("SELECT * FROM `deathBans` WHERE `unixTime`+`expire` > " + currUnixTime + " AND `username` = '" + player.getName() + "' ;");
		if (rows != 0) {
			ResultSet banrow = RawTechDeathBan.DatabaseAPI.getResultSet("SELECT * FROM `deathBans` WHERE `username` = '" + player.getName() + "' ORDER BY `unixTime` DESC LIMIT 1;");
			int expirein = 0;
			try {
				while (banrow.next())
					expirein = banrow.getInt("unixTime") + banrow.getInt("expire") - currUnixTime;
			} catch (SQLException e) {
			}
			int days = expirein / 86400;
			int hours = expirein / 3600 - days * 24;
			int mins = expirein / 60 - days * 24 * 60 - hours * 60;
			int secs = expirein - days * 24 * 60 * 60 - hours * 60 * 60 - mins * 60;
			String message = "You are death banned, expires in";

			if (days == 1)
				message = message + " 1 day,";
			else if (days > 1) {
				message = message + " " + days + " days,";
			}

			if (hours == 1)
				message = message + " 1 hour,";
			else if (hours > 1) {
				message = message + " " + hours + " hours,";
			}

			if (mins == 1)
				message = message + " 1 min,";
			else if (mins > 1) {
				message = message + " " + mins + " mins,";
			}

			if (secs == 1)
				message = message + " 1 sec";
			else if (secs > 1) {
				message = message + " " + secs + " secs";
			}

			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + message + ".");
		} else if (player.isBanned()) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You have been banned from this server.");
		} else if ((Bukkit.getOnlinePlayers().length >= 30) && !staff && !premium) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.WHITE + "The server is full. " + ChatColor.GOLD + "Get a reserved slot by going gold! \n http://rawt.co.uk/gold");
		}

		RawTechDeathBan.loginWait.put(player.getName(), (int) (System.currentTimeMillis() / 1000L) + 60);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		ChatHandler.sendChatMessage(player, message);
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		PlayerInstance pi = new PlayerInstance(p);
		RawTechDeathBan.players.put(p.getName(), pi);

		new TaskDatabaseJoin(p.getName()).runTaskLater(RawTechDeathBan.plugin, 20L);
		event.setJoinMessage(null);

		if (!p.hasPlayedBefore()) {
			Boolean hasSpawned = false;
			while (!hasSpawned) {
				Location spawn = genNewSpawnpoint(p.getWorld());
				Boolean safeSpawn = true;
				for (Player other : Bukkit.getOnlinePlayers()) {
					if ((!other.getName().equalsIgnoreCase(p.getName())) && (other.getLocation().distance(p.getLocation()) <= 200.0D)) {
						safeSpawn = false;
					}
					int type = p.getWorld().getBlockTypeIdAt(spawn);
					if ((type == 9) || (type == 10) || (type == 11) || (type == 8)) {
						safeSpawn = false;
					}
				}
				if (safeSpawn) {
					p.teleport(spawn);
					hasSpawned = true;
				}
			}
			givePlayerStartBook(p);
		}

		p.sendMessage("You have 10 seconds of invulnerability.");
		new TaskSendMessage(p, "You have 5 seconds of invulnerability.").runTaskLater(RawTechDeathBan.plugin, 100L);
		new TaskSendMessage(p, "You have 4 seconds of invulnerability.").runTaskLater(RawTechDeathBan.plugin, 120L);
		new TaskSendMessage(p, "You have 3 seconds of invulnerability.").runTaskLater(RawTechDeathBan.plugin, 140L);
		new TaskSendMessage(p, "You have 2 seconds of invulnerability.").runTaskLater(RawTechDeathBan.plugin, 160L);
		new TaskSendMessage(p, "You have 1 second of invulnerability.").runTaskLater(RawTechDeathBan.plugin, 180L);
		new TaskSendMessage(p, "You are now vulnerable to damage!").runTaskLater(RawTechDeathBan.plugin, 200L);
		new TaskSetVulnerable(pi, true).runTaskLater(RawTechDeathBan.plugin, 200L);
	}

	@EventHandler
	public void onNameTag(PlayerReceiveNameTagEvent event) {
		event.setTag(ChatColor.DARK_GRAY + ".");
	}

	private void givePlayerStartBook(Player player) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();

		meta.setTitle("Game information");
		meta.setAuthor("RawTech");
		ArrayList<String> pages = new ArrayList<String>();

		pages.add("Season 1\nWelcome to the Hardcore Deathban server! As the name implies, when you die you will be banned from this server for 5 days (3 days for Gold).");
		pages.add("Each month will be a new season, meaning a new map and all profiles reset.\nThis season will end on Friday 6th September.");
		pages.add("Season 1 Rules:\n- No enderpearl damage\n- Nether and end disabled\n- No teaming\n- Greifing is allowed\n- Stealing is allowed\n- No natural health regen\n- Can't see nametags");
		pages.add("You are invulnerable for 10 seconds after you login. (you can still die from fire, lava, suffocation & drowning)\nYou can get more lives from the store! rawt.co.uk/store");

		meta.setPages(pages);
		book.setItemMeta(meta);
		player.getInventory().addItem(book);
	}

	private Location genNewSpawnpoint(World world) {
		Random rand = new Random();
		Location spawn = new Location(world, rand.nextInt(3000) - 1500, 0.0D, rand.nextInt(3000) - 1500);
		spawn.setY(world.getHighestBlockYAt(spawn));
		return spawn;
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getDamager().getType().equals(EntityType.ENDER_PEARL))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		p.getWorld().strikeLightningEffect(p.getLocation());
		e.getDrops().add(playerSkullForName(p.getName()));
		e.setDeathMessage(ChatColor.RED + e.getDeathMessage());

		String killerName = "";

		if (p.getKiller() != null) {
			Bukkit.broadcastMessage("");
			killerName = p.getKiller().getName();
			RawTechDeathBan.DatabaseAPI.executeStatement("UPDATE  `RawTechNetwork`.`users` SET  `db-kills` =  `db-kills` + 1 , `kills-total` =  `kills-total` + 1 WHERE  `users`.`username` = '" + killerName + "';");
		} else {
			EntityDamageEvent ede = e.getEntity().getLastDamageCause();
			if ((ede instanceof EntityDamageByEntityEvent)) {
				EntityDamageByEntityEvent edee = (EntityDamageByEntityEvent) ede;
				killerName = edee.getDamager().getType().toString().toLowerCase().replaceAll("_", " ");
			}
		}

		int expire = 432000;

		if (RawTechDeathBan.getPlayer(p).isGold()) {
			expire = 259200;
		}

		int unixTime = (int) (System.currentTimeMillis() / 1000L);

		RawTechDeathBan.DatabaseAPI.executeStatement("UPDATE  `RawTechNetwork`.`users` SET  `db-deaths` =  `db-deaths` + 1 , `deaths-total` =  `deaths-total` + 1 WHERE  `users`.`username` = '" + p.getName() + "';");
		RawTechDeathBan.DatabaseAPI.executeStatement("INSERT INTO `RawTechNetwork`.`deathBans` (`username`, `killer`, `unixTime`, `expire`) VALUES ('" + p.getName() + "', '" + killerName + "', '" + unixTime + "', '" + expire + "');");
	}

	private ItemStack playerSkullForName(String name) {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1);
		is.setDurability((short) 3);
		SkullMeta meta = (SkullMeta) is.getItemMeta();
		meta.setOwner(name);
		is.setItemMeta(meta);
		return is;
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		String duration = "5 days";
		if (RawTechDeathBan.getPlayer(p).isGold()) {
			duration = "3 days";
		}
		p.kickPlayer("You have been death banned! It will expire in " + duration + ".");
		new TaskRemovePlayerProfile(p.getName()).runTaskLater(RawTechDeathBan.plugin, 20L);
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		Player p = e.getPlayer();
		PlayerInstance pi = RawTechDeathBan.getPlayer(p);

		if (pi.getCombatTime() != 0) {
			pi.setCombatTime(0);
			int expire = 432000;

			if (RawTechDeathBan.getPlayer(p).isGold()) {
				expire = 259200;
			}

			int unixTime = (int) (System.currentTimeMillis() / 1000L);
			RawTechDeathBan.DatabaseAPI.executeStatement("UPDATE  `RawTechNetwork`.`users` SET  `db-deaths` =  `db-deaths` + 1 , `deaths-total` =  `deaths-total` + 1 WHERE  `users`.`username` = '" + p.getName() + "';");
			RawTechDeathBan.DatabaseAPI.executeStatement("INSERT INTO `RawTechNetwork`.`deathBans` (`username`, `killer`, `unixTime`, `expire`) VALUES ('" + p.getName() + "', 'Combat Log', '" + unixTime + "', '" + expire + "');");
			new TaskRemovePlayerProfile(p.getName()).runTaskLater(RawTechDeathBan.plugin, 20L);
		}

		pi.remove();
		e.setLeaveMessage(null);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		PlayerInstance pi = RawTechDeathBan.getPlayer(p);

		if (pi.getCombatTime() != 0) {
			pi.setCombatTime(0);
			int expire = 432000;

			if (RawTechDeathBan.getPlayer(p).isGold()) {
				expire = 259200;
			}

			int unixTime = (int) (System.currentTimeMillis() / 1000L);
			RawTechDeathBan.DatabaseAPI.executeStatement("UPDATE  `RawTechNetwork`.`users` SET  `db-deaths` =  `db-deaths` + 1 , `deaths-total` =  `deaths-total` + 1 WHERE  `users`.`username` = '" + p.getName() + "';");
			RawTechDeathBan.DatabaseAPI.executeStatement("INSERT INTO `RawTechNetwork`.`deathBans` (`username`, `killer`, `unixTime`, `expire`) VALUES ('" + p.getName() + "', 'Combat Log', '" + unixTime + "', '" + expire + "');");
			new TaskRemovePlayerProfile(p.getName()).runTaskLater(RawTechDeathBan.plugin, 20L);
		}

		pi.remove();
		e.setQuitMessage(null);
	}
}