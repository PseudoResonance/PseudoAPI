package io.github.wolfleader116.wolfapi.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import io.github.wolfleader116.wolfapi.bukkit.commands.AllPluginsC;
import io.github.wolfleader116.wolfapi.bukkit.commands.PluginsC;
import io.github.wolfleader116.wolfapi.bukkit.commands.ReloadSC;
import io.github.wolfleader116.wolfapi.bukkit.commands.ResetSC;
import io.github.wolfleader116.wolfapi.bukkit.tabcompleters.WolfAPITC;

public class WolfAPI extends WolfPlugin implements Listener {

	public static WolfPlugin plugin;
	public static Message message;
	public static PluginChannelListener pcl;
	
	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	private static PluginsC pluginsCommand;
	private static AllPluginsC allPluginsCommand;
	
	@Override
	public void onEnable() {
		super.onEnable();
		this.saveDefaultConfig();
		plugin = this;
		ConfigOptions.updateConfig();
		message = new Message(this);
		mainCommand = new MainCommand(plugin);
		helpSubCommand = new HelpSC(plugin);
		pluginsCommand = new PluginsC();
		allPluginsCommand = new AllPluginsC();
		initializeCommands();
		initializeTabcompleters();
		initializeSubCommands();
		initializeListeners();
		setCommandDescriptions();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		ConfigOptions.reloadConfig();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}

	private void initializeCommands() {
		this.getCommand("wolfapi").setExecutor(mainCommand);
		this.getCommand("plugins").setExecutor(pluginsCommand);
		this.getCommand("allplugins").setExecutor(allPluginsCommand);
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

}