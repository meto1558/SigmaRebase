package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.ClientMode;
import com.mentalfrostbyte.jello.event.impl.EventRender;
import com.mentalfrostbyte.jello.event.impl.EventRender2D;
import com.mentalfrostbyte.jello.event.impl.TickEvent;
import com.mentalfrostbyte.jello.managers.impl.notifs.Notification;
import com.mentalfrostbyte.jello.managers.impl.music.*;
import com.mentalfrostbyte.jello.gui.unmapped.Class9275;
import com.mentalfrostbyte.jello.util.ClientColors;
import com.mentalfrostbyte.jello.util.MultiUtilities;
import com.mentalfrostbyte.jello.util.ResourceRegistry;
import com.mentalfrostbyte.jello.util.render.*;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.tagtraum.jipes.math.FFTFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.AudioTrack;
import net.sourceforge.jaad.mp4.api.Frame;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.api.Track;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;
import team.sdhq.eventBus.EventBus;
import team.sdhq.eventBus.annotations.EventTarget;
import totalcross.json.JSONException;
import totalcross.json.JSONObject;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.sound.sampled.FloatControl.Type;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MusicManager {
    private static final Minecraft mc = Minecraft.getInstance();
    public BufferedImage field32149;
    public String field32150 = "";
    public List<double[]> visualizerData = new ArrayList<>();
    public ArrayList<Double> amplitudes = new ArrayList<>();
    public SourceDataLine sourceDataLine;
    private boolean playing = false;
    private MusicVideoManager videoManager;
    private int volume = 50;
    private long duration = -1L;
    private Texture field32151;
    private BufferedImage field32152;
    private Texture field32153;
    private boolean field32154 = false;
    private transient volatile Thread audioThread = null;
    private int currentVideoIndex2;
    private long totalDuration = 0L;
    private int currentVideoIndex;
    private YoutubeVideoData currentVideo;
    private boolean spectrum = true;
    private AudioRepeatMode repeatMode = AudioRepeatMode.REPEAT;
    private boolean finished = false;
    private double field32168;
    private boolean field32169 = false;
    private double field32170 = 0.0;

    private static float[] method24305(byte[] var0, AudioFormat var1) {
        float[] var4 = new float[var0.length / var1.getFrameSize()];

        for (int var5 = 0; var5 < var0.length; var5 += var1.getFrameSize()) {
            int var6 = !var1.isBigEndian() ? method24307(var0, var5, var1.getFrameSize()) : method24308(var0, var5, var1.getFrameSize());
            var4[var5 / var1.getFrameSize()] = (float) var6 / 32768.0F;
        }

        return var4;
    }

    private static double[] method24306(float[] var0, float[] var1) {
        double[] var4 = new double[var0.length / 2];

        for (int var5 = 0; var5 < var4.length; var5++) {
            var4[var5] = Math.sqrt(var0[var5] * var0[var5] + var1[var5] * var1[var5]);
        }

        return var4;
    }

    private static int method24307(byte[] var0, int var1, int var2) {
        int var5 = 0;

        for (int var6 = 0; var6 < var2; var6++) {
            int var7 = var0[var1 + var6] & 255;
            var5 += var7 << 8 * var6;
        }

        return var5;
    }

    private static int method24308(byte[] var0, int var1, int var2) {
        int var5 = 0;

        for (int var6 = 0; var6 < var2; var6++) {
            int var7 = var0[var1 + var6] & 255;
            var5 += var7 << 8 * (var2 - var6 - 1);
        }

        return var5;
    }

    public void init() {
        EventBus.register(this);
        try {
            this.loadMusicSettings();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (!this.doesExecutableExist()) {
            this.method24331();
        }

        this.finished = false;
    }

    public void saveMusicSettings() {
        JSONObject object = new JSONObject();
        object.put("volume", this.volume);
        object.put("spectrum", this.spectrum);
        object.put("repeat", this.repeatMode.type);
        Client.getInstance().getConfig().put("music", object);
    }

    private void loadMusicSettings() throws JSONException {
        if (Client.getInstance().getConfig().has("music")) {
            JSONObject object = Client.getInstance().getConfig().getJSONObject("music");
            if (object != null) {
                if (object.has("volume")) {
                    this.volume = Math.max(0, Math.min(100, object.getInt("volume")));
                }

                if (object.has("spectrum")) {
                    this.spectrum = object.getBoolean("spectrum");
                }

                if (object.has("repeat")) {
                    this.repeatMode = AudioRepeatMode.parseRepeat(object.getInt("repeat"));
                }
            }
        }
    }

    @EventTarget
    public void method24296(EventRender var1) {
        if (Client.getInstance().clientMode == ClientMode.JELLO) {
            if (this.playing && this.visualizerData.size() != 0) {
                double[] var4 = this.visualizerData.get(0);
                if (this.amplitudes.isEmpty()) {
                    for (double v : var4) {
                        if (this.amplitudes.size() < 1024) {
                            this.amplitudes.add(v);
                        }
                    }
                }

                float var10 = 60.0F / (float) Minecraft.getFps();

                for (int var6 = 0; var6 < var4.length; var6++) {
                    double var7 = this.amplitudes.get(var6) - var4[var6];
                    boolean var9 = !(this.amplitudes.get(var6) < Double.MAX_VALUE);
                    this.amplitudes.set(var6, Math.min(2.256E7, Math.max(0.0, this.amplitudes.get(var6) - var7 * (double) Math.min(0.335F * var10, 1.0F))));
                    if (var9) {
                        this.amplitudes.set(var6, 0.0);
                    }
                }
            }
        }
    }

    @EventTarget
    public void method24297(EventRender2D var1) {
        if (this.playing && !this.visualizerData.isEmpty() && this.spectrum) {
            this.method24298();
        }
    }

    private void method24298() {
        if (!this.visualizerData.isEmpty()) {
            if (this.field32151 != null) {
                if (!this.amplitudes.isEmpty()) {
                    float var3 = 114.0F;
                    float var4 = (float) Math.ceil((float) mc.getMainWindow().getWidth() / var3);

                    for (int var5 = 0; (float) var5 < var3; var5++) {
                        float var6 = 1.0F - (float) (var5 + 1) / var3;
                        float var7 = (float) mc.getMainWindow().getHeight() / 1080.0F;
                        float var8 = ((float) (Math.sqrt(this.amplitudes.get(var5)) / 12.0) - 5.0F) * var7;
                        RenderUtil.drawRoundedRect2(
                                (float) var5 * var4,
                                (float) mc.getMainWindow().getHeight() - var8,
                                var4,
                                var8,
                                ColorUtils.applyAlpha(ClientColors.MID_GREY.getColor(), 0.2F * var6)
                        );
                    }

                    RenderUtil.initStencilBuffer();

                    for (int var13 = 0; (float) var13 < var3; var13++) {
                        float var14 = (float) mc.getMainWindow().getHeight() / 1080.0F;
                        float var15 = ((float) (Math.sqrt(this.amplitudes.get(var13)) / 12.0) - 5.0F) * var14;
                        RenderUtil.drawRoundedRect2((float) var13 * var4, (float) mc.getMainWindow().getHeight() - var15, var4, var15, ClientColors.LIGHT_GREYISH_BLUE.getColor());
                    }

                    RenderUtil.method11477(Class2329.field15940);
                    if (this.field32151 != null && this.field32153 != null) {
                        RenderUtil.drawImage(0.0F, 0.0F, (float) mc.getMainWindow().getWidth(), (float) mc.getMainWindow().getHeight(), this.field32153, 0.4F);
                    }

                    RenderUtil.restorePreviousStencilBuffer();
                    double var9 = 0.0;
                    float var16 = 4750;

                    for (int var17 = 0; var17 < 3; var17++) {
                        var9 = Math.max(var9, Math.sqrt(this.amplitudes.get(var17)) - 1000.0);
                    }

                    float var18 = 1.0F + (float) Math.round((float) (var9 / (double) (var16 - 1000)) * 0.14F * 75.0F) / 75.0F;
                    GL11.glPushMatrix();
                    GL11.glTranslated(60.0, mc.getMainWindow().getHeight() - 55, 0.0);
                    GL11.glScalef(var18, var18, 0.0F);
                    GL11.glTranslated(-60.0, -(mc.getMainWindow().getHeight() - 55), 0.0);
                    RenderUtil.drawImage(10.0F, (float) (mc.getMainWindow().getHeight() - 110), 100.0F, 100.0F, this.field32151);
                    RenderUtil.drawRoundedRect(10.0F, (float) (mc.getMainWindow().getHeight() - 110), 100.0F, 100.0F, 14.0F, 0.3F);
                    GL11.glPopMatrix();
                    String[] var11 = this.field32150.split(" - ");
                    int var12 = 30;
                    if (var11.length <= 1) {
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18_1,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 70),
                                var11[0],
                                ColorUtils.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.5F)
                        );
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 70),
                                var11[0],
                                ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.7F)
                        );
                    } else {
                        RenderUtil.drawString(
                                ResourceRegistry.JelloMediumFont20_1,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 81),
                                var11[0],
                                ColorUtils.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.4F)
                        );
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18_1,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 56),
                                var11[1],
                                ColorUtils.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.5F)
                        );
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 56),
                                var11[1],
                                ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.7F)
                        );
                        RenderUtil.drawString(
                                ResourceRegistry.JelloMediumFont20,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 81),
                                var11[0],
                                ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.6F)
                        );
                    }
                }
            }
        }
    }

    @EventTarget
    public void method24299(TickEvent var1) {
        if (!this.playing) {
            this.visualizerData.clear();
            this.amplitudes.clear();
        }

        try {
            if (this.field32154 && this.field32152 != null && this.field32149 != null && this.currentVideo == null && !mc.isGamePaused()) {
                if (this.field32153 != null) {
                    this.field32153.release();
                }

                if (this.field32151 != null) {
                    this.field32151.release();
                }

                this.field32153 = BufferedImageUtil.getTexture("picture", this.field32152);
                this.field32151 = BufferedImageUtil.getTexture("picture", this.field32149);
                Client.getInstance().notificationManager.send(new Notification("Now Playing", this.field32150, 7000, this.field32151));
                this.field32154 = false;
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        if (!this.field32154) {
            this.method24301();
        }
    }

    private void method24301() {
        if (this.currentVideo != null) {
            this.visualizerData.clear();
            new Thread(() -> this.method24309(this.currentVideo)).start();
        }
    }

    private void initializeAudioPlayback() {
        this.visualizerData.clear();
        if (this.videoManager != null) {
            while (this.audioThread != null && this.audioThread.isAlive()) {
                this.audioThread.interrupt();
            }

            this.audioThread = new Thread(
                    () -> {
                        Object pcmBufferData;
                        if (this.currentVideoIndex < 0 || this.currentVideoIndex >= this.videoManager.videoList.size()) {
                            this.currentVideoIndex = 0;
                        }

                        for (int i = this.currentVideoIndex; i < this.videoManager.videoList.size(); i++) {
                            URL videoURL = Class9275.getVideoStreamURL(this.videoManager.videoList.get(i).videoId);
                            Client.getInstance().getLogger().setThreadName(videoURL.toString());
                            this.currentVideoIndex2 = i;
                            this.currentVideo = this.videoManager.videoList.get(i);
                            this.visualizerData.clear();

                            // Wait until playback is allowed
                            while (!this.playing) {
                                this.visualizerData.clear();
                                if (Thread.interrupted()) {
                                    this.sourceDataLine.close();
                                    return;
                                }
                            }

                            try {
                                System.out.println(videoURL);
                                URL audioStreamURL = this.resolveAudioStream(videoURL);
                                Client.getInstance().getLogger().setThreadName(audioStreamURL == null ? "No stream" : audioStreamURL.toString());
                                if (audioStreamURL != null) {
                                    MusicStream stream = getMusicStream(audioStreamURL);
                                    MP4Container mp4Container = new MP4Container(stream);
                                    Movie movie = mp4Container.getMovie();
                                    List<Track> tracks = movie.getTracks();
                                    if (tracks.isEmpty()) {
                                        Client.getInstance().getLogger().setThreadName("No content");
                                    }

                                    AudioTrack audioTrack = (AudioTrack) movie.getTracks().get(1);
                                    AudioFormat audioFormat = new AudioFormat((float) audioTrack.getSampleRate(), audioTrack.getSampleSize(), audioTrack.getChannelCount(), true, true);
                                    this.sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
                                    this.sourceDataLine.open();
                                    this.sourceDataLine.start();
                                    this.duration = (long) movie.getDuration();
                                    if (this.duration > 1300L) {
                                        stream.close();
                                        Client.getInstance().notificationManager.send(new Notification("Now Playing", "Music is too long."));
                                    }

                                    Decoder decoder = new Decoder(audioTrack.getDecoderSpecificInfo());
                                    SampleBuffer sampleBuffer = new SampleBuffer();

                                    while (audioTrack.hasMoreFrames()) {
                                        while (!this.playing) {
                                            this.visualizerData.clear();
                                            if (Thread.interrupted()) {
                                                this.sourceDataLine.close();
                                                return;
                                            }
                                        }

                                        Frame nextFrame = audioTrack.readNextFrame();
                                        decoder.decodeFrame(nextFrame.getData(), sampleBuffer);
                                        pcmBufferData = sampleBuffer.getData();
                                        this.sourceDataLine.write((byte[]) pcmBufferData, 0, ((byte[]) pcmBufferData).length);
                                        float[] var29 = method24305(sampleBuffer.getData(), audioFormat);
                                        FFTFactory.JavaFFT var19 = new FFTFactory.JavaFFT(var29.length);
                                        float[][] var20 = var19.transform(var29);
                                        float[] var21 = var20[0];
                                        float[] var22 = var20[1];

                                        this.visualizerData.add(method24306(var21, var22));
                                        if (this.visualizerData.size() > 18) {
                                            this.visualizerData.remove(0);
                                        }

                                        this.method24328(this.sourceDataLine, this.volume);
                                        if (!Thread.interrupted()) {
                                            this.totalDuration = Math.round(audioTrack.getNextTimeStamp());
                                            this.field32170 = audioTrack.method23326();
                                            if (this.field32169) {
                                                audioTrack.seek(this.field32168);
                                                this.totalDuration = (long) this.field32168;
                                                this.field32169 = false;
                                            }
                                        }

                                        if (!audioTrack.hasMoreFrames()
                                                && (this.repeatMode == AudioRepeatMode.LOOP_CURRENT || this.repeatMode == AudioRepeatMode.REPEAT && this.videoManager.videoList.size() == 1)) {
                                            audioTrack.seek(0.0);
                                            this.totalDuration = 0L;
                                        }

                                        if (Thread.interrupted()) {
                                            this.sourceDataLine.close();
                                            return;
                                        }
                                    }

                                    this.sourceDataLine.close();
                                    stream.close();
                                }
                            } catch (IOException exception) {
                                if (exception.getMessage() != null && exception.getMessage().contains("403")) {
                                    System.out.println("installing");
                                    this.download();
                                }
                            } catch (LineUnavailableException lineException) {
                                lineException.printStackTrace();
                            }

                            if (this.repeatMode == AudioRepeatMode.LOOP_CURRENT) {
                                i--;
                            } else if (this.repeatMode == AudioRepeatMode.REPEAT && i == this.videoManager.videoList.size() - 1) {
                                i = -1;
                            } else if (this.repeatMode == AudioRepeatMode.NO_REPEAT) {
                                return;
                            }

                            if (i >= this.videoManager.videoList.size()) {
                                i = 0;
                            }
                        }
                    }
            );
            this.audioThread.start();
        }
    }

    private @NotNull MusicStream getMusicStream(URL audioStreamURL) throws IOException {
        URLConnection connection = audioStreamURL.openConnection();
        connection.setConnectTimeout(14000);
        connection.setReadTimeout(14000);
        connection.setUseCaches(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Connection", "Keep-Alive");
        InputStream inputStream = connection.getInputStream();
        MusicStream stream = new MusicStream(inputStream, new Class8808(this));
        return stream;
    }

    public void setRepeat(AudioRepeatMode repeat) {
        this.repeatMode = repeat;
        this.saveMusicSettings();
    }

    public AudioRepeatMode getRepeatMode() {
        return this.repeatMode;
    }

    public void method24309(YoutubeVideoData var1) {
        try {
            this.field32154 = true;
            BufferedImage var4 = ImageIO.read(new URL(var1.fullUrl));
            this.field32152 = ImageUtil.method35032(var4, 15);
            this.field32152 = this.field32152
                    .getSubimage(0, (int) ((float) this.field32152.getHeight() * 0.75F), this.field32152.getWidth(), (int) ((float) this.field32152.getHeight() * 0.2F));
            this.field32150 = var1.title;
            if (var4.getHeight() != var4.getWidth()) {
                if (this.field32150.contains("[NCS Release]")) {
                    this.field32149 = var4.getSubimage(1, 3, 170, 170);
                } else {
                    this.field32149 = var4.getSubimage(70, 0, 180, 180);
                }
            } else {
                this.field32149 = var4;
            }

            this.currentVideo = null;
        } catch (IOException | NumberFormatException var5) {
            var5.printStackTrace();
        }
    }

    public void setPlaying(boolean playing) {
        if (!playing && this.sourceDataLine != null) {
            this.sourceDataLine.flush();
        }

        this.playing = playing;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        this.saveMusicSettings();
    }

    public void setSpectrum(boolean spectrum) {
        this.spectrum = spectrum;
        this.saveMusicSettings();
    }

    public boolean isSpectrum() {
        return this.spectrum;
    }

    public int getVolume() {
        return this.volume;
    }

    public void method24315() {
        if (this.videoManager != null) {
            this.currentVideoIndex = this.currentVideoIndex2 - 1;
            this.totalDuration = 0L;
            this.field32170 = 0.0;
            this.initializeAudioPlayback();
        }
    }

    public void method24316() {
        if (this.videoManager != null) {
            this.currentVideoIndex = this.currentVideoIndex2 + 1;
            this.totalDuration = 0L;
            this.field32170 = 0.0;
            this.initializeAudioPlayback();
        }
    }

    public void playSong(MusicVideoManager manager, YoutubeVideoData video) {
        if (manager == null) {
            manager = new MusicVideoManager("temp", "temp", YoutubeContentType.PLAYLIST);
            manager.videoList.add(video);
        }

        this.videoManager = manager;
        this.playing = true;
        this.totalDuration = 0L;
        this.field32170 = 0.0;

        for (int i = 0; i < manager.videoList.size(); i++) {
            if (manager.videoList.get(i) == video) {
                this.currentVideoIndex = i;
            }
        }

        this.initializeAudioPlayback();
    }

    public boolean isPlayingSong() {
        return this.playing;
    }

    public long method24321() {
        return this.totalDuration;
    }

    public double method24322() {
        return this.field32170;
    }

    public synchronized URL resolveAudioStream(URL var1) {
        String var4 = var1.toString();
        String var5 = System.getProperty("user.home");
        YoutubeDLRequest request = new YoutubeDLRequest(var4, var5);
        request.setOption("get-url");
        request.setOption("no-check-certificate");
        request.setOption("rm-cache-dir");
        request.setOption("retries", "10");
        request.setOption("format", "18");

        try {
            YoutubeDL.setExecutablePath(this.stopYtDlp());
            YoutubeDLResponse var7 = YoutubeDL.execute(request);
            String var8 = var7.getOut();
            return new URL(var8);
        } catch (YoutubeDLException var9) {
            Client.getInstance().notificationManager.send(
                    new Notification("Failed to Play Song", "Check the logs for more details."));

            this.stopYtDlp();

            return null;
        } catch (MalformedURLException var10) {
            MultiUtilities.sendChatMessage("URL Error: " + var10);
            var10.printStackTrace();

            Client.getInstance().notificationManager.send(
                    new Notification("Failed to Play Song", "Invalid URL encountered."));

            this.stopYtDlp();

            return null;
        }
    }

    public String method24324() {
        return this.field32150;
    }

    public Texture method24325() {
        return this.field32153;
    }

    public Texture method24326() {
        return this.field32151;
    }

    public int method24327() {
        return (int) this.duration;
    }

    private void method24328(SourceDataLine var1, int var2) {
        try {
            FloatControl var5 = (FloatControl) var1.getControl(Type.MASTER_GAIN);
            BooleanControl var6 = (BooleanControl) var1.getControl(javax.sound.sampled.BooleanControl.Type.MUTE);
            if (var2 == 0) {
                var6.setValue(true);
            } else {
                var6.setValue(false);
                var5.setValue((float) (Math.log((double) var2 / 100.0) / Math.log(10.0) * 20.0));
            }
        } catch (Exception ignored) {
        }
    }

    public void method24329(double var1) {
        this.field32168 = var1;
        this.totalDuration = (long) this.field32168;
        this.field32169 = true;
    }

    public boolean doesExecutableExist() {
        File file = new File(Client.getInstance().file + "/music/yt-dlp");
        if (Util.getOSType() == Util.OS.WINDOWS) {
            file = new File(Client.getInstance().file + "/music/yt-dlp.exe");
        } else if (Util.getOSType() == Util.OS.LINUX) {
            file = new File(Client.getInstance().file + "/music/yt-dlp_linux");
        } else if (Util.getOSType() == Util.OS.OSX) {
            file = new File(Client.getInstance().file + "/music/yt-dlp_macos");
        }

        return file.exists();
    }

    public void method24331() {
        Client.getInstance().getLogger().setThreadName("Updating dependencies threaded");
        new Thread(this::download).start();
    }

    public void download() {
        if (!this.finished) {
            if (Util.getOSType() == Util.OS.WINDOWS || Util.getOSType() == Util.OS.OSX || Util.getOSType() == Util.OS.LINUX) {
                File musicDir = new File(Client.getInstance().file + "/music/");
                musicDir.mkdirs();

                //https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_macos
                //https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux
                //https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe
                String fileName =
                        Util.getOSType() == Util.OS.WINDOWS ? "yt-dlp.exe"
                                : Util.getOSType() == Util.OS.LINUX ? "yt-dlp_linux"
                                : "yt-dlp_macos";

                File targetFile = new File(Client.getInstance().file + "/music/" + fileName);

                try {
                    String urlString = "https://github.com/yt-dlp/yt-dlp/releases/download/" + fileName;
                    try (BufferedInputStream in = new BufferedInputStream(new URL(urlString).openStream());
                         FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
                        byte[] dataBuffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                            fileOutputStream.write(dataBuffer, 0, bytesRead);
                        }
                        finished = true;
                        System.out.println("Finished downloading yt-dlp");
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    finished = false;
                }
            } else {
                System.out.println("Failed to extract yt-dlp, because your OS is unsupported.");
                finished = false;
            }
        }
    }


    public String stopYtDlp() {
        String fileName =
                Util.getOSType() == Util.OS.WINDOWS ? "yt-dlp.exe"
                        : Util.getOSType() == Util.OS.LINUX ? "yt-dlp_linux"
                        : "yt-dlp_macos";
        String ytDlpFile = Client.getInstance().file.getAbsolutePath() + "/music/" + fileName;
        if (Util.getOSType() != Util.OS.WINDOWS) {
            File var4 = new File(ytDlpFile);
            var4.setExecutable(true);
        }

        return ytDlpFile;
    }

    public boolean hasPython() {
        String[] commands;
        if (Util.getOSType() == Util.OS.WINDOWS) {
            commands = new String[]{"python --version || py --version || python3 --version"};
        } else {
            commands = new String[]{"python3 --version || python --version"};
        }

        for (String command : commands) {
            try {
                Process process = new ProcessBuilder(getCommandArray(command)).redirectErrorStream(true).start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = reader.readLine();
                while (output != null) {
                    if (
                        // Windows / command prompt
                            output.contains("is not recognized as an internal or external command")
                                    // bash
                                    || output.contains("command not found")) {
                        break; // Move to the next command
                    }
                    if (output.toLowerCase().contains("python")) {
                        String version = output.replaceAll("[^0-9.]", "");
                        if (isVersionAtLeast(version, "3.12")) {
                            return true;
                        }
                    }
                    output = reader.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String[] getCommandArray(String command) {
        if (Util.getOSType() == Util.OS.WINDOWS) {
            return new String[]{"cmd", "/c", command};
        } else {
            return new String[]{"bash", "-c", command};
        }
    }

    private boolean isVersionAtLeast(String actual, String required) {
        String[] actualParts = actual.split("\\.");
        String[] requiredParts = required.split("\\.");
        int length = Math.max(actualParts.length, requiredParts.length);
        for (int i = 0; i < length; i++) {
            int actualPart = i < actualParts.length ? Integer.parseInt(actualParts[i]) : 0;
            int requiredPart = i < requiredParts.length ? Integer.parseInt(requiredParts[i]) : 0;
            if (actualPart > requiredPart) return true;
            if (actualPart < requiredPart) return false;
        }
        return true;
    }

    public boolean hasVCRedist() {
        if (Util.getOSType() != Util.OS.WINDOWS) {
            return true;
        } else {
            boolean hasRedist;

            try {
                hasRedist = Advapi32Util.registryGetIntValue(
                        WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\WOW6432Node\\Microsoft\\VisualStudio\\10.0\\VC\\VCRedist\\x86", "Installed"
                )
                        == 1;
            } catch (RuntimeException ignored) {
                hasRedist = false;
            }

            try {
                hasRedist = hasRedist
                        || Advapi32Util.registryGetIntValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\VisualStudio\\10.0\\VC\\VCRedist\\x86", "Installed") == 1;
            } catch (RuntimeException ignored) {
                hasRedist = false;
            }

            return hasRedist;
        }
    }
}
