package me.puthvang.azerty.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import me.puthvang.azerty.commands.manager.ICommand;
import me.puthvang.azerty.lavaplayer.GuildMusicManager;
import me.puthvang.azerty.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class PauseCommand implements ICommand {
    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return "Pause or unpause the audio that is currently played";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (event.getGuild() == null) return;

        Guild guild = event.getGuild();
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.getHook().editOriginal("You need to be in a voice channel").queue();
            return;
        }

        Member self = guild.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            guild.getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        } else {
            if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
                event.getHook().editOriginal("You need to be in the same channel as me").queue();
                return;
            }
        }

        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(guild);
        AudioPlayer audioPlayer = guildMusicManager.getTrackScheduler().getPlayer();

        audioPlayer.setPaused(!audioPlayer.isPaused());

        String s = "The audio has been paused";
        if (!audioPlayer.isPaused()){
            s = "The audio has been unpaused";
        }

        event.getHook().editOriginal(s).queue();
    }
}
