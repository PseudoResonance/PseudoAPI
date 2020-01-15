package io.github.pseudoresonance.pseudoapi.bungee;

import java.util.concurrent.TimeUnit;

import javax.tools.ToolProvider;

import org.bstats.bungeecord.Metrics;

import io.github.pseudoresonance.pseudoapi.bukkit.messaging.PluginMessenger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;

public class PseudoAPI extends Plugin {
	
	protected static PseudoAPI plugin;
	
	private static Config config;
	
	private static Metrics metrics = null;
	
	@Override
	public void onEnable() {
		ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("Loading PseudoAPI Bungee Link").color(ChatColor.GREEN).create());
		plugin = this;
		config = new Config(this);
		config.updateConfig();
		config.reloadConfig();
		BungeeLanguageManager.copyDefaultPluginLanguageFiles(false);
		registerCommands();
		initializeListeners();
		PlayerDataController.update();
		plugin.getFile();
		getProxy().getScheduler().schedule(this, new Runnable() {
			public void run() {
				PseudoUpdater.checkUpdates(true);
			}
		}, Config.startupDelay, TimeUnit.SECONDS);
		initializeMetrics();
	}
	
	public static Metrics getMetrics() {
		return metrics;
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

	private void registerCommands() {
		getProxy().getPluginManager().registerCommand(this, new PseudoAPIC());
	}

	private void initializeListeners() {
		getProxy().getPluginManager().registerListener(this, new PlayerL());
		getProxy().getPluginManager().registerListener(this, new PluginMessageL());
		getProxy().registerChannel(PluginMessenger.channelBungeeName);
	}
	
	protected static Config getConfig() {
		return config;
	}

}
