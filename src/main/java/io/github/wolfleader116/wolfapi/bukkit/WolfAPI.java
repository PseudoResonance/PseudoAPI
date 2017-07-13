package io.github.wolfleader116.wolfapi.bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.xxmicloxx.NoteBlockAPI.SongPlayer;

import io.github.wolfleader116.wolfapi.bukkit.commands.AllPluginsC;
import io.github.wolfleader116.wolfapi.bukkit.commands.PluginsC;
import io.github.wolfleader116.wolfapi.bukkit.commands.ReloadSC;
import io.github.wolfleader116.wolfapi.bukkit.commands.ResetSC;
import io.github.wolfleader116.wolfapi.bukkit.events.CommandPreprocessEH;
import io.github.wolfleader116.wolfapi.bukkit.events.PlayerJoinEH;
import io.github.wolfleader116.wolfapi.bukkit.messaging.PluginMessenger;
import io.github.wolfleader116.wolfapi.bukkit.tabcompleters.WolfAPITC;

public class WolfAPI extends WolfPlugin implements Listener {

	public static WolfAPI plugin;
	public static Message message;
	public static PluginChannelListener pcl;

	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	private static PluginsC pluginsCommand;
	private static AllPluginsC allPluginsCommand;

	private static ConfigOptions configOptions;

	private static List<ConfigOption> config = new ArrayList<ConfigOption>();

	public Map<String, ArrayList<SongPlayer>> playingSongs = Collections.synchronizedMap(new HashMap<String, ArrayList<SongPlayer>>());
	public Map<String, Byte> playerVolume = Collections.synchronizedMap(new HashMap<String, Byte>());

	private boolean disabling = false;

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
		disabling = true;
		Bukkit.getScheduler().cancelTasks(this);
		super.onDisable();
	}

	public static boolean isReceivingSong(Player p) {
		return ((plugin.playingSongs.get(p.getName()) != null) && (!plugin.playingSongs.get(p.getName()).isEmpty()));
	}

	public static void stopPlaying(Player p) {
		if (plugin.playingSongs.get(p.getName()) == null) {
			return;
		}
		for (SongPlayer s : plugin.playingSongs.get(p.getName())) {
			s.removePlayer(p);
		}
	}

	public static void setPlayerVolume(Player p, byte volume) {
		plugin.playerVolume.put(p.getName(), volume);
	}

	public static byte getPlayerVolume(Player p) {
		Byte b = plugin.playerVolume.get(p.getName());
		if (b == null) {
			b = 100;
			plugin.playerVolume.put(p.getName(), b);
		}
		return b;
	}

	public static int getCompatibility() {
		if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.7")) {
			return NoteBlockCompatibility.pre1_9;
		} else if (Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")) {
			return NoteBlockCompatibility.pre1_12;
		} else {
			return NoteBlockCompatibility.post1_12;
		}
	}

	public void doSync(Runnable r) {
		getServer().getScheduler().runTask(this, r);
	}

	public void doAsync(Runnable r) {
		getServer().getScheduler().runTaskAsynchronously(this, r);
	}

	public boolean isDisabling() {
		return disabling;
	}

	public class NoteBlockCompatibility {
		public static final int pre1_9 = 0;
		public static final int pre1_12 = 1;
		public static final int post1_12 = 2;
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
		return WolfAPI.configOptions;
	}

	private void initializeCommands() {
		this.getCommand("wolfapi").setExecutor(mainCommand);
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
		this.getCommand("wolfapi").setTabCompleter(new WolfAPITC());
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
		this.commandDescriptions.add(new CommandDescription("wolfapi", "Shows WolfAPI information", ""));
		this.commandDescriptions.add(new CommandDescription("wolfapi help", "Shows WolfAPI commands", ""));
		this.commandDescriptions.add(new CommandDescription("wolfapi reload", "Reloads WolfAPI config", "wolfapi.reload"));
		this.commandDescriptions.add(new CommandDescription("wolfapi reset", "Resets WolfAPI config", "wolfapi.reset"));
		this.commandDescriptions.add(new CommandDescription("plugins", "Shows plugins", "wolfapi.plugins"));
		this.commandDescriptions.add(new CommandDescription("allplugins", "Shows all plugins", "wolfapi.allplugins"));
	}

	public static PluginsC getPluginsCommand() {
		return pluginsCommand;
	}

}