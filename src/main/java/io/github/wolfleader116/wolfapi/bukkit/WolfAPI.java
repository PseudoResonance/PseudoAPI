package io.github.wolfleader116.wolfapi.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.xxmicloxx.noteblockapi.SongPlayer;

import io.github.wolfleader116.wolfapi.bukkit.commands.AllPluginsC;
import io.github.wolfleader116.wolfapi.bukkit.commands.PluginsC;
import io.github.wolfleader116.wolfapi.bukkit.commands.ReloadSC;
import io.github.wolfleader116.wolfapi.bukkit.commands.ResetSC;
import io.github.wolfleader116.wolfapi.bukkit.events.CommandPreprocessEH;
import io.github.wolfleader116.wolfapi.bukkit.messaging.PluginMessenger;
import io.github.wolfleader116.wolfapi.bukkit.tabcompleters.WolfAPITC;

public class WolfAPI extends WolfPlugin implements Listener {

	public static WolfPlugin plugin;
	public static Message message;
	public static PluginChannelListener pcl;
	
	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	private static PluginsC pluginsCommand;
	private static AllPluginsC allPluginsCommand;
	
	private static ConfigOptions configOptions;
	
    public static HashMap<String, ArrayList<SongPlayer>> playingSongs = new HashMap<String, ArrayList<SongPlayer>>();
    public static HashMap<String, Byte> playerVolume = new HashMap<String, Byte>();
    
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
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
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
		return WolfAPI.configOptions;
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
		getServer().getPluginManager().registerEvents(new CommandPreprocessEH(), this);
	}

	private void setCommandDescriptions() {
		this.commandDescriptions.add(new CommandDescription("wolfapi", "Shows WolfAPI information", ""));
		this.commandDescriptions.add(new CommandDescription("wolfapi help", "Shows WolfAPI commands", ""));
		this.commandDescriptions.add(new CommandDescription("wolfapi reload", "Reloads WolfAPI config", "wolfapi.reload"));
		this.commandDescriptions.add(new CommandDescription("wolfapi reset", "Resets WolfAPI config", "wolfapi.reset"));
		this.commandDescriptions.add(new CommandDescription("plugins", "Shows plugins", "wolfapi.plugins"));
		this.commandDescriptions.add(new CommandDescription("allplugins", "Shows all plugins", "wolfapi.allplugins"));
	}

    public static boolean isReceivingSong(Player p) {
        return ((playingSongs.get(p.getName()) != null) && (!playingSongs.get(p.getName()).isEmpty()));
    }

    public static void stopPlaying(Player p) {
        if (playingSongs.get(p.getName()) == null) {
            return;
        }
        for (SongPlayer s : playingSongs.get(p.getName())) {
            s.removePlayer(p);
        }
    }

    public static void setPlayerVolume(Player p, byte volume) {
        playerVolume.put(p.getName(), volume);
    }

    public static byte getPlayerVolume(Player p) {
        Byte b = playerVolume.get(p.getName());
        if (b == null) {
            b = 100;
            playerVolume.put(p.getName(), b);
        }
        return b;
    }
    
    public static PluginsC getPluginsCommand() {
    	return pluginsCommand;
    }

}