package me.puthvang.azerty.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private Map<Long, GuildMusicManager> guildMusicManagers = new HashMap<>();
    private AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

    private PlayerManager() {
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public static PlayerManager get() {
        if(INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return guildMusicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildMusicManager musicManager = new GuildMusicManager(audioPlayerManager);

            guild.getAudioManager().setSendingHandler(musicManager.getAudioPlayerSendHandler());
            return musicManager;
        });
    }

    public void play(TextChannel channel, String trackURL) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(channel.getGuild());
        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusicManager.getTrackScheduler().queue(track);

                String s = "Adding to queue: `" + track.getInfo().title + "` by `" + track.getInfo().author + '`';
                channel.sendMessage(s).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getTracks().size() <= 0) return;

                AudioTrack track = playlist.getTracks().get(0);
                guildMusicManager.getTrackScheduler().queue(track);

                String s = "Adding to queue from a playlist: `" + track.getInfo().title + "` by `" + track.getInfo().author + '`';
                channel.sendMessage(s).queue();
            }

            @Override
            public void noMatches() {
                String s = "No audio exists for " + trackURL;
                channel.sendMessage(s).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {

                String s = "There seems to be an error when trying to load up " + trackURL;
                channel.sendMessage(s).queue();
            }
        });
    }

}