package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender2D;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender2DOffset;
import com.mentalfrostbyte.jello.event.impl.player.EventPlayerTick;
import com.mentalfrostbyte.jello.managers.util.notifs.Notification;
import com.mentalfrostbyte.jello.module.impl.render.jello.esp.util.Class2329;
import com.mentalfrostbyte.jello.util.client.ClientMode;
import com.mentalfrostbyte.jello.util.client.network.youtube.YoutubeContentType;
import com.mentalfrostbyte.jello.util.client.network.youtube.YoutubeUtil;
import com.mentalfrostbyte.jello.util.client.network.youtube.YoutubeVideoData;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.MinecraftUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.system.network.ImageUtil;
import com.mentalfrostbyte.jello.util.system.sound.AudioRepeatMode;
import com.mentalfrostbyte.jello.util.system.sound.BasicAudioProcessor;
import com.mentalfrostbyte.jello.util.system.sound.MusicStream;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.tagtraum.jipes.math.FFTFactory;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.*;
import org.newdawn.slick.opengl.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL11;
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
    public BufferedImage scaledThumbnail;
    public String songTitle = "";
    public List<double[]> visualizerData = new ArrayList<double[]>();
    public ArrayList<Double> amplitudes = new ArrayList<Double>();
    public SourceDataLine sourceDataLine;
    private boolean playing = false;
    private MusicVideoManager videoManager;
    private int volume = 50;
    private long duration = -1L;
    private Texture notificationImage;
    private BufferedImage thumbnailImage;
    private Texture songThumbnail;
    private boolean isThumbnailProcessing = false;
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
            int var6 = !var1.isBigEndian() ? method24307(var0, var5, var1.getFrameSize())
                    : method24308(var0, var5, var1.getFrameSize());
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
            this.method24295();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (!this.method24330()) {
            this.setupDownloadThread();
        }

        this.finished = false;
    }

    public void saveMusicSettings() {
        JSONObject var3 = new JSONObject();
        var3.put("volume", this.volume);
        var3.put("spectrum", this.spectrum);
        var3.put("repeat", this.repeatMode.type);
        Client.getInstance().getConfig().put("music", var3);
    }

    private void method24295() throws JSONException {
        if (Client.getInstance().getConfig().has("music")) {
            JSONObject var3 = Client.getInstance().getConfig().getJSONObject("music");
            if (var3 != null) {
                if (var3.has("volume")) {
                    this.volume = Math.max(0, Math.min(100, var3.getInt("volume")));
                }

                if (var3.has("spectrum")) {
                    this.spectrum = var3.getBoolean("spectrum");
                }

                if (var3.has("repeat")) {
                    this.repeatMode = AudioRepeatMode.parseRepeat(var3.getInt("repeat"));
                }
            }
        }
    }

    @EventTarget
    public void method24296(EventRender2DOffset event) {
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
                    this.amplitudes.set(var6, Math.min(2.256E7,
                            Math.max(0.0, this.amplitudes.get(var6) - var7 * (double) Math.min(0.335F * var10, 1.0F))));
                    if (var9) {
                        this.amplitudes.set(var6, 0.0);
                    }
                }
            }
        }
    }

    @EventTarget
    public void method24297(EventRender2D event) {
        if (this.playing && !this.visualizerData.isEmpty() && this.spectrum) {
            this.renderSpectrum();
        }
    }

    private void renderSpectrum() {
        if (!this.visualizerData.isEmpty()) {
            if (this.notificationImage != null) {
                if (!this.amplitudes.isEmpty()) {
                    float var3 = 114.0F;
                    float var4 = (float) Math.ceil((float) mc.mainWindow.getWidth() / var3);

                    for (int var5 = 0; (float) var5 < var3; var5++) {
                        float var6 = 1.0F - (float) (var5 + 1) / var3;
                        float var7 = (float) mc.mainWindow.getHeight() / 1080.0F;
                        float var8 = ((float) (Math.sqrt(this.amplitudes.get(var5)) / 12.0) - 5.0F) * var7;
                        RenderUtil.renderBackgroundBox(
                                (float) var5 * var4,
                                (float) mc.mainWindow.getHeight() - var8,
                                var4,
                                var8,
                                RenderUtil2.applyAlpha(ClientColors.MID_GREY.getColor(), 0.2F * var6));
                    }

                    RenderUtil.method11476();

                    for (int var13 = 0; (float) var13 < var3; var13++) {
                        float var14 = (float) mc.mainWindow.getHeight() / 1080.0F;
                        float var15 = ((float) (Math.sqrt(this.amplitudes.get(var13)) / 12.0) - 5.0F) * var14;
                        RenderUtil.renderBackgroundBox((float) var13 * var4,
                                (float) mc.mainWindow.getHeight() - var15, var4, var15,
                                ClientColors.LIGHT_GREYISH_BLUE.getColor());
                    }

                    RenderUtil.method11477(Class2329.field15940);
                    if (this.notificationImage != null && this.songThumbnail != null) {
                        RenderUtil.drawImage(0.0F, 0.0F, (float) mc.mainWindow.getWidth(),
                                (float) mc.mainWindow.getHeight(), this.songThumbnail, 0.4F);
                    }

                    RenderUtil.method11478();
                    double var9 = 0.0;
                    float var16 = 4750;

                    for (int var17 = 0; var17 < 3; var17++) {
                        var9 = Math.max(var9, Math.sqrt(this.amplitudes.get(var17)) - 1000.0);
                    }

                    float var18 = 1.0F
                            + (float) Math.round((float) (var9 / (double) (var16 - 1000)) * 0.14F * 75.0F) / 75.0F;
                    GL11.glPushMatrix();
                    GL11.glTranslated(60.0, mc.mainWindow.getHeight() - 55, 0.0);
                    GL11.glScalef(var18, var18, 0.0F);
                    GL11.glTranslated(-60.0, -(mc.mainWindow.getHeight() - 55), 0.0);
                    RenderUtil.drawImage(10.0F, (float) (mc.mainWindow.getHeight() - 110), 100.0F, 100.0F,
                            this.notificationImage);
                    RenderUtil.drawRoundedRect(10.0F, (float) (mc.mainWindow.getHeight() - 110), 100.0F, 100.0F,
                            14.0F, 0.3F);
                    GL11.glPopMatrix();
                    String[] var11 = this.songTitle.split(" - ");
                    int var12 = 30;
                    if (var11.length <= 1) {
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18_1,
                                130.0F,
                                (float) (mc.mainWindow.getHeight() - 70),
                                var11[0],
                                RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.5F));
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18,
                                130.0F,
                                (float) (mc.mainWindow.getHeight() - 70),
                                var11[0],
                                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.7F));
                    } else {
                        RenderUtil.drawString(
                                ResourceRegistry.JelloMediumFont20_1,
                                130.0F,
                                (float) (mc.mainWindow.getHeight() - 81),
                                var11[0],
                                RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.4F));
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18_1,
                                130.0F,
                                (float) (mc.mainWindow.getHeight() - 56),
                                var11[1],
                                RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.5F));
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18,
                                130.0F,
                                (float) (mc.mainWindow.getHeight() - 56),
                                var11[1],
                                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.7F));
                        RenderUtil.drawString(
                                ResourceRegistry.JelloMediumFont20,
                                130.0F,
                                (float) (mc.mainWindow.getHeight() - 81),
                                var11[0],
                                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.6F));
                    }
                }
            }
        }
    }

    @EventTarget
    public void onTick(EventPlayerTick event) {
        if (!this.playing) {
            this.visualizerData.clear();
            this.amplitudes.clear();
        }

        try {
            if (this.isThumbnailProcessing && this.thumbnailImage != null && this.scaledThumbnail != null && this.currentVideo == null
                    && !mc.isGamePaused()) {
                if (this.songThumbnail != null) {
                    this.songThumbnail.release();
                }

                if (this.notificationImage != null) {
                    this.notificationImage.release();
                }

                this.songThumbnail = BufferedImageUtil.getTexture("picture", this.thumbnailImage);
                this.notificationImage = BufferedImageUtil.getTexture("picture", this.scaledThumbnail);
                Client.getInstance().notificationManager
                        .send(new Notification("Now Playing", this.songTitle, 7000, this.notificationImage));
                this.isThumbnailProcessing = false;
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        if (!this.isThumbnailProcessing) {
            this.startProcessingVideoThumbnail();
        }
    }

    private void startProcessingVideoThumbnail() {
        if (this.currentVideo != null) {
            this.visualizerData.clear();
            new Thread(() -> this.processVideoThumbnail(this.currentVideo)).start();
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
                        byte[] pcmBufferData;
                        if (this.currentVideoIndex < 0 || this.currentVideoIndex >= this.videoManager.videoList.size()) {
                            this.currentVideoIndex = 0;
                        }

                        for (int var4 = this.currentVideoIndex; var4 < this.videoManager.videoList.size(); var4++) {
                            URL songUrl = YoutubeUtil.getVideoStreamURL(this.videoManager.videoList.get(var4).videoId);
                            Client.getInstance().getLogger().setThreadName(songUrl.toString());
                            this.currentVideoIndex2 = var4;
                            this.currentVideo = this.videoManager.videoList.get(var4);
                            this.visualizerData.clear();

                            while (!this.playing) {
                                try {
                                    Thread.sleep(300L);
                                }
                                catch (final InterruptedException ex) {}
                                this.visualizerData.clear();
                                if (Thread.interrupted()) {
                                    if (this.sourceDataLine != null) {
                                        this.sourceDataLine.close();
                                    }
                                    return;
                                }
                            }

                            try {
                                System.out.println(songUrl);
                                URL url = this.resolveAudioStream(songUrl);
                                Client.getInstance().getLogger().setThreadName(url == null ? "No stream" : url.toString());
                                if (url != null) {
                                    URLConnection urlConnection = url.openConnection();
                                    urlConnection.setConnectTimeout(14000);
                                    urlConnection.setReadTimeout(14000);
                                    urlConnection.setUseCaches(true);
                                    urlConnection.setDoOutput(true);
                                    urlConnection.setRequestProperty("Connection", "Keep-Alive");

                                    InputStream iS = urlConnection.getInputStream();
                                    MusicStream mS = new MusicStream(iS, new BasicAudioProcessor());
                                    MP4Container container = new MP4Container(mS);
                                    Movie movie = container.getMovie();
                                    List<Track> tracks = movie.getTracks();
                                    if (tracks.isEmpty()) {
                                        Client.getInstance().getLogger().setThreadName("No content");
                                    }

                                    AudioTrack var13 = (AudioTrack) movie.getTracks().get(1);
                                    AudioFormat var14 = new AudioFormat((float) var13.getSampleRate(),
                                            var13.getSampleSize(), var13.getChannelCount(), true, true);
                                    this.sourceDataLine = AudioSystem.getSourceDataLine(var14);
                                    this.sourceDataLine.open();
                                    this.sourceDataLine.start();
                                    this.duration = (long) movie.getDuration();
                                    if (this.duration > 1300L) {
                                        mS.close();
                                        Client.getInstance().notificationManager
                                                .send(new Notification("Now Playing", "Music is too long."));
                                    }

                                    Decoder var15 = new Decoder(var13.getDecoderSpecificInfo());
                                    SampleBuffer var16 = new SampleBuffer();

                                    while (var13.hasMoreFrames()) {
                                        while (!this.playing) {
                                            this.visualizerData.clear();
                                            if (Thread.interrupted()) {
                                                this.sourceDataLine.close();
                                                return;
                                            }
                                        }

                                        Frame var18 = var13.readNextFrame();
                                        var15.decodeFrame(var18.getData(), var16);
                                        pcmBufferData = var16.getData();
                                        this.sourceDataLine.write((byte[]) pcmBufferData, 0, ((byte[]) pcmBufferData).length);
                                        float[] var29 = method24305(var16.getData(), var14);
                                        FFTFactory.JavaFFT var19 = new FFTFactory.JavaFFT(var29.length);
                                        float[][] var20 = var19.transform(var29);
                                        float[] var21 = var20[0];
                                        float[] var22 = var20[1];
                                        this.visualizerData.add(method24306(var21, var22));
                                        if (this.visualizerData.size() > 18) {
                                            this.visualizerData.remove(0);
                                        }

                                        this.adjustAudioVolume(this.sourceDataLine, this.volume);
                                        if (!Thread.interrupted()) {
                                            this.totalDuration = Math.round(var13.getNextTimeStamp());
                                            this.field32170 = var13.method23326();
                                            if (this.field32169) {
                                                var13.seek(this.field32168);
                                                this.totalDuration = (long) this.field32168;
                                                this.field32169 = false;
                                            }
                                        }

                                        if (!var13.hasMoreFrames()
                                                && (this.repeatMode == AudioRepeatMode.LOOP_CURRENT
                                                || this.repeatMode == AudioRepeatMode.REPEAT
                                                && this.videoManager.videoList.size() == 1)) {
                                            var13.seek(0.0);
                                            this.totalDuration = 0L;
                                        }

                                        if (Thread.interrupted()) {
                                            this.sourceDataLine.close();
                                            return;
                                        }
                                    }

                                    this.sourceDataLine.close();
                                    mS.close();
                                }
                            } catch (IOException var24) {
                                if (var24.getMessage() != null && var24.getMessage().contains("403")) {
                                    System.out.println("installing");
                                    this.download();
                                }
                            } catch (LineUnavailableException var25) {
                                var25.printStackTrace();
                            }

                            if (this.repeatMode == AudioRepeatMode.LOOP_CURRENT) {
                                var4--;
                            } else if (this.repeatMode == AudioRepeatMode.REPEAT
                                    && var4 == this.videoManager.videoList.size() - 1) {
                                var4 = -1;
                            } else if (this.repeatMode == AudioRepeatMode.NO_REPEAT) {
                                return;
                            }

                            if (var4 >= this.videoManager.videoList.size()) {
                                var4 = 0;
                            }
                        }
                    });
            this.audioThread.start();
        }
    }

    public void setRepeat(AudioRepeatMode var1) {
        this.repeatMode = var1;
        this.saveMusicSettings();
    }

    public AudioRepeatMode getRepeatMode() {
        return this.repeatMode;
    }

    public void processVideoThumbnail(YoutubeVideoData var1) {
        try {
            this.isThumbnailProcessing = true;
            BufferedImage var4 = ImageIO.read(new URL(var1.fullUrl));
            this.thumbnailImage = ImageUtil.applyBlur(var4, 15);
            this.thumbnailImage = this.thumbnailImage
                    .getSubimage(0, (int) ((float) this.thumbnailImage.getHeight() * 0.75F), this.thumbnailImage.getWidth(),
                            (int) ((float) this.thumbnailImage.getHeight() * 0.2F));
            this.songTitle = var1.title;
            if (var4.getHeight() != var4.getWidth()) {
                if (this.songTitle.contains("[NCS Release]")) {
                    this.scaledThumbnail = var4.getSubimage(1, 3, 170, 170);
                } else {
                    this.scaledThumbnail = var4.getSubimage(70, 0, 180, 180);
                }
            } else {
                this.scaledThumbnail = var4;
            }

            this.currentVideo = null;
        } catch (IOException | NumberFormatException var5) {
            var5.printStackTrace();
        }
    }

    public void setPlaying(boolean var1) {
        if (!var1 && this.sourceDataLine != null) {
            this.sourceDataLine.flush();
        }

        this.playing = var1;
    }

    public void setVolume(int var1) {
        this.volume = var1;
        this.saveMusicSettings();
    }

    public void setSpectrum(boolean var1) {
        this.spectrum = var1;
        this.saveMusicSettings();
    }

    public boolean isSpectrum() {
        return this.spectrum;
    }

    public int getVolume() {
        return this.volume;
    }

    public void playPreviousSong() {
        if (this.videoManager != null) {
            this.currentVideoIndex = this.currentVideoIndex2 - 1;
            this.totalDuration = 0L;
            this.field32170 = 0.0;
            this.initializeAudioPlayback();
        }
    }

    public void playNextSong() {
        if (this.videoManager != null) {
            this.currentVideoIndex = this.currentVideoIndex2 + 1;
            this.totalDuration = 0L;
            this.field32170 = 0.0;
            this.initializeAudioPlayback();
        }
    }

    public void playSong(MusicVideoManager var1, YoutubeVideoData var2) {
        if (var1 == null) {
            var1 = new MusicVideoManager("temp", "temp", YoutubeContentType.PLAYLIST);
            var1.videoList.add(var2);
        }

        this.videoManager = var1;
        this.playing = true;
        this.totalDuration = 0L;
        this.field32170 = 0.0;

        for (int var5 = 0; var5 < var1.videoList.size(); var5++) {
            if (var1.videoList.get(var5) == var2) {
                this.currentVideoIndex = var5;
            }
        }

        this.initializeAudioPlayback();
    }

    public boolean isPlayingSong() {
        return this.playing;
    }

    public long getDuration() {
        return this.totalDuration;
    }

    public double method24322() {
        return this.field32170;
    }

    public URL resolveAudioStream(URL var1) {
        String var4 = var1.toString();
        String var5 = System.getProperty("user.home");
        YoutubeDLRequest request = new YoutubeDLRequest(var4, var5);
        request.setOption("get-url");
        request.setOption("no-check-certificate");
        request.setOption("rm-cache-dir");
        request.setOption("retries", "10");
        request.setOption("format", "18");

        try {
            YoutubeDL.setExecutablePath(this.prepareYtDlpExecutable());
            YoutubeDLResponse var7 = YoutubeDL.execute(request);
            String var8 = var7.getOut();
            return new URL(var8);
        } catch (YoutubeDLException var9) {
            Client.getInstance().notificationManager.send(
                    new Notification("Failed to Play Song", "Check the logs for more details."));

            this.stopYtDlp();

            return null;
        } catch (MalformedURLException var10) {
            MinecraftUtil.addChatMessage("URL Error: " + var10.toString());
            var10.printStackTrace();

            Client.getInstance().notificationManager.send(
                    new Notification("Failed to Play Song", "Invalid URL encountered."));

            this.stopYtDlp();

            return null;
        }
    }

    private void stopYtDlp() {
        try {
            String fileName = Util.getOSType() == Util.OS.WINDOWS ? "yt-dlp.exe"
                    : Util.getOSType() == Util.OS.LINUX ? "yt-dlp_linux"
                    : "yt-dlp_macos";

            File ytDlpFile = new File(Client.getInstance().file + "/music/" + fileName);

            if (ytDlpFile.exists()) {
                ProcessBuilder pb = new ProcessBuilder("taskkill", "/F", "/IM", ytDlpFile.getName());
                pb.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getSongTitle() {
        return this.songTitle;
    }

    public Texture getSongThumbnail() {
        return this.songThumbnail;
    }

    public Texture getNotificationImage() {
        return this.notificationImage;
    }

    public int getDurationInt() {
        return (int) this.duration;
    }

    private void adjustAudioVolume(SourceDataLine var1, int var2) {
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

    public void doesExecutableExist(double var1) {
        this.field32168 = var1;
        this.totalDuration = (long) this.field32168;
        this.field32169 = true;
    }

    public boolean method24330() {
        File var3 = new File(Client.getInstance().file + "/music/yt-dlp");
        if (Util.getOSType() == Util.OS.WINDOWS) {
            var3 = new File(Client.getInstance().file + "/music/yt-dlp.exe");
        } else if (Util.getOSType() == Util.OS.LINUX) {
            var3 = new File(Client.getInstance().file + "/music/yt-dlp_linux");
        } else if (Util.getOSType() == Util.OS.OSX) {
            var3 = new File(Client.getInstance().file + "/music/yt-dlp_macos");
        }

        return var3.exists();
    }

    public void setupDownloadThread() {
        Client.getInstance().getLogger().setThreadName("Updating dependencies threaded");
        new Thread(this::download).start();
    }

    public void download() {
        if (!this.finished) {
            if (Util.getOSType() == Util.OS.WINDOWS || Util.getOSType() == Util.OS.OSX
                    || Util.getOSType() == Util.OS.LINUX) {
                File musicDir = new File(Client.getInstance().file + "/music/");
                musicDir.mkdirs();

                String fileName = Util.getOSType() == Util.OS.WINDOWS ? "yt-dlp.exe"
                        : Util.getOSType() == Util.OS.LINUX ? "yt-dlp_linux"
                        : "yt-dlp_macos";

                File targetFile = new File(Client.getInstance().file + "/music/" + fileName);

                String urlString = "https://github.com/yt-dlp/yt-dlp/releases/download/2025.01.26/" + fileName;
                try (BufferedInputStream in = new BufferedInputStream(new URL(urlString).openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                    finished = true;
                    System.out.println("Finished downloading yt-dlp");
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

    public String prepareYtDlpExecutable() {
        String fileName = Util.getOSType() == Util.OS.WINDOWS ? "yt-dlp.exe"
                : Util.getOSType() == Util.OS.LINUX ? "yt-dlp_linux"
                : "yt-dlp_macos";
        String var3 = Client.getInstance().file.getAbsolutePath() + "/music/" + fileName;
        if (Util.getOSType() != Util.OS.WINDOWS) {
            File var4 = new File(var3);
            var4.setExecutable(true);
        }

        return var3;
    }

    public boolean hasPython() {
        if (Util.getOSType() == Util.OS.WINDOWS) {
            return true;
        } else {
            File var3 = new File("/usr/local/bin/python");
            if (var3.exists()) {
                Process var4;

                try {
                    var4 = new ProcessBuilder("/usr/local/bin/python", "-V").start();
                    InputStream var5 = var4.getErrorStream();
                    InputStreamReader var6 = new InputStreamReader(var5);
                    BufferedReader bufferedReader = new BufferedReader(var6);

                    String version;
                    try {
                        while ((version = bufferedReader.readLine()) != null) {
                            if (version.contains("3.12.5")) {
                                return true;
                            }
                        }
                    } catch (IOException ignored) {
                    }
                } catch (IOException ignored) {
                }
            }

            return false;
        }
    }

    public boolean hasVCRedist() {
        if (Util.getOSType() != Util.OS.WINDOWS) {
            return true;
        } else {
            boolean var3 = false;

            try {
                var3 = Advapi32Util.registryGetIntValue(
                        WinReg.HKEY_LOCAL_MACHINE,
                        "SOFTWARE\\WOW6432Node\\Microsoft\\VisualStudio\\10.0\\VC\\VCRedist\\x86", "Installed") == 1;
            } catch (RuntimeException ignored) {
            }

            try {
                var3 = var3
                        || Advapi32Util.registryGetIntValue(WinReg.HKEY_LOCAL_MACHINE,
                        "SOFTWARE\\Microsoft\\VisualStudio\\10.0\\VC\\VCRedist\\x86", "Installed") == 1;
            } catch (RuntimeException ignored) {
            }

            return var3;
        }
    }
}
