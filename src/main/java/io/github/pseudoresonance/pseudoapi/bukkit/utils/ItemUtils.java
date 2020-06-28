package io.github.pseudoresonance.pseudoapi.bukkit.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class ItemUtils {

	private static boolean setup = false;
	private static Class<?> craftItemStack = null;
	private static Method asNMSCopyMethod = null;
	private static Class<?> nmsItemStackClass = null;
	private static Method getItem = null;
	private static Class<?> nmsItemClass = null;
	private static Method getName = null;
	private static Class<?> nbtTagCompound = null;
	private static Method saveItemStack = null;
	private static Method getItemRarity = null;
	private static Class<?> enumItemRarityClass = null;
	private static Field enumChatFormatField = null;
	private static Class<?> enumChatFormatClass = null;
	
	private static Class<?> itemToolClass = null;
	private static Field blocksField = null;
	private static Class<?> craftBlock = null;
	private static Class<?> nmsBlock = null;
	private static Class<?> iBlockData = null;
	private static Method getNMSBlockMethod = null;
	private static Method getNMSMethod = null;
	private static Method durabilityMethod = null;

	private static boolean setup() {
		if (!setup) {
			try {
				craftItemStack = Class.forName("org.bukkit.craftbukkit." + Utils.getBukkitVersion() + ".inventory.CraftItemStack");
				asNMSCopyMethod = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
				asNMSCopyMethod.setAccessible(true);
				nmsItemStackClass = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".ItemStack");
				getItem = nmsItemStackClass.getMethod("getItem");
				getItem.setAccessible(true);
				nmsItemClass = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".Item");
				getName = nmsItemClass.getMethod("getName");
				getName.setAccessible(true);
				nbtTagCompound = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".NBTTagCompound");
				saveItemStack = nmsItemStackClass.getMethod("save", nbtTagCompound);
				saveItemStack.setAccessible(true);
				enumItemRarityClass = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".EnumItemRarity");
				enumChatFormatClass = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".EnumChatFormat");
				for (Method m : nmsItemClass.getMethods()) {
					if (m.getReturnType().equals(String.class) && m.getParameterCount() == 1 && m.getParameterTypes()[0] == nmsItemStackClass) {
						getName = m;
						getName.setAccessible(true);
						break;
					}
				}
				for (Method m : nmsItemStackClass.getMethods()) {
					if (m.getReturnType().equals(enumItemRarityClass)) {
						getItemRarity = m;
						getItemRarity.setAccessible(true);
						break;
					}
				}
				for (Field f : enumItemRarityClass.getFields()) {
					if (f.getType().equals(enumChatFormatClass)) {
						enumChatFormatField = f;
						enumChatFormatField.setAccessible(true);
						break;
					}
				}
				
				itemToolClass = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".ItemTool");
				for (Field f : itemToolClass.getDeclaredFields()) {
					if (Set.class.isAssignableFrom(f.getType())) {
						blocksField = f;
						blocksField.setAccessible(true);
						break;
					}
				}
				craftBlock = Class.forName("org.bukkit.craftbukkit." + Utils.getBukkitVersion() + ".block.CraftBlock");
				nmsBlock = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".Block");
				iBlockData = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".IBlockData");
				for (Method m : craftBlock.getDeclaredMethods()) {
					if (m.getReturnType().equals(nmsBlock)) {
						getNMSBlockMethod = m;
						getNMSBlockMethod.setAccessible(true);
					} else if (m.getReturnType().equals(iBlockData)) {
						getNMSMethod = m;
						getNMSMethod.setAccessible(true);
					}
				}
				durabilityMethod = nmsBlock.getDeclaredMethod("getDurability");
				durabilityMethod.setAccessible(true);
						
				setup = true;
				return true;
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
				PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.error_failed_itemutils_setup"));
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public static BaseComponent[] getAsTextComponent(ItemStack is) {
		if (setup()) {
			try {
				Object nmsItemStackObj = asNMSCopyMethod.invoke(null, is);
				Object nmsItem = getItem.invoke(nmsItemStackObj);
				String name = (String) (getName.invoke(nmsItem, nmsItemStackObj));
				Object rarity = getItemRarity.invoke(nmsItemStackObj);
				Object chatFormat = enumChatFormatField.get(rarity);
				String color = chatFormat.toString();
				Object nbtTagCompoundObj = nbtTagCompound.newInstance();
				Object itemAsJson = saveItemStack.invoke(nmsItemStackObj, nbtTagCompoundObj);
				String json = itemAsJson.toString();
				ItemMeta im = is.getItemMeta();
				BaseComponent itemTC = null;
				if (im.hasDisplayName()) {
					name = im.getDisplayName();
					if (!name.startsWith("§")) {
						name = ChatColor.ITALIC + name;
					}
					itemTC = new TextComponent(name);
				} else if (name.length() == 0) {
					name = WordUtils.capitalizeFully(is.getType().toString().replace('_', ' '));
				} else {
					itemTC = new TranslatableComponent(name);
				}
				TextComponent openingTC = new TextComponent(color + "[");
				TextComponent closingTC = new TextComponent(color + "]");
				openingTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] { new TextComponent(json) }));
				itemTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] { new TextComponent(json) }));
				closingTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] { new TextComponent(json) }));
				return new BaseComponent[] { openingTC, itemTC, closingTC };
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | InstantiationException e) {
				e.printStackTrace();
			}
		}
		PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.error_itemutils_failed"));
		throw new IllegalStateException("ItemUtils unable to run!");
	}

	public static boolean getCanToolBreakBlock(ItemStack tool, Block block) {
		if (setup()) {
			try {
				Object nmsItemStackObj = asNMSCopyMethod.invoke(null, tool);
				Object nmsItem = getItem.invoke(nmsItemStackObj);
				if (itemToolClass.isAssignableFrom(nmsItem.getClass())) {
					Set<?> blocksList = (Set<?>) blocksField.get(nmsItem);
					Object nmsBlock = getNMSBlockMethod.invoke(block);
					if (blocksList.contains(nmsBlock))
						return true;
					else {
						float strength = (float) durabilityMethod.invoke(nmsBlock);
						return strength < 1;
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
		}
		PseudoAPI.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudoapi.error_itemutils_failed"));
		throw new IllegalStateException("ItemUtils unable to run!");
	}

}
