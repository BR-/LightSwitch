package me.br_.minecraft.bukkit.lightswitch;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LSMain extends JavaPlugin {
	public LSListener bL;

	@Override
	public void onDisable() {
		bL.save(); // save the positions of the levers
		bL = null;
		System.out.println("[LightSwitch] Disabled.");
	}

	@Override
	public void onEnable() {
		bL = new LSListener();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, bL, Event.Priority.Normal,
				this); // whenever a block is powered
						// or unpowered by redstone
		pm.registerEvent(Event.Type.BLOCK_PLACE, bL, Event.Priority.Normal,
				this); // whenever a block is placed
		pm.registerEvent(Event.Type.BLOCK_BREAK, bL, Event.Priority.Normal,
				this); // whenever a block is destroyed
		System.out.println("[LightSwitch] Enabled.");
	}
}
