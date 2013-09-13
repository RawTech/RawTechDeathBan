package me.rawtech.deathban;

import me.rawtech.deathban.instances.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatHandler {
	public static Boolean chatMute = Boolean.valueOf(false);
	private static Map<String, String> chatprefix = new HashMap();
	private static Map<String, String> chatsuffix = new HashMap();

	public static void init() {
		chatprefix.put("default", ChatColor.GRAY + "[" + ChatColor.WHITE);
		chatprefix.put("gold", ChatColor.BLACK + "[" + ChatColor.GOLD);
		chatprefix.put("chatmod", ChatColor.GRAY + "[" + ChatColor.GREEN);
		chatprefix.put("mod", ChatColor.GRAY + "[" + ChatColor.DARK_GREEN);
		chatprefix.put("admin", ChatColor.GOLD + "[" + ChatColor.DARK_RED);

		chatsuffix.put("default", ChatColor.GRAY + "] " + ChatColor.WHITE);
		chatsuffix.put("gold", ChatColor.BLACK + "] " + ChatColor.WHITE);
		chatsuffix.put("chatmod", ChatColor.GRAY + "] " + ChatColor.GREEN);
		chatsuffix.put("mod", ChatColor.GRAY + "] " + ChatColor.DARK_GREEN);
		chatsuffix.put("admin", ChatColor.GOLD + "] " + ChatColor.DARK_RED);
	}

	public static String getName(Player player) {
		PlayerInstance pi = RawTechDeathBan.getPlayer(player.getName());

		if (pi.getRank() == 3)
			return (String) chatprefix.get("admin") + player.getDisplayName() + (String) chatsuffix.get("admin");
		if (pi.getRank() == 2)
			return (String) chatprefix.get("mod") + player.getDisplayName() + (String) chatsuffix.get("mod");
		if (pi.getRank() == 1)
			return (String) chatprefix.get("chatmod") + player.getDisplayName() + (String) chatsuffix.get("chatmod");
		if (pi.isGold().booleanValue()) {
			return (String) chatprefix.get("gold") + player.getDisplayName() + (String) chatsuffix.get("gold");
		}

		return (String) chatprefix.get("default") + player.getDisplayName() + (String) chatsuffix.get("default");
	}

	public static void sendChatMessage(Player player, String message) {
		PlayerInstance pi = RawTechDeathBan.getPlayer(player.getName());
		if (!pi.canChat().booleanValue()) {
			player.sendMessage("Your chat slowmode is: " + ChatColor.RED + pi.getSlowMode() + ChatColor.WHITE + " seconds.");
			player.sendMessage("You can change this in your settings: " + ChatColor.RED + "http://rawtechnet.co.uk/settings");
			return;
		}

		if (pi.getRank() > 0) {
			for (PlayerInstance p : RawTechDeathBan.getAllPlayers()) {
				p.getPlayer().sendMessage(getName(player) + message);
			}
			pi.chat();

			return;
		}
		if (chatMute.booleanValue()) {
			return;
		}

		for (PlayerInstance p : RawTechDeathBan.getAllPlayers()) {
			p.getPlayer().sendMessage(getName(player) + message);
		}
		Bukkit.getLogger().info(getName(player) + message);
		pi.chat();
	}

	private static String processChat(String message) {
		String str = message.toLowerCase();
		str = str.replace(" im ", " I'm ");
		str = str.replace("brb", "Be right back");
		str = str.replace("guise", "guys");
		str = str.replace("cya", "See ya");
		str = str.replace("c'ya", "See ya");
		str = str.replace("idk", "I don't know");
		str = str.replace("msg", "message");
		str = str.replace("pls", "please");
		str = str.replace("plz", "please");
		str = str.replace(" ppl", " people");
		str = str.replace("sry", "sorry");
		str = str.replace("wher ", "where ");
		str = str.replace("sup", "what's up");
		str = str.replace("yea", "yeah");
		str = str.replace("whos", "who's");
		str = str.replace("srs", "serious");
		str = str.replace("srsly", "seriously");
		str = str.replace("wtf", "what the frick");
		str = str.replace("wats ", "what's ");
		str = str.replace("whats ", "what's ");
		str = str.replace("doe ", "though ");
		str = str.replace("dat ", "that ");
		str = str.replace("thx ", "thanks ");
		str = str.replace("u2", "you too ");
		str = str.replace("chris", "cris");
		str = str.replace("rawtech", "RawTech");
		str = str.replace("fuck", "frick");
		str = str.replace("penis", "I'm Immature.");
		str = str.replace("vagina", "I'm Immature.");
		str = str.replace("scetch.net", "no.net");
		str = str.replace("dvz.dwarfscraft.com", "hub.rawtechnet.co.uk");
		str = str.replace("dwarfscraft.com", "rawtechnet.co.uk");
		str = capitalise(str);
		str = closeSentences(str);
		return str;
	}

	private static String capitalise(String message) {
		String[] sentences = message.split("(?<=[!?\\.])\\s");
		String tempMessage = "";
		for (String sentence : sentences) {
			List words = Arrays.asList(message.split("\\s"));
			String firstWord = (String) words.get(0);

			if (!isLink(firstWord)) {
				String firstChar = Character.toString(sentence.charAt(0));

				firstChar = firstChar.toUpperCase();
				sentence = sentence.substring(1);
				sentence = firstChar + sentence;
			}
			tempMessage = tempMessage + sentence + " ";
		}
		return tempMessage.trim();
	}

	private static String closeSentences(String message) {
		List exemptions = Arrays.asList(new String[]{".", "!", "?", "(", ")", "[", "]", "{", "}", "<", ">", "D", "3", "P", "\\", "/", ";", ":", ",", "-", "_", "d", "p", "'"});
		String lastChar = Character.toString(message.charAt(message.length() - 1));
		boolean exempt = false;
		if (exemptions.contains(lastChar)) {
			exempt = true;
		}

		List words = Arrays.asList(message.split("\\s"));
		String lastWord = (String) words.get(words.size() - 1);

		if ((!exempt) && (!isLink(lastWord)) && (!isNumber(lastWord))) {
			message = message + ".";
		}

		return message;
	}

	private static boolean isLink(String word) {
		if (word.matches("(https?://)?(\\w+(\\.|/))+(\\w+(/|$))")) {
			return true;
		}
		return false;
	}

	private static boolean isNumber(String word) {
		if (word.matches("[0-9\\.]+")) {
			return true;
		}
		return false;
	}

	private static String processChatNew(String message) {
		String str = message.toLowerCase();

		str = str.replace("im", "I'm");
		str = str.replace("brb", "Be right back");
		str = str.replace("guise", "guys");
		str = str.replace("cya", "See ya");
		str = str.replace("c'ya", "See ya");
		str = str.replace("idk", "I don't know");
		str = str.replace("msg", "message");
		str = str.replace("pls", "please");
		str = str.replace("plz", "please");
		str = str.replace("ppl", "people");
		str = str.replace("sry", "sorry");
		str = str.replace("wher", "where");
		str = str.replace("sup", "what's up");
		str = str.replace("yea", "yeah");
		str = str.replace("whos", "who's");
		str = str.replace("srs", "serious");
		str = str.replace("srsly", "seriously");
		str = str.replace("wtf", "what the fuck");
		str = str.replace("wats", "what's");
		str = str.replace("whats", "what's");
		str = str.replace("doe", "though");
		str = str.replace("dat", "that");
		str = str.replace("thx", "thanks");
		str = str.replace("u2", "you too");
		str = str.replace("chris", "cris");
		str = str.replace("rawtech", "RawTech");
		str = str.replace("fuck", "frick");
		str = str.replace("penis", "I'm Immature.");
		str = str.replace("vagina", "I'm Immature.");
		str = str.replace("wtf", "Blummin' eck!");
		str = str.replace("scetch.net", "no.net");
		str = str.replace("dvz.dwarfscraft.com", "hub.rawtechnet.co.uk");
		str = str.replace("dwarfscraft.com", "rawtechnet.co.uk");

		str = wordSINGLE(str, "wat", "what");
		str = wordSINGLE(str, "y", "why");
		str = wordSINGLE(str, "r", "are");
		str = wordSINGLE(str, "np", "no problem");
		str = wordSINGLE(str, "ty", "thank you");
		str = wordSINGLE(str, "u", "you");

		str = removeDOUBLE(str, ".");
		str = removeDOUBLE(str, ",");
		str = removeDOUBLE(str, "!");
		str = removeDOUBLE(str, "?");
		str = removeDOUBLE(str, "!?");
		str = removeDOUBLE(str, "'");
		str = removeDOUBLE(str, "-");
		str = removeDOUBLE(str, " ");
		str = removeDOUBLE(str, ", ");
		str = removeDOUBLE(str, "z");

		str = addCOMMA(str);

		str = str.replace("why you", "why are you");
		str = str.replace("when you", "when are you");
		str = str.replace("where you", "where are you");
		str = str.replace("what you", "what do you");
		str = str.replace("when you", "when do you");

		str = str.replace("why he", "why are he");
		str = str.replace("when he", "when are he");
		str = str.replace("where he", "where are he");
		str = str.replace("what he", "what does he");
		str = str.replace("when he", "when does he");

		str = str.replace("why she", "why are she");
		str = str.replace("when she", "when are she");
		str = str.replace("where she", "where are she");
		str = str.replace("what she", "what does she");
		str = str.replace("when she", "when dies she");

		str = str.replace("why we", "why are we");
		str = str.replace("when we", "when are we");
		str = str.replace("where we", "where are we");
		str = str.replace("what we", "what do we");
		str = str.replace("when we", "when do we");

		str = str.replace("why they", "why are they");
		str = str.replace("when they", "when are we");
		str = str.replace("where they", "where are we");
		str = str.replace("what they", "what do we");
		str = str.replace("when we", "when do we");

		str = str.replace("?", ".");

		str = spaceAFTER(str, ",");
		str = spaceAFTER(str, "!");
		str = spaceAFTER(str, ".");
		str = spaceAFTER(str, "-");
		str = spaceAFTER(str, "?");

		str = str.replace(" ,", ",");
		str = str.replace(" .", ".");
		str = str.replace(" !", "!");
		str = str.replace(" ?", "?");
		if (str.toUpperCase().equals(str)) {
			return str;
		}
		String[] sentences = str.split("\\.");
		String STR = "";
		for (int i = 0; i < sentences.length; i++)
			try {
				String s = sentences[i];
				if (s.startsWith(", ")) {
					s = s.substring(2);
				}
				if (s.endsWith(".")) {
					s = s.substring(0, s.length() - 1);
				}
				if (s.endsWith("?")) {
					s = s.substring(0, s.length() - 1);
				}
				if (s.endsWith(" ")) {
					s = s.substring(0, s.length() - 1);
				}
				if (!isQuestion(s).booleanValue())
					STR = STR + s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + ".";
				else
					STR = STR + s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + "?";
			} catch (Exception localException) {
			}
		STR = STR.replace("i'm", "I'm");
		return STR.replace(" i ", " I ");
	}

	public static String spaceAFTER(String string, String chr) {
		String str = string;
		str = str.replace(chr + " ", chr);
		str = str.replace(chr, chr + " ");
		return str;
	}

	public static String addCOMMA(String tmp) {
		String string = tmp;
		if (string.startsWith(", ")) {
			string = string.substring(2);
		}
		String str = "";
		String s = string.substring(0, string.indexOf(" "));
		String s2 = string.substring(string.indexOf(" ") + 1);
		s = s.toLowerCase();
		s2 = s2.toLowerCase();
		if (s.equals("yeah")) {
			s = s + ",";
		}
		if (s.substring(s.length() - 2).equals("ly")) {
			s = s + ",";
		}
		if (s.equals("often")) {
			s = s + ",";
		}
		str = s + " " + s2;
		return str;
	}

	public static Boolean isQuestion(String string) {
		Boolean b = Boolean.valueOf(false);
		String s = string;
		if (s.contains(",")) {
			s = s.substring(string.indexOf(", ") + 2);
		}
		if (s.contains(" ")) {
			s = s.substring(0, s.indexOf(" "));
		}
		if (s.contains("'")) {
			s = s.substring(0, s.indexOf("'"));
		}
		s = s.toLowerCase();
		s = s.replace(" ", "");
		if (s.equals("what")) {
			b = Boolean.valueOf(true);
		}
		if (s.equals("how")) {
			b = Boolean.valueOf(true);
		}
		if (s.equals("why")) {
			b = Boolean.valueOf(true);
		}
		if (s.equals("where")) {
			b = Boolean.valueOf(true);
		}
		if (s.equals("when")) {
			b = Boolean.valueOf(true);
		}
		if (s.equals("who")) {
			b = Boolean.valueOf(true);
		}
		return b;
	}

	public static String wordSINGLE(String string, String word, String filter) {
		if (!string.contains(" ")) {
			return string;
		}
		String str = "";
		String[] array = string.toLowerCase().split("\\ ");
		int i = 0;
		for (i = 0; i < array.length; i++) {
			String s = array[i];
			if (s.length() > 2) {
				if (commaFORMAT(s).equals("who")) {
					s = ", " + s;
				}
				if (commaFORMAT(s).equals("what")) {
					s = ", " + s;
				}
				if (commaFORMAT(s).equals("where")) {
					s = ", " + s;
				}
				if (commaFORMAT(s).equals("why")) {
					s = ", " + s;
				}
				if (commaFORMAT(s).equals("how")) {
					s = ", " + s;
				}
			}
			if (s.equals(word))
				str = str + s.replace(word, filter);
			else {
				str = str + s;
			}
			str = str + " ";
		}
		return str.substring(0, str.length());
	}

	public static String commaFORMAT(String string) {
		if (string.contains("'")) {
			return string.substring(0, string.indexOf("'"));
		}
		return string;
	}

	public static String removeDOUBLE(String string, String dbl) {
		String str = string;
		do
			str = str.replace(dbl + dbl, dbl);
		while (str.contains(dbl + dbl));
		return str;
	}
}