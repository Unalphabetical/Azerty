package me.puthvang.azerty.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {

    private TrackScheduler trackScheduler;
    private AudioPlayerSendHandler audioPlayerSendHandler;

    public GuildMusicManager(AudioPlayerManager manager) {
        AudioPlayer player = manager.createPlayer();
        this.trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
        this.audioPlayerSendHandler = new AudioPlayerSendHandler(player);
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public AudioPlayerSendHandler getAudioPlayerSendHandler() {
        return audioPlayerSendHandler;
    }
}