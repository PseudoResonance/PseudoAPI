package io.github.wolfleader116.wolfapi.bukkit;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import io.github.wolfleader116.wolfapi.bukkit.commands.AllPluginsC;
import io.github.wolfleader116.wolfapi.bukkit.commands.PluginsC;
import io.github.wolfleader116.wolfapi.bukkit.commands.ReloadSC;
import io.github.wolfleader116.wolfapi.bukkit.commands.ResetSC;
import io.github.wolfleader116.wolfapi.bukkit.tabcompleters.WolfAPITC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class WolfAPI extends WolfPlugin implements Listener {

	public static WolfPlugin plugin;
	public static Message message;
	public static PluginChannelListener pcl;
	
	public static MainCommand mainCommand;
	public static HelpSC helpSubCommand;
	
	public static ChatColor border;
	public static ChatColor title;
	public static ChatColor command;
	public static ChatColor description;
	public static ChatColor text;
	public static ChatColor prefix;
	public static ChatColor error;
	public static ChatColor errorPrefix;
	
	public static Action clickEvent;
	
	public void onEnable() {
		this.saveDefaultConfig();
		if (this.getConfig().getInt("Version") != 1) {
			File conf = new File(this.getDataFolder(), "config.yml");
			conf.renameTo(new File("config.yml.old"));
			this.saveDefaultConfig();
			this.reloadConfig();
		}
		plugin = this;
		message = new Message(this);
		mainCommand = new MainCommand(plugin);
		helpSubCommand = new HelpSC(plugin);
		initializeCommands();
		initializeTabcompleters();
		initializeSubCommands();
		initializeListeners();
		setCommandDescriptions();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getVariables();
	}

	private void initializeCommands() {
		this.getCommand("wolfapi").setExecutor(mainCommand);
		this.getCommand("plugins").setExecutor(new PluginsC());
		this.getCommand("allplugins").setExecutor(new AllPluginsC());
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reset", new ResetSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("wolfapi").setTabCompleter(new WolfAPITC());
	}
	
	private void initializeListeners() {
		if (this.getConfig().getBoolean("BungeeEnabled")) {
		    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		    this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeSpigot", pcl = new PluginChannelListener());
		}
	}

	private void setCommandDescriptions() {
		this.commandDescriptions.add(new CommandDescription("wolfapi", "Shows WolfAPI information", ""));
		this.commandDescriptions.add(new CommandDescription("wolfapi help", "Shows WolfAPI commands", ""));
		this.commandDescriptions.add(new CommandDescription("wolfapi reload", "Reloads WolfAPI config", ""));
		this.commandDescriptions.add(new CommandDescription("wolfapi reset", "Resets WolfAPI config", ""));
		this.commandDescriptions.add(new CommandDescription("plugins", "Shows plugins", "wolfapi.plugins"));
		this.commandDescriptions.add(new CommandDescription("allplugins", "Shows all plugins", "wolfapi.allplugins"));
	}
	
	private void getVariables() {
		if (this.getConfig().getString("BorderColor").length() == 1) {
			border = ChatColor.getByChar(this.getConfig().getString("BorderColor").charAt(0));
		} else {
			border = Enum.valueOf(ChatColor.class, this.getConfig().getString("BorderColor"));
		}
		if (this.getConfig().getString("TitleColor").length() == 1) {
			title = ChatColor.getByChar(this.getConfig().getString("TitleColor").charAt(0));
		} else {
			title = Enum.valueOf(ChatColor.class, this.getConfig().getString("TitleColor"));
		}
		if (this.getConfig().getString("CommandColor").length() == 1) {
			command = ChatColor.getByChar(this.getConfig().getString("CommandColor").charAt(0));
		} else {
			command = Enum.valueOf(ChatColor.class, this.getConfig().getString("CommandColor"));
		}
		if (this.getConfig().getString("DescriptionColor").length() == 1) {
			description = ChatColor.getByChar(this.getConfig().getString("DescriptionColor").charAt(0));
		} else {
			description = Enum.valueOf(ChatColor.class, this.getConfig().getString("DescriptionColor"));
		}
		if (this.getConfig().getString("TextColor").length() == 1) {
			text = ChatColor.getByChar(this.getConfig().getString("TextColor").charAt(0));
		} else {
			text = Enum.valueOf(ChatColor.class, this.getConfig().getString("TextColor"));
		}
		if (this.getConfig().getString("PrefixColor").length() == 1) {
			prefix = ChatColor.getByChar(this.getConfig().getString("PrefixColor").charAt(0));
		} else {
			prefix = Enum.valueOf(ChatColor.class, this.getConfig().getString("PrefixColor"));
		}
		if (this.getConfig().getString("ErrorColor").length() == 1) {
			error = ChatColor.getByChar(this.getConfig().getString("ErrorColor").charAt(0));
		} else {
			error = Enum.valueOf(ChatColor.class, this.getConfig().getString("ErrorColor"));
		}
		if (this.getConfig().getString("ErrorPrefixColor").length() == 1) {
			errorPrefix = ChatColor.getByChar(this.getConfig().getString("ErrorPrefixColor").charAt(0));
		} else {
			errorPrefix = Enum.valueOf(ChatColor.class, this.getConfig().getString("ErrorPrefixColor"));
		}
		if (!(this.getConfig().getString("ClickEvent").equalsIgnoreCase("run")) && !(this.getConfig().getString("ClickEvent").equalsIgnoreCase("suggest"))) {
			clickEvent = Action.RUN_COMMAND;
			Message.sendConsoleMessage("Invalid ClickEvent in Config! Please choose run or suggest!");
		} else if (this.getConfig().getString("ClickEvent").equalsIgnoreCase("run")) {
			clickEvent = Action.RUN_COMMAND;
		} else if (this.getConfig().getString("ClickEvent").equalsIgnoreCase("suggest")) {
			clickEvent = Action.SUGGEST_COMMAND;
		}
	}

}