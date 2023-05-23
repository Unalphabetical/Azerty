package me.puthvang.azerty.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.puthvang.azerty.commands.manager.ICommand;
import me.puthvang.azerty.lavaplayer.GuildMusicManager;
import me.puthvang.azerty.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class NowPlayingCommand implements ICommand {

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getDescription() {
        return "Will display the current playing song";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        event.deferReply().queue();

        if(!memberVoiceState.inAudioChannel()) {
            event.getHook().editOriginal("You need to be in a voice channel").queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.getHook().editOriginal("I am not in an audio channel").queue();
            return;
        }

        if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.getHook().editOriginal("You are not in the same channel as me").queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        if(guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack() == null) {
            event.getHook().editOriginal("I am not playing anything").queue();
            return;
        }

        AudioTrackInfo info = guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack().getInfo();

        String s = "Currently playing: \n";
        s = s + "**Name**: " + info.title + "\n";
        s = s + "**Author**: " + info.author + "\n";
        s = s + "**URL**: <" + info.uri + ">\n";

        event.getHook().editOriginal(s).queue();
    }

}
