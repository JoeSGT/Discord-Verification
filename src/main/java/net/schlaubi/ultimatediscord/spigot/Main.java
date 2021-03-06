package net.schlaubi.ultimatediscord.spigot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.schlaubi.ultimatediscord.util.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;

public class Main extends JavaPlugin {


    public static JDA jda;
    public static Main instance;
    public static JDABuilder bot;


    @Override
    public void onEnable() {
        instance = this;
        loadConfig();
        MySQL.connect();
        MySQL.createDatabase();
        this.getCommand("discord").setExecutor(new CommandDiscord());
        startBot();
        
    }

    private void startBot() {
        FileConfiguration cfg = getConfiguration();
        bot = new JDABuilder(AccountType.BOT);
        bot.setAutoReconnect(true);
        bot.setToken(cfg.getString("Discord.token"));
        bot.setGame(Game.playing(cfg.getString("Discord.game")));
        bot.addEventListener(new MessageListener());
        try {
            jda = bot.buildBlocking();
        } catch (LoginException | InterruptedException e) {
            Bukkit.getConsoleSender().sendMessage("§4§l[UltimateDiscord] Invalid discord token");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        MySQL.disconnect();
        jda.shutdown();
    }

    private void loadConfig() {
        File f = new File("plugins/UltimateDiscord", "config.yml");
        if(!f.exists())
            saveDefaultConfig();
    }

    public static FileConfiguration getConfiguration(){
        File f = new File("plugins/UltimateDiscord", "config.yml");
        return YamlConfiguration.loadConfiguration(f);
    }

}
