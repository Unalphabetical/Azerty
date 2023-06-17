package me.puthvang.azerty.listeners;

import me.puthvang.azerty.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SelectionEventListener extends ListenerAdapter {

    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        User user = event.getUser();
        TextChannel channel = event.getChannel().asTextChannel();
        String stringMenu = event.getComponentId();

        if (stringMenu.equals("azerty:music")) {

            String str = event.getValues().get(0).replaceAll("playYouTubeID=====", "");
            String[] values = str.split("&uid=");

            if (user.getId().equals(values[1])) {
                event.deferReply().queue();

                PlayerManager.get().play(channel, values[0], event.getHook());
            }
        }

    }

}
