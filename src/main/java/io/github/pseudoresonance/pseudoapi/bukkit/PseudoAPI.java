package io.github.pseudoresonance.pseudoapi.bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.AllPluginsC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.BackendSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.PluginsC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.ReloadSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.ResetSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.UpdateSC;
import io.github.pseudoresonance.pseudoapi.bukkit.listeners.CommandL;
import io.github.pseudoresonance.pseudoapi.bukkit.listeners.PlayerJoinLeaveL;
import io.github.pseudoresonance.pseudoapi.bukkit.messaging.PluginMessenger;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudoapi.bukkit.tabcompleters.PseudoAPITC;

public class PseudoAPI extends PseudoPlugin {

	public static PseudoAPI plugin;
	public static Message message;
	public static PluginChannelListener pcl;

	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	private static PluginsC pluginsCommand;
	private static AllPluginsC allPluginsCommand;

	private static ConfigOptions configOptions;

	private static List<ConfigOption> config = new ArrayList<ConfigOption>();
	
	public void onLoad() {
		PseudoUpdater.registerPlugin(this);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		this.saveDefaultConfig();
		plugin = this;
		configOptions = new ConfigOptions();
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
		configOptions.reloadConfig();
		PluginMessenger.enable();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			PlayerJoinLeaveL.playerJoin(p);
		}
		PlayerDataController.update();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				PseudoUpdater.checkUpdates(true);
			}
			
		}, ConfigOptions.startupDelay * 20);
	}

	public void onDisable() {
		super.onDisable();
		for (File f : PseudoUpdater.getOldFiles()) {
			PseudoAPI.message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Please delete " + f.getName() + " before starting the server again to prevent duplicate plugins!");
		}
		Bukkit.getScheduler().cancelTasks(this);
	}

	public static void registerConfig(ConfigOption r) {
		config.add(r);
	}

	public static void updateAll() {
		for (ConfigOption r : config) {
			r.reloadConfig();
		}
	}

	public static ConfigOptions getConfigOptions() {
		return PseudoAPI.configOptions;
	}

	private void initializeCommands() {
		this.getCommand("pseudoapi").setExecutor(mainCommand);
		this.getCommand("plugins").setExecutor(pluginsCommand);
		this.getCommand("pl").setExecutor(pluginsCommand);
		this.registerDynamicCommand("plugins", pluginsCommand);
		this.registerDynamicCommand("pl", pluginsCommand);
		this.getCommand("allplugins").setExecutor(allPluginsCommand);
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reset", new ResetSC());
		subCommands.put("backend", new BackendSC());
		subCommands.put("update", new UpdateSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("pseudoapi").setTabCompleter(new PseudoAPITC());
	}

	private void initializeListeners() {
		if (this.getConfig().getBoolean("BungeeEnabled")) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeSpigot", pcl = new PluginChannelListener());
		}
		getServer().getPluginManager().registerEvents(new CommandL(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinLeaveL(), this);
	}

	private void setCommandDescriptions() {
		this.commandDescriptions.add(new CommandDescription("pseudoapi", "Shows PseudoAPI information", ""));
		this.commandDescriptions.add(new CommandDescription("pseudoapi help", "Shows PseudoAPI commands", ""));
		this.commandDescriptions.add(new CommandDescription("pseudoapi reload", "Reloads PseudoAPI config", "pseudoapi.reload"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi reset", "Resets PseudoAPI config", "pseudoapi.reset"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi backend list", "Lists all backends", "pseudoapi.backend"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi backend migrate <from> <to>", "Migrates from one backend to another", "pseudoapi.backend", false));
		this.commandDescriptions.add(new CommandDescription("pseudoapi update", "Updates all PseudoAPI plugins", "pseudoapi.update"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi update <plugin>", "Updates specified PseudoAPI plugin", "pseudoapi.update", false));
		this.commandDescriptions.add(new CommandDescription("plugins", "Shows plugins", "pseudoapi.plugins"));
		this.commandDescriptions.add(new CommandDescription("allplugins", "Shows all plugins", "pseudoapi.allplugins"));
	}

	public static PluginsC getPluginsCommand() {
		return pluginsCommand;
	}

}