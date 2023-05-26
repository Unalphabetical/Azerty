package me.puthvang.azerty.listeners;

import me.puthvang.azerty.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

public class ButtonEventListener extends ListenerAdapter {

    public void onButtonInteraction(ButtonInteractionEvent event) {
        User user = event.getUser();
        TextChannel channel = event.getChannel().asTextChannel();
        String buttonName = event.getComponentId();

        System.out.println(buttonName);
        if (buttonName.startsWith("playYouTubeID=====")) {
            String str = buttonName.replaceAll("playYouTubeID=====", "");
            String[] values = str.split("&uid=");

            System.out.println(Arrays.toString(values));

            if (user.getId().equals(values[1])) {
                event.deferReply().queue();

                PlayerManager.get().play(channel, values[0], event.getHook());
            }
        }

    }

}
