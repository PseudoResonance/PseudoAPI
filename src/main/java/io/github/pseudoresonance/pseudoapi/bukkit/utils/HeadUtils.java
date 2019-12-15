package io.github.pseudoresonance.pseudoapi.bukkit.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;

public class HeadUtils {
	
	private static boolean init = false;
	
	private static Class<?> gameProfileClass = null;
	private static Constructor<?> gameProfileConstructor = null;
	private static Method getProperties = null;
	private static Class<?> propertyClass = null;
	private static Constructor<?> propertyConstructor = null;
	private static Class<?> propertiesClass = null;
	private static Method putProperty = null;
	private static Field profileField = null;
	
	private static UUID genericUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	public static ItemStack getHeadWithName(String name) {
		init();
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		try {
			Object gameProfile = gameProfileConstructor.newInstance(null, name);
			profileField.set(meta, gameProfile);
			head.setItemMeta(meta);
			return head;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			PseudoAPI.plugin.getChat().sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.could_not_get_head_from_uuid"));
			e.printStackTrace();
		}
		return null;
	}
	
	public static ItemStack getHeadWithUUID(UUID uuid, String name) {
		init();
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setDisplayName(name);
		meta.getPersistentDataContainer().set(new NamespacedKey(PseudoAPI.plugin, "HeadName"), PersistentDataType.STRING, name);
		meta.getPersistentDataContainer().set(new NamespacedKey(PseudoAPI.plugin, "Uuid"), PersistentDataType.STRING, uuid.toString());
		try {
			Object gameProfile = gameProfileConstructor.newInstance(uuid, "");
			profileField.set(meta, gameProfile);
			head.setItemMeta(meta);
			return head;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			PseudoAPI.plugin.getChat().sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.could_not_get_head_from_uuid"));
			e.printStackTrace();
		}
		return null;
	}
	
	public static ItemStack getHeadWithBase64(String base64, String name) {
		init();
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setDisplayName(name);
		meta.getPersistentDataContainer().set(new NamespacedKey(PseudoAPI.plugin, "HeadName"), PersistentDataType.STRING, name);
		meta.getPersistentDataContainer().set(new NamespacedKey(PseudoAPI.plugin, "Base64"), PersistentDataType.STRING, base64);
		try {
			Object gameProfile = gameProfileConstructor.newInstance(genericUuid, "");
			Object properties = getProperties.invoke(gameProfile);
			Object property = propertyConstructor.newInstance("textures", base64);
			putProperty.invoke(properties, "textures", property);
			profileField.set(meta, gameProfile);
			head.setItemMeta(meta);
			return head;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			PseudoAPI.plugin.getChat().sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.could_not_get_head_from_base64"));
			e.printStackTrace();
		}
		return null;
	}
	
	private static void init() {
		if (!init) {
			try {
				gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
				gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
				propertyClass = Class.forName("com.mojang.authlib.properties.Property");
				propertyConstructor = propertyClass.getConstructor(String.class, String.class);
				getProperties = gameProfileClass.getMethod("getProperties");
				propertiesClass = getProperties.getReturnType();
				for (Method m : propertiesClass.getMethods()) {
					if (m.getName().equals("put")) {
						putProperty = m;
						break;
					}
				}
				ItemStack head = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) head.getItemMeta();
				profileField = meta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
				PseudoAPI.plugin.getChat().sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.could_not_initialize_head_api"));
				e.printStackTrace();
			}
		}
	}

}
