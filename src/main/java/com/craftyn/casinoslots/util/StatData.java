package com.craftyn.casinoslots.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.craftyn.casinoslots.CasinoSlots;

public class StatData {
    private CasinoSlots plugin;
    private HashMap<String, Stat> stats;
    public int globalSpins, globalWins, globalLosts;
    public double globalWon, globalLost;

    // Initialize StatData
    public StatData(CasinoSlots plugin) {
        this.plugin = plugin;
        this.stats = new HashMap<String, Stat>();
        this.globalSpins = 0;
        this.globalWins = 0;
        this.globalLosts = 0;
        this.globalWon = 0;
        this.globalLost = 0;
    }

    // Returns collection of all stats
    public Collection<Stat> getStats() {
        if (!this.stats.isEmpty()) {
            return this.stats.values();
        } else {
            return null;
        }
    }

    // Returns stats of a type
    public Stat getStat(String type) {
        return this.stats.get(type);
    }

    // Check if stat exists
    public Boolean isStat(String type) {
        return stats.containsKey(type);
    }

    // Adds the stats
    public void addStat(Stat stat) {
        this.stats.put(stat.getType(), stat);
        calculateGlobal();
    }

    // Recalculate the global stats
    private void calculateGlobal() {
        this.globalSpins = 0;
        this.globalWins = 0;
        this.globalLosts = 0;
        this.globalWon = 0;
        this.globalLost = 0;

        for (Stat s : this.stats.values()) {
            this.globalSpins += s.getSpins();
            this.globalWins += s.getWins();
            this.globalLosts += s.getLosts();
            this.globalWon += s.getWon();
            this.globalLost += s.getLost();
        }
    }

    // Loads a stat
    private void loadStat(String type) {
        String path = "types." + type + ".";

        Integer spins = plugin.configData.stats.getInt(path + "spins", 0);
        Integer wins = plugin.configData.stats.getInt(path + "wins", 0);
        Integer losts = plugin.configData.stats.getInt(path + "losts", 0);
        Double won = plugin.configData.stats.getDouble(path + "won", 0);
        Double lost = plugin.configData.stats.getDouble(path + "lost", 0);

        this.stats.put(type, new Stat(type, spins, wins, losts, won, lost));
    }

    // Load all stats
    public void loadStats() {
        if (plugin.configData.trackStats) {
            this.globalSpins = 0;
            this.globalWins = 0;
            this.globalLosts = 0;
            this.globalWon = 0.0;
            this.globalLost = 0.0;

            if (plugin.configData.stats.isConfigurationSection("types")) {
                Set<String> types = plugin.configData.stats.getConfigurationSection("types").getKeys(false);
                for (String type : types) {
                    loadStat(type);
                }
            }

            calculateGlobal();

            plugin.log("Loaded statistics for " + this.stats.size() + " types.");
        } else {
            plugin.log("Not tracking statistics.");
        }
    }
}
