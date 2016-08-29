package io.github.wolfleader116.wolfapi.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum ParticleEffect {
    EXPLOSION_NORMAL("explode"),
    EXPLOSION_LARGE("largeexplode"),
    EXPLOSION_HUGE("hugeexplosion"),
    FIREWORKS_SPARK("fireworksSpark"),
    WATER_BUBBLE("bubble"),
    WATER_SPLASH("splash"),
    WATER_WAKE("wake"),
    SUSPENDED("suspended"),
    SUSPENDED_DEPTH("depthsuspend"),
    CRIT("crit"),
    CRIT_MAGIC("magicCrit"),
    SMOKE_NORMAL("smoke"),
    SMOKE_LARGE("largesmoke"),
    SPELL("spell"),
    SPELL_INSTANT("instantSpell"),
    SPELL_MOB("mobSpell", true),
    SPELL_MOB_AMBIENT("mobSpellAmbient"),
    SPELL_WITCH("witchMagic"),
    DRIP_WATER("dripWater"),
    DRIP_LAVA("dripLava"),
    VILLAGER_ANGRY("angryVillager"),
    VILLAGER_HAPPY("happyVillager"),
    TOWN_AURA("townaura"),
    NOTE("note", true),
    PORTAL("portal"),
    ENCHANTMENT_TABLE("enchantmenttable"),
    FLAME("flame"),
    LAVA("lava"),
    FOOTSTEP("footstep"),
    CLOUD("cloud"),
    REDSTONE("reddust", true),
    SNOWBALL("snowballpoof"),
    SNOW_SHOVEL("snowshovel"),
    SLIME("slime"),
    HEART("heart"),
    BARRIER("barrier"),
    ITEM_CRACK("iconcrack_"),
    BLOCK_CRACK("blockcrack_"),
    BLOCK_DUST("blockdust_"),
    WATER_DROP("droplet"),
    ITEM_TAKE("take"),
    MOB_APPEARANCE("mobappearance"),
    DRAGON_BREATH("dragonbreath"),
    END_ROD("endRod"),
    DAMAGE_INDICATOR("damageIndicator"),
    SWEEP_ATTACK("sweepAttack");

	private String particleName;
	private String enumValue;
	private boolean hasColor;
	private static Class<?> nmsPacketPlayOutParticle;
	private static Class<?> nmsEnumParticle;
	private static int particleRange;
	private static Class<?> nmsPlayerConnection;
	private static Class<?> nmsEntityPlayer;
	private static Class<?> ioNettyChannel;
	private static Method nmsNetworkGetVersion;
	private static Field nmsFieldPlayerConnection;
	private static Field nmsFieldNetworkManager;
	private static Field nmsFieldNetworkManagerI;
	private static Field nmsFieldNetworkManagerM;

	private ParticleEffect(String particleName, boolean hasColor) {
		this.particleName = particleName;
		this.hasColor = hasColor;
		this.enumValue = this.name();
	}

	private ParticleEffect(String particleName) {
		this.particleName = particleName;
		this.hasColor = false;
		this.enumValue = this.name();
	}

	public String getName() {
		return this.particleName;
	}

	public static void setRange(int range) {
		if (range < 0) {
			throw new IllegalArgumentException("Range must be positive!");
		}
		if (range > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Range is too big!");
		}
		particleRange = range;
	}

	public static int getRange() {
		return particleRange;
	}

	private void sendToPlayer(Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count, int... extra) throws Exception {
		if (!isPlayerInRange(player, location)) {
			return;
		}
		if (ReflectionUtilities.getVersion().contains("v1_8")) {
			try {
				if (nmsEnumParticle == null) {
					nmsEnumParticle = ReflectionUtilities.getNMSClass("EnumParticle");
				}
				if (this == BLOCK_CRACK) {
					int id = 0;
					int data = 0;
					if (extra.length > 0) {
						id = extra[0];
					}
					if (extra.length > 1) {
						data = extra[1];
					}
					extra = new int[] { id, id | data << 12 };
				}
				Object packet = nmsPacketPlayOutParticle.getConstructor(new Class[] { nmsEnumParticle, Boolean.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE, int[].class }).newInstance(new Object[] { getEnum(nmsEnumParticle.getName() + "." + (this.enumValue != null ? this.enumValue : name().toUpperCase())), Boolean.valueOf(true), Float.valueOf((float) location.getX()), Float.valueOf((float) location.getY()), Float.valueOf((float) location.getZ()), Float.valueOf(offsetX), Float.valueOf(offsetY), Float.valueOf(offsetZ), Float.valueOf(speed), Integer.valueOf(count), extra });
				Object handle = ReflectionUtilities.getHandle(player);
				Object connection = ReflectionUtilities.getField(handle.getClass(), "playerConnection").get(handle);
				ReflectionUtilities.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, new Object[] { packet });
			} catch (Exception e) {
				throw e;
			}
		} else {
			try {
				if (this.particleName == null) {
					this.particleName = name().toLowerCase();
				}
				String name = this.particleName;
				if ((this == BLOCK_CRACK) || (this == ITEM_CRACK) || (this == BLOCK_DUST)) {
					int id = 0;
					int data = 0;
					if (extra.length > 0) {
						id = extra[0];
					}
					if (extra.length > 1) {
						data = extra[1];
					}
					name = name + id + "_" + data;
				}
				Object packet = nmsPacketPlayOutParticle.getConstructor(new Class[] { String.class, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE }).newInstance(new Object[] { name, Float.valueOf((float) location.getX()), Float.valueOf((float) location.getY()), Float.valueOf((float) location.getZ()), Float.valueOf(offsetX), Float.valueOf(offsetY), Float.valueOf(offsetZ), Float.valueOf(speed), Integer.valueOf(count) });
				Object handle = ReflectionUtilities.getHandle(player);
				Object connection = ReflectionUtilities.getField(handle.getClass(), "playerConnection").get(handle);
				ReflectionUtilities.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, new Object[] { packet });
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public void sendToPlayer(Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		sendToPlayer(player, location, offsetX, offsetY, offsetZ, speed, count, new int[0]);
	}

	public void sendToPlayers(Collection<? extends Player> players, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		for (Player p : players) {
			sendToPlayer(p, location, offsetX, offsetY, offsetZ, speed, count);
		}
	}

	public void sendToPlayers(Player[] players, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		Player[] arrayOfPlayer;
		int j = (arrayOfPlayer = players).length;
		for (int i = 0; i < j; i++) {
			Player p = arrayOfPlayer[i];
			sendToPlayer(p, location, offsetX, offsetY, offsetZ, speed, count);
		}
	}

	public void sendColor(Player p, Location location, org.bukkit.Color color) throws Exception {
		if (!this.hasColor) {
			return;
		}
		sendToPlayer(p, location, getColor(color.getRed()), getColor(color.getGreen()), getColor(color.getBlue()), 1.0F, 0);
	}

	public void sendColor(Player p, Location location, java.awt.Color color) throws Exception {
		if (!this.hasColor) {
			return;
		}
		sendToPlayer(p, location, getColor(color.getRed()), getColor(color.getGreen()), getColor(color.getBlue()), 1.0F, 0);
	}

	public void sendColor(Collection<? extends Player> players, Location location, java.awt.Color color) throws Exception {
		if (!this.hasColor) {
			return;
		}
		for (Player p : players) {
			sendColor(p, location, color);
		}
	}

	public void sendColor(Collection<? extends Player> players, Location location, org.bukkit.Color color) throws Exception {
		if (!this.hasColor) {
			return;
		}
		for (Player p : players) {
			sendColor(p, location, color);
		}
	}

	public void sendColor(Player[] players, Location location, org.bukkit.Color color) throws Exception {
		if (!this.hasColor) {
			return;
		}
		Player[] arrayOfPlayer;
		int j = (arrayOfPlayer = players).length;
		for (int i = 0; i < j; i++) {
			Player p = arrayOfPlayer[i];
			sendColor(p, location, color);
		}
	}

	public void sendColor(Player[] players, Location location, java.awt.Color color) throws Exception {
		if (!this.hasColor) {
			return;
		}
		Player[] arrayOfPlayer;
		int j = (arrayOfPlayer = players).length;
		for (int i = 0; i < j; i++) {
			Player p = arrayOfPlayer[i];
			sendColor(p, location, color);
		}
	}

	protected float getColor(float value) {
		if (value <= 0.0F) {
			value = -1.0F;
		}
		return value / 255.0F;
	}

	public void sendBlockCrack(Player player, Location location, int id, byte data, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (this != BLOCK_CRACK) {
			throw new IllegalArgumentException("This method is only available for BLOCK_CRACK!");
		}
		sendToPlayer(player, location, offsetX, offsetY, offsetZ, speed, count, new int[] { id, data });
	}

	public void sendBlockCrack(Collection<? extends Player> players, Location location, int id, byte data, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (this != BLOCK_CRACK) {
			throw new IllegalArgumentException("This method is only available for BLOCK_CRACK!");
		}
		for (Player p : players) {
			sendBlockCrack(p, location, id, data, offsetX, offsetY, offsetZ, speed, count);
		}
	}

	public void sendBlockCrack(Player[] players, Location location, int id, byte data, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (this != BLOCK_CRACK) {
			throw new IllegalArgumentException("This method is only available for BLOCK_CRACK!");
		}
		Player[] arrayOfPlayer;
		int j = (arrayOfPlayer = players).length;
		for (int i = 0; i < j; i++) {
			Player p = arrayOfPlayer[i];
			sendBlockCrack(p, location, id, data, offsetX, offsetY, offsetZ, speed, count);
		}
	}

	public void sendItemCrack(Player player, Location location, int id, byte data, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (this != ITEM_CRACK) {
			throw new IllegalArgumentException("This method is only available for ITEM_CRACK!");
		}
		sendToPlayer(player, location, offsetX, offsetY, offsetZ, speed, count, new int[] { id, data });
	}

	public void sendItemCrack(Collection<? extends Player> players, Location location, int id, byte data, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (this != ITEM_CRACK) {
			throw new IllegalArgumentException("This method is only available for ITEM_CRACK!");
		}
		for (Player p : players) {
			sendItemCrack(p, location, id, data, offsetX, offsetY, offsetZ, speed, count);
		}
	}

	public void sendItemCrack(Player[] players, Location location, int id, byte data, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (this != ITEM_CRACK) {
			throw new IllegalArgumentException("This method is only available for ITEM_CRACK!");
		}
		Player[] arrayOfPlayer;
		int j = (arrayOfPlayer = players).length;
		for (int i = 0; i < j; i++) {
			Player p = arrayOfPlayer[i];
			sendItemCrack(p, location, id, data, offsetX, offsetY, offsetZ, speed, count);
		}
	}

	public void sendBlockDust(Player p, Location location, int id, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (this != BLOCK_DUST) {
			throw new IllegalArgumentException("This method is only available for BLOCK_DUST!");
		}
		sendToPlayer(p, location, offsetX, offsetY, offsetZ, speed, count, new int[] { id });
	}

	public void sendBlockDust(Collection<? extends Player> players, Location location, int id, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (this != BLOCK_DUST) {
			throw new IllegalArgumentException("This method is only available for BLOCK_DUST!");
		}
		for (Player p : players) {
			sendBlockDust(p, location, id, offsetX, offsetY, offsetZ, speed, count);
		}
	}

	public void sendBlockDust(Player[] players, Location location, int id, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
		if (this != BLOCK_DUST) {
			throw new IllegalArgumentException("This method is only available for BLOCK_DUST!");
		}
		Player[] arrayOfPlayer;
		int j = (arrayOfPlayer = players).length;
		for (int i = 0; i < j; i++) {
			Player p = arrayOfPlayer[i];
			sendBlockDust(p, location, id, offsetX, offsetY, offsetZ, speed, count);
		}
	}

	protected static int getVersion(Player p) {
		try {
			Object handle = ReflectionUtilities.getHandle(p);
			Object connection = nmsFieldPlayerConnection.get(handle);
			Object network = nmsFieldNetworkManager.get(connection);
			Object channel;
			if (ReflectionUtilities.getVersion().contains("1_7")) {
				channel = nmsFieldNetworkManagerM.get(network);
			} else {
				channel = nmsFieldNetworkManagerI.get(network);
			}
			Object version = ReflectionUtilities.getVersion().contains("1_7") ? nmsNetworkGetVersion.invoke(network, new Object[] { channel }) : Integer.valueOf(47);
			return ((Integer) version).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	static {
		nmsPacketPlayOutParticle = ReflectionUtilities.getNMSClass("PacketPlayOutWorldParticles");

		particleRange = 25;

		String ver = ReflectionUtilities.getVersion();
		try {
			nmsPlayerConnection = ReflectionUtilities.getNMSClass("PlayerConnection");
			nmsEntityPlayer = ReflectionUtilities.getNMSClass("EntityPlayer");
			ioNettyChannel = ver.contains("1_7") ? Class.forName("net.minecraft.util.io.netty.channel.Channel") : Class.forName("io.netty.channel.Channel");

			nmsFieldPlayerConnection = ReflectionUtilities.getField(nmsEntityPlayer, "playerConnection");
			nmsFieldNetworkManager = ReflectionUtilities.getField(nmsPlayerConnection, "networkManager");
			nmsFieldNetworkManagerI = ReflectionUtilities.getField(nmsFieldNetworkManager.getType(), "i");
			nmsFieldNetworkManagerM = ReflectionUtilities.getField(nmsFieldNetworkManager.getType(), "m");

			nmsNetworkGetVersion = ReflectionUtilities.getMethod(nmsFieldNetworkManager.getType(), "getVersion", new Class[] { ioNettyChannel });
		} catch (Exception e) {
			System.err.println("[ParticleLIB] Error while loading: " + e.getMessage());
			e.printStackTrace(System.err);
			Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("ParticleLIB"));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Enum<?> getEnum(String enumFullName) {
		String[] x = enumFullName.split("\\.(?=[^\\.]+$)");
		if (x.length == 2) {
			String enumClassName = x[0];
			String enumName = x[1];
			try {
				Class<Enum> cl = (Class<Enum>) Class.forName(enumClassName);
				return Enum.valueOf(cl, enumName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static boolean isPlayerInRange(Player p, Location center) {
		double distance = 0.0D;
		if (!p.getLocation().getWorld().equals(center.getWorld())) {
			return false;
		}
		if ((distance = center.distanceSquared(p.getLocation())) > Double.MAX_VALUE) {
			return false;
		}
		return distance < particleRange * particleRange;
	}

	public static class ReflectionUtilities {
		public static void setValue(Object instance, String fieldName, Object value) throws Exception {
			Field field = instance.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		}

		public static Object getValue(Object instance, String fieldName) throws Exception {
			Field field = instance.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(instance);
		}

		public static String getVersion() {
			String name = Bukkit.getServer().getClass().getPackage().getName();
			String version = name.substring(name.lastIndexOf('.') + 1) + ".";
			return version;
		}

		public static Class<?> getNMSClass(String className) {
			String fullName = "net.minecraft.server." + getVersion() + className;
			Class<?> clazz = null;
			try {
				clazz = Class.forName(fullName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return clazz;
		}

		public static Class<?> getOBCClass(String className) {
			String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
			Class<?> clazz = null;
			try {
				clazz = Class.forName(fullName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return clazz;
		}

		public static Object getHandle(Object obj) {
			try {
				return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public static Field getField(Class<?> clazz, String name) {
			try {
				Field field = clazz.getDeclaredField(name);
				field.setAccessible(true);
				return field;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
			Method[] arrayOfMethod;
			int j = (arrayOfMethod = clazz.getMethods()).length;
			for (int i = 0; i < j; i++) {
				Method m = arrayOfMethod[i];
				if ((m.getName().equals(name)) && ((args.length == 0) || (ClassListEqual(args, m.getParameterTypes())))) {
					m.setAccessible(true);
					return m;
				}
			}
			return null;
		}

		public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
			boolean equal = true;
			if (l1.length != l2.length) {
				return false;
			}
			for (int i = 0; i < l1.length; i++) {
				if (l1[i] != l2[i]) {
					equal = false;
					break;
				}
			}
			return equal;
		}
	}
}
