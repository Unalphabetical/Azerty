package me.puthvang.azerty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.puthvang.azerty.commands.*;
import me.puthvang.azerty.commands.manager.CommandManager;
import me.puthvang.azerty.listeners.SelectionEventListener;
import me.puthvang.azerty.utilities.RegexPattern;
import me.puthvang.azerty.utilities.Type;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;

public class Azerty {

    private static Data data;
    private static Gson gson;
    private static JDA bot;

    public static void main(String[] args){
        gson = new GsonBuilder().setPrettyPrinting().create();
        init();
    }

    private static void init() {
        environmentalInit();
        fileInit();
        setupData();
        login();
    }

    private static void environmentalInit(){
        if (Paths.get(".env").toFile().exists()) return;
        if (System.getenv("token") == null) {
            boolean validToken = false;
            while (!validToken) {
                System.out.println("Enter bot token: ");

                Scanner scanner = new Scanner(System.in);
                String token = scanner.next();
                Bot bot = new Bot(token);

                Matcher matcher = RegexPattern.DISCORD_TOKEN.matcher(token);
                validToken = matcher.find();
                if (validToken) {
                    System.out.println("Are you using repl.it or anything that uses Environmental Variables? (1,y,yes,true)");
                    System.out.println("The bot will default to .env files if Environmental Variables fail");
                    String environmentalVariables = scanner.next();
                    if (Type.isTrue(environmentalVariables)){
                        data = new Data(bot, true);
                    } else {
                        data = new Data(bot, false);
                    }
                    scanner.close();
                }
            }
        }
    }

    private static void fileInit(){
        if (!Paths.get(".env").toFile().exists()) {
            try {
                FileWriter fileWriter = new FileWriter(Paths.get(".env").toFile());
                fileWriter.write(gson.toJson(data));
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setupData(){
        if (System.getenv("token") != null) {
            Bot bot = new Bot(System.getenv("token"));
            data = new Data(bot, true);
        } else if (Paths.get(".env").toFile().exists()) {
            try {
                FileReader reader = new FileReader(".env");
                data = gson.fromJson(reader, Data.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void login(){
        try {
            System.out.println("Token found, attempting to log in.");

            CommandManager manager = new CommandManager();
            manager.add(new PlayCommand());
            manager.add(new SkipCommand());
            manager.add(new QueueCommand());
            manager.add(new NowPlayingCommand());
            manager.add(new PauseCommand());

            bot = JDABuilder.createDefault(data.getBot().getToken())
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES)
                    .enableIntents(GatewayIntent.GUILD_PRESENCES)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                    .enableCache(CacheFlag.ACTIVITY)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .addEventListeners(manager)
                    .addEventListeners(new SelectionEventListener())
                    .build();

            System.out.println("Logged in successfully!");
        } catch (InvalidTokenException e) {
            if (data.isUsingEnvironmentalVariables()){
                System.out.println("Looks like there's an invalid token in your environmental variables. ");
            } else {
                System.out.println("Looks like there's an invalid token in your .env file. ");
            }
        }
    }

    public static JDA getBot() {
        if (bot == null) init();
        return bot;
    }

}
