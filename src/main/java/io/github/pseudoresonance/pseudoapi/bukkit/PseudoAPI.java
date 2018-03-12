package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import io.github.pseudoresonance.pseudoapi.bukkit.commands.AllPluginsC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.PluginsC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.ReloadSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.ResetSC;
import io.github.pseudoresonance.pseudoapi.bukkit.events.CommandPreprocessEH;
import io.github.pseudoresonance.pseudoapi.bukkit.events.PlayerJoinEH;
import io.github.pseudoresonance.pseudoapi.bukkit.messaging.PluginMessenger;
import io.github.pseudoresonance.pseudoapi.bukkit.tabcompleters.PseudoAPITC;

public class PseudoAPI extends PseudoPlugin implements Listener {

	public static PseudoAPI plugin;
	public static Message message;
	public static PluginChannelListener pcl;

	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	private static PluginsC pluginsCommand;
	private static AllPluginsC allPluginsCommand;

	private static ConfigOptions configOptions;

	private static List<ConfigOption> config = new ArrayList<ConfigOption>();

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
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		PluginMessenger.enable();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			PlayerJoinEH.playerJoin(p);
		}
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		super.onDisable();
	}

	public static String getPlayerName(String uuid) {
		return DataController.getName(uuid);
	}

	public static String getPlayerUUID(String name) {
		return DataController.getUUID(name);
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
		this.getCommand("allplugins").setExecutor(allPluginsCommand);
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reset", new ResetSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("pseudoapi").setTabCompleter(new PseudoAPITC());
	}

	private void initializeListeners() {
		if (this.getConfig().getBoolean("BungeeEnabled")) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeSpigot", pcl = new PluginChannelListener());
		}
		getServer().getPluginManager().registerEvents(new CommandPreprocessEH(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinEH(), this);
	}

	private void setCommandDescriptions() {
		this.commandDescriptions.add(new CommandDescription("pseudoapi", "Shows PseudoAPI information", ""));
		this.commandDescriptions.add(new CommandDescription("pseudoapi help", "Shows PseudoAPI commands", ""));
		this.commandDescriptions.add(new CommandDescription("pseudoapi reload", "Reloads PseudoAPI config", "pseudoapi.reload"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi reset", "Resets PseudoAPI config", "pseudoapi.reset"));
		this.commandDescriptions.add(new CommandDescription("plugins", "Shows plugins", "pseudoapi.plugins"));
		this.commandDescriptions.add(new CommandDescription("allplugins", "Shows all plugins", "pseudoapi.allplugins"));
	}

	public static PluginsC getPluginsCommand() {
		return pluginsCommand;
	}

}