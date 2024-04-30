import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioPlayerEvent;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrameProvider;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MattoMusic extends ListenerAdapter {
    private final AudioPlayerManager playerManager;
    private final BlockingQueue<AudioTrack> queue;
    private final MattoAudioPlayer player;

    public MattoMusic() {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        this.queue = new LinkedBlockingQueue<>();
        this.player = new MattoAudioPlayer(playerManager);
        this.playerManager.setFrameBufferDuration(500);

        player.addListener(new AudioEventAdapter() {
            @Override
            public void onTrackStart(TrackStartEvent event) {
                super.onTrackStart(event);
                TextChannel channel = event.getTextChannel();
                channel.sendMessage("Now playing: " + event.getTrack().getInfo().title).queue();
            }

            @Override
            public void onTrackEnd(TrackEndEvent event) {
                super.onTrackEnd(event);
                nextTrack();
            }
        });
    }

    public void play(TextChannel channel, String trackUrl) {
        playerManager.loadItemOrdered(this, trackUrl, new MattoAudioLoadResultHandler(this, channel));
    }

    public void nextTrack() {
        player.startTrack(queue.poll(), false);
    }

    public AudioSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

    public static class AudioPlayerSendHandler implements AudioSendHandler {
        private final AudioPlayerEvent audioPlayer;

        public AudioPlayerSendHandler(AudioPlayerEvent audioPlayer) {
            this.audioPlayer = audioPlayer;
        }

        @Override
        public boolean canProvide() {
            return audioPlayer instanceof AudioFrameProvider;
        }

        @Override
        public ByteBuffer provide20MsAudio() {
            AudioFrameProvider frameProvider = (AudioFrameProvider) audioPlayer;
            AudioFrame frame = frameProvider.provide();
            return ByteBuffer.wrap(frame.getData());
        }

        @Override
        public boolean isOpus() {
            return true;
        }
    }

    public static class MattoAudioPlayer extends AudioEventAdapter {
        private final AudioPlayerManager playerManager;
        private final com.sedmelluq.discord.lavaplayer.player.AudioPlayer player;

        public MattoAudioPlayer(AudioPlayerManager playerManager) {
            this.playerManager = playerManager;
            this.player = playerManager.createPlayer();
        }

        public void startTrack(AudioTrack track, boolean interrupt) {
            player.startTrack(track, interrupt);
        }

        public void stopTrack() {
            player.stopTrack();
        }

        public void setPaused(boolean pause) {
            player.setPaused(pause);
        }

        public boolean isPaused() {
            return player.isPaused();
        }

        public void setVolume(int volume) {
            player.setVolume(volume);
        }

        public int getVolume() {
            return player.getVolume();
        }
    }

    @Nonnull
    public MattoAudioPlayer getPlayer() {
        return player;
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public void connectToVoiceChannel(TextChannel channel) {
        AudioManager audioManager = channel.getGuild().getAudioManager();
        audioManager.setSendingHandler(getSendHandler());
        audioManager.openAudioConnection(channel.getGuild().getVoiceChannels().get(0));
    }
}
