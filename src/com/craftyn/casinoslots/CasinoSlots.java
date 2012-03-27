package com.craftyn.casinoslots;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.craftyn.casinoslots.command.AnCommandExecutor;
import com.craftyn.casinoslots.listeners.AnBlockListener;
import com.craftyn.casinoslots.listeners.AnPlayerListener;
import com.craftyn.casinoslots.slot.RewardData;
import com.craftyn.casinoslots.slot.SlotData;
import com.craftyn.casinoslots.slot.TypeData;
import com.craftyn.casinoslots.util.ConfigData;
import com.craftyn.casinoslots.util.Permissions;
import com.craftyn.casinoslots.util.StatData;

public class CasinoSlots extends JavaPlugin{
	
	protected CasinoSlots plugin;
	
	public String prefix = "[Casino]";
	
	private AnPlayerListener playerListener = new AnPlayerListener(this);
	private AnBlockListener blockListener = new AnBlockListener(this);
	private AnCommandExecutor commandExecutor = new AnCommandExecutor(this);
	
	public ConfigData configData = new ConfigData(this);
	public SlotData slotData = new SlotData(this);
	public TypeData typeData = new TypeData(this);
	public StatData statsData = new StatData(this);
	public RewardData rewardData = new RewardData(this);
	public Permissions permission = new Permissions(this);
	
	public Economy economy = null;
	public final Logger logger = Logger.getLogger("Minecraft");

	public void onDisable() {		
		configData.save();
		configData.saveSlots();
		
		this.configData = null;
		this.slotData = null;
		this.typeData = null;
		this.statsData = null;
		this.rewardData = null;
		this.permission = null;
	}

	public void onEnable() {
		
		configData.load();
		
		PluginManager pm = this.getServer().getPluginManager();
		
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.blockListener, this);
		
		getCommand("casino").setExecutor(commandExecutor);
		
		if(!pm.isPluginEnabled("Vault")) {
			
			this.logger.warning(prefix +" Vault is required in order to use this plugin.");
			this.logger.warning(prefix +" dev.bukkit.org/server-mods/vault/");
			pm.disablePlugin(this);
		}
		else {
			
			if(!setupEconomy()) {
				this.logger.warning(prefix + " An economy plugin is required in order to use this plugin.");
				pm.disablePlugin(this);
			}
		}
		
	}
	
	// Sends a properly formatted message
	public void sendMessage(Player player, String message) {
		
		message = configData.prefixColor + prefix + configData.chatColor + " " + message;
		message = message.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
		player.sendMessage(message);	
	}
	
	//saves the files
	public void saveFiles() {
		configData.save();
		configData.saveSlots();
	}
	
	// Registers economy with Vault
	private Boolean setupEconomy() {
		
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        
        return (economy != null); 
    }

}