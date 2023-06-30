package me.puthvang.azerty.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.puthvang.azerty.commands.manager.ICommand;
import me.puthvang.azerty.lavaplayer.GuildMusicManager;
import me.puthvang.azerty.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PlayCommand implements ICommand {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Will play a song";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "Name of the song to play", true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        TextChannel channel = event.getChannel().asTextChannel();

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

        PlayerManager playerManager = PlayerManager.get();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild);
        AudioPlayer audioPlayer = guildMusicManager.getTrackScheduler().getPlayer();

        if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            if (audioPlayer.getPlayingTrack() != null) {
                event.getHook().editOriginal("I'm currently playing an audio in another channel, try again after I finish.").queue();
                return;
            } else {
                guild.getAudioManager().openAudioConnection(memberVoiceState.getChannel());
            }
        }

        String name = event.getOption("name").getAsString();
        try {
            new URI(name);
        } catch (URISyntaxException e) {
            name = "ytsearch:" + name;
        }

        playerManager.play(channel, name, event.getHook());
    }

}
