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

import java.util.ArrayList;
import java.util.List;

public class VolumeCommand implements ICommand {

    public String getName() {
        return "volume";
    }

    @Override
    public String getDescription() {
        return "Changes the volume of the bot";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "volume", "Volume to set it to", true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (event.getGuild() == null) return;

        Guild guild = event.getGuild();

        PlayerManager playerManager = PlayerManager.get();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild);
        AudioPlayer audioPlayer = guildMusicManager.getTrackScheduler().getPlayer();

        int curentVolume = audioPlayer.getVolume();
        int volume = event.getOption("volume").getAsInt();

        if (volume > 500) volume = 500;
        if (volume < 0) volume = 0;

        audioPlayer.setVolume(volume);
        event.getHook().editOriginal("The volume has been changed from " + curentVolume + " to " + volume).queue();
    }

}
