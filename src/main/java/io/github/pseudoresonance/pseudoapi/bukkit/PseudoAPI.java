package io.github.pseudoresonance.pseudoapi.bukkit;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import io.github.pseudoresonance.pseudoapi.bukkit.commands.AllPluginsC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.BackendSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.BrandSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.MetricsSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.PluginsC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.ReloadSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.ResetSC;
import io.github.pseudoresonance.pseudoapi.bukkit.listeners.ClientBrandL;
import io.github.pseudoresonance.pseudoapi.bukkit.listeners.CommandPreprocessL;
import io.github.pseudoresonance.pseudoapi.bukkit.listeners.PlayerJoinLeaveL;
import io.github.pseudoresonance.pseudoapi.bukkit.messaging.PluginMessenger;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudoapi.bukkit.tabcompleters.PseudoAPITC;

public class PseudoAPI extends PseudoPlugin implements Listener {

	public static PseudoAPI plugin;
	public static Message message;
	public static PluginChannelListener pcl;

	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	private static PluginsC pluginsCommand;
	private static AllPluginsC allPluginsCommand;
	private static MetricsSC metricsSubCommand;
	private static BrandSC brandSubCommand;

	private static ConfigOptions configOptions;

	private static List<ConfigOption> config = new ArrayList<ConfigOption>();

	@Override
	public void onEnable() {
		super.onEnable();
		this.saveDefaultConfig();
		plugin = this;
		Utils.startTps();
		configOptions = new ConfigOptions();
		ConfigOptions.updateConfig();
		message = new Message(this);
		mainCommand = new MainCommand(plugin);
		helpSubCommand = new HelpSC(plugin);
		pluginsCommand = new PluginsC();
		allPluginsCommand = new AllPluginsC();
		metricsSubCommand = new MetricsSC();
		brandSubCommand = new BrandSC();
		initializeCommands();
		initializeTabcompleters();
		initializeSubCommands();
		initializeListeners();
		setCommandDescriptions();
		configOptions.reloadConfig();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		PluginMessenger.enable();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			PlayerJoinLeaveL.playerJoin(p);
		}
		PlayerDataController.update();
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		super.onDisable();
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
		this.getCommand("metrics").setExecutor(metricsSubCommand);
		this.getCommand("brand").setExecutor(brandSubCommand);
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reset", new ResetSC());
		subCommands.put("metrics", metricsSubCommand);
		subCommands.put("brand", brandSubCommand);
		subCommands.put("backend", new BackendSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("pseudoapi").setTabCompleter(new PseudoAPITC());
	}

	private void initializeListeners() {
		if (this.getConfig().getBoolean("BungeeEnabled")) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeSpigot", pcl = new PluginChannelListener());
		}
		getServer().getPluginManager().registerEvents(new CommandPreprocessL(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinLeaveL(), this);
		this.getServer().getMessenger().registerIncomingPluginChannel(PseudoAPI.plugin, "MC|Brand", new ClientBrandL());
	}

	private void setCommandDescriptions() {
		this.commandDescriptions.add(new CommandDescription("pseudoapi", "Shows PseudoAPI information", ""));
		this.commandDescriptions.add(new CommandDescription("pseudoapi help", "Shows PseudoAPI commands", ""));
		this.commandDescriptions.add(new CommandDescription("pseudoapi reload", "Reloads PseudoAPI config", "pseudoapi.reload"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi reset", "Resets PseudoAPI config", "pseudoapi.reset"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi metrics", "Shows server metrics", "pseudoapi.metrics"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi brand <player>", "Shows user client brand", "pseudoapi.brand", false));
		this.commandDescriptions.add(new CommandDescription("pseudoapi backend list", "Lists all backends", "pseudoapi.backend"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi backend migrate <from> <to>", "Migrates from one backend to another", "pseudoapi.backend", false));
		this.commandDescriptions.add(new CommandDescription("plugins", "Shows plugins", "pseudoapi.plugins"));
		this.commandDescriptions.add(new CommandDescription("allplugins", "Shows all plugins", "pseudoapi.allplugins"));
	}

	public static PluginsC getPluginsCommand() {
		return pluginsCommand;
	}

}