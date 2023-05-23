package me.puthvang.azerty.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.puthvang.azerty.commands.manager.ICommand;
import me.puthvang.azerty.lavaplayer.GuildMusicManager;
import me.puthvang.azerty.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class QueueCommand implements ICommand {

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return "Will display the current queue";
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
        List<AudioTrack> queue = new ArrayList<>(guildMusicManager.getTrackScheduler().getQueue());

        StringBuilder s = new StringBuilder("Current queue:\n");

        if(queue.isEmpty()) {
            s = new StringBuilder("The current queue is empty\n");
        }

        for(int i = 0; i < queue.size(); i++) {
            AudioTrackInfo info = queue.get(i).getInfo();
            s.append("[").append(i + 1).append("]: ").append(info.title).append(" by ").append(info.author);
        }

        event.getHook().editOriginal(s.toString()).queue();
    }

}
