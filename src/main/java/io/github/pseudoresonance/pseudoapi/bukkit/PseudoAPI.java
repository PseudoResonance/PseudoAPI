package io.github.pseudoresonance.pseudoapi.bukkit;

import java.io.File;
import java.util.ArrayList;

import javax.tools.ToolProvider;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.AllPluginsC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.BackendSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.PluginsC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.ReloadLocalizationSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.ReloadSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.ResetLocalizationSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.ResetSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.SetLocalizationSC;
import io.github.pseudoresonance.pseudoapi.bukkit.commands.UpdateSC;
import io.github.pseudoresonance.pseudoapi.bukkit.data.PluginConfig;
import io.github.pseudoresonance.pseudoapi.bukkit.listeners.BlockL;
import io.github.pseudoresonance.pseudoapi.bukkit.listeners.CommandL;
import io.github.pseudoresonance.pseudoapi.bukkit.listeners.PlayerJoinLeaveL;
import io.github.pseudoresonance.pseudoapi.bukkit.messaging.PluginMessenger;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.ServerPlayerDataController;
import io.github.pseudoresonance.pseudoapi.bukkit.tabcompleters.PseudoAPITC;
import io.github.pseudoresonance.pseudoapi.bungee.BungeeLanguageManager;

public class PseudoAPI extends PseudoPlugin {

	public static PseudoAPI plugin;

	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	private static PluginsC pluginsCommand;
	private static AllPluginsC allPluginsCommand;

	private static Config pluginConfig;

	private static ArrayList<PluginConfig> config = new ArrayList<PluginConfig>();
	
	private static Metrics metrics = null;

	public void onLoad() {
		PseudoUpdater.registerPlugin(this);
	}

	@Override
	public void onEnable() {
		plugin = this;
		this.saveDefaultConfig();
		pluginConfig = new Config(this);
		pluginConfig.updateConfig();
		pluginConfig.reloadConfig();
		super.onEnable();
		mainCommand = new MainCommand(plugin);
		helpSubCommand = new HelpSC(plugin);
		pluginsCommand = new PluginsC();
		allPluginsCommand = new AllPluginsC();
		initializeCommands();
		initializeTabcompleters();
		initializeSubCommands();
		initializeListeners();
		setCommandDescriptions();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			PlayerJoinLeaveL.playerJoin(p);
		}
		PluginMessenger.enable();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				PseudoUpdater.checkUpdates(true);
			}
		}, Config.startupDelay * 20);
		initializeMetrics();
	}

	public void onDisable() {
		super.onDisable();
		for (File f : PseudoUpdater.getOldFiles()) {
			getChat().sendConsolePluginError(Errors.CUSTOM, BungeeLanguageManager.getLanguage().getMessage("pseudoapi.delete_before_restart", f.getName()));
		}
		Bukkit.getScheduler().cancelTasks(this);
	}
	
	private void initializeMetrics() {
		metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("default_language", () -> {
	        return Config.defaultLocale;
	    }));
		metrics.addCustomChart(new Metrics.SimplePie("java_type", () -> {
			return ToolProvider.getSystemJavaCompiler() == null ? "JRE" : "JDK";
	    }));
	}
	
	public static Metrics getMetrics() {
		return metrics;
	}
	
	public void doSync(Runnable run) {
		Bukkit.getScheduler().runTask(this, run);
	}
	
	public void doAsync(Runnable run) {
		Bukkit.getScheduler().runTaskAsynchronously(this, run);
	}

	public static void registerConfig(PluginConfig r) {
		config.add(r);
	}

	public static Config getPluginConfig() {
		return PseudoAPI.pluginConfig;
	}

	private void initializeCommands() {
		this.getCommand("pseudoapi").setExecutor(mainCommand);
		this.getCommand("plugins").setExecutor(pluginsCommand);
		this.getCommand("pl").setExecutor(pluginsCommand);
		this.registerCommandOverride("plugins", pluginsCommand);
		this.registerCommandOverride("pl", pluginsCommand);
		this.getCommand("allplugins").setExecutor(allPluginsCommand);
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reset", new ResetSC());
		subCommands.put("reloadlocalization", new ReloadLocalizationSC());
		subCommands.put("resetlocalization", new ResetLocalizationSC());
		subCommands.put("setlocalization", new SetLocalizationSC());
		subCommands.put("backend", new BackendSC());
		subCommands.put("update", new UpdateSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("pseudoapi").setTabCompleter(new PseudoAPITC());
	}

	private void initializeListeners() {
		getServer().getPluginManager().registerEvents(new CommandL(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinLeaveL(), this);
		getServer().getPluginManager().registerEvents(new BlockL(), this);
	}

	private void setCommandDescriptions() {
		this.commandDescriptions.add(new CommandDescription("pseudoapi", "pseudoapi.pseudoapi_help", ""));
		this.commandDescriptions.add(new CommandDescription("pseudoapi help", "pseudoapi.pseudoapi_help_help", ""));
		this.commandDescriptions.add(new CommandDescription("pseudoapi reload", "pseudoapi.pseudoapi_reload_help", "pseudoapi.reload"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi reloadlocalization", "pseudoapi.pseudoapi_reloadlocalization_help", "pseudoapi.reloadlocalization"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi reset", "pseudoapi.pseudoapi_reset_help", "pseudoapi.reset"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi resetlocalization", "pseudoapi.pseudoapi_resetlocalization_help", "pseudoapi.resetlocalization"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi setlocalization <locale> (player)", "pseudoapi.pseudoapi_setlocalization_help", "pseudoapi.setlocalization", false));
		this.commandDescriptions.add(new CommandDescription("pseudoapi backend list", "pseudoapi.pseudoapi_backend_list_help", "pseudoapi.backend"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi backend migrate <from> <to>", "pseudoapi.pseudoapi_backend_migrate_help", "pseudoapi.backend", false));
		this.commandDescriptions.add(new CommandDescription("pseudoapi update", "pseudoapi.pseudoapi_update_help", "pseudoapi.update"));
		this.commandDescriptions.add(new CommandDescription("pseudoapi update <plugin>", "pseudoapi.pseudoapi_update_plugin_help", "pseudoapi.update", false));
		this.commandDescriptions.add(new CommandDescription("plugins", "pseudoapi.plugins_help", "pseudoapi.plugins"));
		this.commandDescriptions.add(new CommandDescription("allplugins", "pseudoapi.allplugins_help", "pseudoapi.allplugins"));
	}

	public static PluginsC getPluginsCommand() {
		return pluginsCommand;
	}

}