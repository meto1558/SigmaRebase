package com.mentalfrostbyte.jello.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender2DCustom;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender2DOffset;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRenderChat;
import com.mentalfrostbyte.jello.event.impl.player.EventPlayerTick;
import com.mentalfrostbyte.jello.managers.util.notifs.Notification;
import com.mentalfrostbyte.jello.util.client.ClientMode;
import com.mentalfrostbyte.jello.util.client.network.youtube.YoutubeContentType;
import com.mentalfrostbyte.jello.util.client.network.youtube.YoutubeUtil;
import com.mentalfrostbyte.jello.util.client.network.youtube.YoutubeVideoData;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import com.mentalfrostbyte.jello.util.system.network.ImageUtil;
import com.mentalfrostbyte.jello.util.system.sound.AudioRepeatMode;
import com.mentalfrostbyte.jello.util.system.sound.BasicAudioProcessor;
import com.mentalfrostbyte.jello.util.system.sound.MusicStream;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import com.tagtraum.jipes.math.FFTFactory;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.newdawn.slick.opengl.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.util.BufferedImageUtil;
import team.sdhq.eventBus.EventBus;
import team.sdhq.eventBus.annotations.EventTarget;

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
    private boolean processing = false;
    private transient volatile Thread audioThread = null;
    private int currentVideoIndex2;
    private long totalDuration = 0L;
    private int currentVideoIndex;
    private YoutubeVideoData currentVideo;
    private boolean spectrum = true;
    private AudioRepeatMode repeat = AudioRepeatMode.REPEAT;
    private boolean finished = false;
    private double field32168;
    private boolean field32169 = false;
    private double field32170 = 0.0;

    public void init() {
        EventBus.register(this);
        try {
            this.loadSettings();
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        }
        if (!this.doesYTDLPExist()) {
            this.setupDownloadThread();
        }

        this.finished = false;
    }

    public void saveMusicSettings() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("volume", this.volume);
        jsonObject.addProperty("spectrum", this.spectrum);
        jsonObject.addProperty("repeat", this.repeat.type);
        Client.getInstance().getConfig().add("music", jsonObject);
    }

    private void loadSettings() throws JsonParseException {
        if (Client.getInstance().getConfig().has("music")) {
            JsonObject jsonObject = Client.getInstance().getConfig().getAsJsonObject("music");
            if (jsonObject != null) {
                if (jsonObject.has("volume")) {
                    this.volume = Math.max(0, Math.min(100, jsonObject.get("volume").getAsInt()));
                }

                if (jsonObject.has("spectrum")) {
                    this.spectrum = jsonObject.get("spectrum").getAsBoolean();
                }

                if (jsonObject.has("repeat")) {
                    this.repeat = AudioRepeatMode.parseRepeat(jsonObject.get("repeat").getAsInt());
                }
            }
        }
    }

    @EventTarget
    public void onRender2D(EventRender2DOffset event) {
        if (Client.getInstance().clientMode == ClientMode.JELLO) {
            if (this.playing && !this.visualizerData.isEmpty()) {
                double[] var4 = this.visualizerData.get(0);
                if (this.amplitudes.isEmpty()) {
                    for (double v : var4) {
                        if (this.amplitudes.size() < 1024) {
                            this.amplitudes.add(v);
                        }
                    }
                }

                float fps = 60.0F / (float) Minecraft.getFps();

                for (int i = 0; i < var4.length; i++) {
                    double var7 = this.amplitudes.get(i) - var4[i];
                    boolean var9 = !(this.amplitudes.get(i) < Double.MAX_VALUE);
                    this.amplitudes.set(i, Math.min(2.256E7, Math.max(0.0, this.amplitudes.get(i) - var7 * (double) Math.min(0.335F * fps, 1.0F))));
                    if (var9) {
                        this.amplitudes.set(i, 0.0);
                    }
                }
            }
        }
    }

    @EventTarget
    public void onRenderChat(EventRenderChat eventRenderChat) {
        if (isPlayingSong())
            eventRenderChat.addOffset(-45);
    }

    @EventTarget
    public void onRender2D(EventRender2DCustom event) {
        if (this.playing && !this.visualizerData.isEmpty() && this.spectrum) {
            this.renderSpectrum();
        }
    }

    private void renderSpectrum() {
        if (!this.visualizerData.isEmpty()) {
            if (this.notificationImage != null) {
                if (!this.amplitudes.isEmpty()) {
                    float maxWidth = 114.0F;
                    float width = (float) Math.ceil((float) mc.getMainWindow().getWidth() / maxWidth);

                    for (int i = 0; (float) i < maxWidth; i++) {
                        float alphaValue = 1.0F - (float) (i + 1) / maxWidth;
                        float heightRatio = (float) mc.getMainWindow().getHeight() / 1080.0F;
                        float height = ((float) (Math.sqrt(this.amplitudes.get(i)) / 12.0) - 5.0F) * heightRatio;
                        RenderUtil.drawRoundedRect2(
                                (float) i * width,
                                (float) mc.getMainWindow().getHeight() - height,
                                width,
                                height,
                                RenderUtil2.applyAlpha(ClientColors.MID_GREY.getColor(), 0.2F * alphaValue)
                        );
                    }

                    RenderUtil.initStencilBuffer();

                    for (int i = 0; (float) i < maxWidth; i++) {
                        float heightRatio = (float) mc.getMainWindow().getHeight() / 1080.0F;
                        float height = ((float) (Math.sqrt(this.amplitudes.get(i)) / 12.0) - 5.0F) * heightRatio;
                        RenderUtil.drawRoundedRect2((float) i * width, (float) mc.getMainWindow().getHeight() - height, width, height, ClientColors.LIGHT_GREYISH_BLUE.getColor());
                    }

                    RenderUtil.configureStencilTest();
                    if (this.notificationImage != null && this.songThumbnail != null) {
                        RenderUtil.drawImage(0.0F, 0.0F, (float) mc.getMainWindow().getWidth(), (float) mc.getMainWindow().getHeight(), this.songThumbnail, 0.4F);
                    }

                    RenderUtil.restorePreviousStencilBuffer();
                    double var9 = 0.0;
                    float var16 = 4750;

                    for (int i = 0; i < 3; i++) {
                        var9 = Math.max(var9, Math.sqrt(this.amplitudes.get(i)) - 1000.0);
                    }

                    float scale = 1.0F + (float) Math.round((float) (var9 / (double) (var16 - 1000)) * 0.14F * 75.0F) / 75.0F;
                    GL11.glPushMatrix();
                    GL11.glTranslated(60.0, mc.getMainWindow().getHeight() - 55, 0.0);
                    GL11.glScalef(scale, scale, 0.0F);
                    GL11.glTranslated(-60.0, -(mc.getMainWindow().getHeight() - 55), 0.0);
                    RenderUtil.drawImage(10.0F, (float) (mc.getMainWindow().getHeight() - 110), 100.0F, 100.0F, this.notificationImage);
                    RenderUtil.drawRoundedRect(10.0F, (float) (mc.getMainWindow().getHeight() - 110), 100.0F, 100.0F, 14.0F, 0.3F);
                    GL11.glPopMatrix();
                    String[] titleSplit = this.songTitle.split(" - ");
                    if (titleSplit.length <= 1) {
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18_1,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 70),
                                titleSplit[0],
                                RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.5F)
                        );
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 70),
                                titleSplit[0],
                                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.7F)
                        );
                    } else {
                        RenderUtil.drawString(
                                ResourceRegistry.JelloMediumFont20_1,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 81),
                                titleSplit[0],
                                RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.4F)
                        );
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18_1,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 56),
                                titleSplit[1],
                                RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.5F)
                        );
                        RenderUtil.drawString(
                                ResourceRegistry.JelloLightFont18,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 56),
                                titleSplit[1],
                                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.7F)
                        );
                        RenderUtil.drawString(
                                ResourceRegistry.JelloMediumFont20,
                                130.0F,
                                (float) (mc.getMainWindow().getHeight() - 81),
                                titleSplit[0],
                                RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.6F)
                        );
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
            if (this.processing && this.thumbnailImage != null && this.scaledThumbnail != null && this.currentVideo == null && !mc.isGamePaused()) {
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
                this.processing = false;
            }
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }

        if (!this.processing) {
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

                        for (int index = this.currentVideoIndex; index < this.videoManager.videoList.size(); index++) {
                            URL songUrl = YoutubeUtil.getVideoStreamURL(this.videoManager.videoList.get(index).videoId);
                            Client.getInstance().getLogger().setThreadName(songUrl.toString());
                            this.currentVideoIndex2 = index;
                            this.currentVideo = this.videoManager.videoList.get(index);
                            this.visualizerData.clear();

                            while (!this.playing) {
                                try {
                                    Thread.sleep(300L);
                                } catch (final InterruptedException ignored) {
                                }

                                double[] var6 = new double[0];
                                this.visualizerData.clear();
                                if (Thread.interrupted()) {
                                    if (this.sourceDataLine != null) {
                                        this.sourceDataLine.close();
                                    }

                                    return;
                                }
                            }

                            try {
                                URL url = this.resolveAudioStream(songUrl);
                                Client.getInstance().getLogger().setThreadName(url == null ? "No stream" : url.toString());
                                if (url != null) {
                                    URLConnection connection = url.openConnection();
                                    connection.setConnectTimeout(14000);
                                    connection.setReadTimeout(14000);
                                    connection.setUseCaches(true);
                                    connection.setDoOutput(true);
                                    connection.setRequestProperty("Connection", "Keep-Alive");

                                    InputStream iS = connection.getInputStream();
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
                                            Thread.sleep(300L);
                                            this.visualizerData.clear();
                                            if (Thread.interrupted()) {
                                                this.sourceDataLine.close();
                                                return;
                                            }
                                        }

                                        Frame var18 = var13.readNextFrame();
                                        var15.decodeFrame(var18.getData(), var16);
                                        pcmBufferData = var16.getData();

                                        this.sourceDataLine.write(pcmBufferData, 0, pcmBufferData.length);
                                        float[] var29 = MathHelper.convertToPCMFloatArray(var16.getData(), var14);

                                        FFTFactory.JavaFFT var19 = new FFTFactory.JavaFFT(var29.length);

                                        float[][] var20 = var19.transform(var29);
                                        float[] var21 = var20[0];
                                        float[] var22 = var20[1];

                                        this.visualizerData.add(MathHelper.calculateAmplitudes(var21, var22));
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
                                                && (this.repeat == AudioRepeatMode.LOOP_CURRENT
                                                || this.repeat == AudioRepeatMode.REPEAT
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
                                } else {
                                    Thread.sleep(1000L);
                                }
                            } catch (IOException exc) {
                                if (exc.getMessage() != null && exc.getMessage().contains("403")) {
                                    System.out.println("installing");
                                    this.download();
                                }
                            } catch (LineUnavailableException | InterruptedException exc) {
                                throw new RuntimeException(exc);
                            }

                            if (this.repeat == AudioRepeatMode.LOOP_CURRENT) {
                                index--;
                            } else if (this.repeat == AudioRepeatMode.REPEAT && index == this.videoManager.videoList.size() - 1) {
                                index = -1;
                            } else if (this.repeat == AudioRepeatMode.NO_REPEAT) {
                                return;
                            }

                            if (index >= this.videoManager.videoList.size()) {
                                index = 0;
                            }
                        }
                    });
            this.audioThread.start();
        }
    }

    public void setRepeat(AudioRepeatMode repeatMode) {
        this.repeat = repeatMode;
        this.saveMusicSettings();
    }

    public AudioRepeatMode getRepeat() {
        return this.repeat;
    }

    public void processVideoThumbnail(YoutubeVideoData videoData) {
        try {
            this.processing = true;
            BufferedImage buffImage = ImageIO.read(new URL(videoData.fullUrl));
            this.thumbnailImage = ImageUtil.applyBlur(buffImage, 15);
            this.thumbnailImage = this.thumbnailImage
                    .getSubimage(0, (int) ((float) this.thumbnailImage.getHeight() * 0.75F), this.thumbnailImage.getWidth(),
                            (int) ((float) this.thumbnailImage.getHeight() * 0.2F));
            this.songTitle = videoData.title;
            if (buffImage.getHeight() != buffImage.getWidth()) {
                if (this.songTitle.contains("[NCS Release]")) {
                    this.scaledThumbnail = buffImage.getSubimage(1, 3, 170, 170);
                } else {
                    this.scaledThumbnail = buffImage.getSubimage(70, 0, 180, 180);
                }
            } else {
                this.scaledThumbnail = buffImage;
            }

            this.currentVideo = null;
        } catch (IOException | NumberFormatException var5) {
            throw new RuntimeException(var5);
        }
    }

    public void setPlaying(boolean playing) {
        if (!playing && this.sourceDataLine != null) {
            this.sourceDataLine.flush();
        }

        this.playing = playing;
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

    public void playSong(MusicVideoManager vidManager, YoutubeVideoData videoData) {
        if (vidManager == null) {
            vidManager = new MusicVideoManager("temp", "temp", YoutubeContentType.PLAYLIST);
            vidManager.videoList.add(videoData);
        }

        this.videoManager = vidManager;
        this.playing = true;
        this.totalDuration = 0L;
        this.field32170 = 0.0;

        for (int i = 0; i < vidManager.videoList.size(); i++) {
            if (vidManager.videoList.get(i) == videoData) {
                this.currentVideoIndex = i;
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

    public URL resolveAudioStream(URL songURL) {
        String songURLString = songURL.toString();
        String userHomeDir = System.getProperty("user.home");
        YoutubeDLRequest request = new YoutubeDLRequest(songURLString, userHomeDir);
        request.setOption("get-url");
        request.setOption("no-check-certificate", " ");
        request.setOption("rm-cache-dir", " ");
        request.setOption("retries", 10);
        request.setOption("format", 18);

        try {
            YoutubeDL.setExecutablePath(this.prepareYtDlpExecutable());
            YoutubeDLResponse response = YoutubeDL.execute(request);
            String responseString = response.getOut();
            return new URL(responseString);
        } catch (YoutubeDLException exception) {
            if (exception.getMessage() != null
                    && exception.getMessage().contains("ERROR: This video contains content from")
                    && exception.getMessage().contains("who has blocked it in your country on copyright grounds")) {
                Client.getInstance().notificationManager.send(new Notification("Now Playing", "Not available in your region."));
            } else {
                exception.printStackTrace();
                this.download();
            }
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }

        return null;
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

    public void setDuration(double duration) {
        this.field32168 = duration;
        this.totalDuration = (long) this.field32168;
        this.field32169 = true;
    }

    public boolean doesYTDLPExist() {
        File targetFile = new File(Client.getInstance().file + "/music/yt-dlp");
        if (Util.getOSType() == Util.OS.WINDOWS) {
            targetFile = new File(Client.getInstance().file + "/music/yt-dlp.exe");
        }

        return targetFile.exists();
    }

    public void setupDownloadThread() {
        Client.getInstance().getLogger().setThreadName("Updating dependencies threaded");
        new Thread(this::download).start();
    }

    public void download() {
        if (!this.finished) {
            File musicDir = new File(Client.getInstance().file + "/music/");
            musicDir.mkdirs();
            Client.getInstance().getLogger().setThreadName("Updating dependencies");
            if (Util.getOSType() == Util.OS.WINDOWS) {
                try {
                    File targetFile = new File(Client.getInstance().file + "/music/yt-dlp.exe");
                    CloseableHttpClient client = HttpClients.createDefault();
                    CloseableHttpResponse response = client.execute(new HttpGet("https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe"));
                    Throwable throwable = null;

                    try {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                                entity.writeTo(outputStream);
                            }
                        }
                    } catch (Throwable t) {
                        throwable = t;
                        throw t;
                    } finally {
                        if (response != null) {
                            if (throwable != null) {
                                try {
                                    response.close();
                                } catch (Throwable t) {
                                    throwable.addSuppressed(t);
                                }
                            } else {
                                response.close();
                            }
                        }
                    }
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            } else {
                try {
                    File targetFile = new File(Client.getInstance().file + "/music/yt-dlp");
                    CloseableHttpClient client = HttpClients.createDefault();
                    CloseableHttpResponse response = client.execute(new HttpGet("https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp"));
                    Throwable throwable = null;

                    try {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                                entity.writeTo(outputStream);
                            }
                        }
                    } catch (Throwable t) {
                        throwable = t;
                        throw t;
                    } finally {
                        if (response != null) {
                            if (throwable != null) {
                                try {
                                    response.close();
                                } catch (Throwable t) {
                                    throwable.addSuppressed(t);
                                }
                            } else {
                                response.close();
                            }
                        }
                    }
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }

            System.out.println("done");
            this.finished = true;
        }
    }

    public String prepareYtDlpExecutable() {
        String fileName = Client.getInstance().file.getAbsolutePath() + "/music/yt-dlp";
        if (Util.getOSType() != Util.OS.WINDOWS) {
            File targetFile = new File(fileName);
            targetFile.setExecutable(true);
        } else {
            fileName = fileName + ".exe";
        }

        return fileName;
    }

    public boolean hasPython() {
        if (Util.getOSType() == Util.OS.WINDOWS) {
            return true; // Windows yt-dlp doesn't require Python
        }

        String[][] commands = {{"python3", "-V"}, {"python", "-V"}};

        for (String[] cmd : commands) {
            try {
                Process process = new ProcessBuilder(cmd).start();

                // Read both stdout and stderr
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String version;
                while ((version = inputReader.readLine()) != null || (version = errorReader.readLine()) != null) {
                    if (version.contains("Python")) {
                        return true;
                    }
                }
            } catch (IOException ignored) {
                Client.getInstance().getLogger().warn("No Python version found!");
            }
        }

        return false;
    }

    public boolean hasVCRedist() {
        if (Util.getOSType() != Util.OS.WINDOWS) {
            return true; // Not on Windows, VC Redist is not on Linux/mac.
        }

        String[] redistKeys = {
                "SOFTWARE\\WOW6432Node\\Microsoft\\VisualStudio\\10.0\\VC\\VCRedist\\x86",
                "SOFTWARE\\Microsoft\\VisualStudio\\10.0\\VC\\VCRedist\\x86",
                "SOFTWARE\\WOW6432Node\\Microsoft\\VisualStudio\\14.0\\VC\\Runtimes\\x86",
                "SOFTWARE\\Microsoft\\VisualStudio\\14.0\\VC\\Runtimes\\x86",
                "SOFTWARE\\WOW6432Node\\Microsoft\\VisualStudio\\14.0\\VC\\Runtimes\\x64",
                "SOFTWARE\\Microsoft\\VisualStudio\\14.0\\VC\\Runtimes\\x64",
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\{D992A37A-AF08-45C4-9E49-D50EA5F46A16}_is1", // VC++ 2015-2019
                "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\{D992A37A-AF08-45C4-9E49-D50EA5F46A16}_is1"
        };

        for (String key : redistKeys) {
            try {
                if (Advapi32Util.registryGetIntValue(WinReg.HKEY_LOCAL_MACHINE, key, "Installed") == 1) {
                    return true;
                }
            } catch (Win32Exception ignored) {
                Client.getInstance().getLogger().warn("No VCRedist was found on your computer.");
            }
        }

        return false;
    }
}