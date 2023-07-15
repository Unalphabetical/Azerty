package me.puthvang.azerty.listeners;

import me.puthvang.azerty.lavaplayer.GuildMusicManager;
import me.puthvang.azerty.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildVoiceLeaveEventListener extends ListenerAdapter {

    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Guild guild = event.getGuild();

        AudioChannel left = event.getChannelLeft();
        if (left != null) {
            if (left.getMembers().size() == 1) {
                PlayerManager playerManager = PlayerManager.get();
                GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild);
                guildMusicManager.getTrackScheduler().setRepeat(false);
                guildMusicManager.getTrackScheduler().getQueue().clear();

                guild.getAudioManager().closeAudioConnection();
            }
        }

    }
}
