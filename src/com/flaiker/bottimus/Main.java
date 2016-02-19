package com.flaiker.bottimus;

import com.flaiker.bottimus.configuration.Configuration;
import com.flaiker.bottimus.configuration.ConfigurationBuilder;
import com.flaiker.bottimus.listeners.CommandListener;
import com.flaiker.bottimus.listeners.JoinMessageListener;
import com.flaiker.bottimus.tools.ChannelLogHandler;
import com.flaiker.bottimus.tools.LogFormatter;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;

import java.io.File;
import java.io.PrintStream;
import java.util.logging.*;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Set up logging
        LogManager.getLogManager().reset();
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new LogFormatter());
        Logger.getLogger("").addHandler(handler);

        // Redirect default System.out to the logger (because JDA does not use java logging...)
        System.setOut(new PrintStream(System.out) {
            @Override
            public void print(String s) {
                Logger.getLogger("System.out").info(s);
            }
        });

        LOG.info("Starting...");

        try {
            // Load configuration
            new ConfigurationBuilder(new File("config.properties")).build();

            // Start JDA
            JDA jda = new JDABuilder()
                        .setEmail(Configuration.EMAIL)
                        .setPassword(Configuration.PASSWORD)
                        .addListener(new JoinMessageListener())
                        .addListener(new CommandListener())
                        .buildAsync();

            // Log to the specified log channel
            ChannelLogHandler channelLogHandler = new ChannelLogHandler(jda, new LogFormatter());
            Logger.getLogger("").addHandler(channelLogHandler);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An unexpected error occurred", e);
        }
    }
}
