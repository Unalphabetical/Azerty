package me.puthvang.azerty.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void play(TextChannel channel, String trackURL, InteractionHook hook) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(channel.getGuild());
        String id = hook.getInteraction().getUser().getId();

        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusicManager.getTrackScheduler().queue(track);

                String s = "Adding to queue: `" + track.getInfo().title + "` by `" + track.getInfo().author + '`';
                hook.editOriginal(s).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getTracks().size() <= 0) return;

                List<AudioTrack> tracks = playlist.getTracks();
                List<Button> buttonList = new ArrayList<>();

                String s = "";
                for (int i = 1; i <= 5; i++) {
                    AudioTrack track = tracks.get(i);
                    AudioTrackInfo info = track.getInfo();

                    s = s + "[**" + i + "**] **[" + info.title + "](" + info.uri + ")**\n";
                    buttonList.add(Button.primary("playYouTubeID=====" + info.uri + "&uid=" + id, String.valueOf(i)));
                }

                hook.editOriginal(s).setActionRow(buttonList).queue();
            }

            @Override
            public void noMatches() {
                String s = "No audio exists for " + trackURL;
                hook.editOriginal(s).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                String s = "There seems to be an error when trying to load up " + trackURL;
                hook.editOriginal(s).queue();
            }
        });
    }

}