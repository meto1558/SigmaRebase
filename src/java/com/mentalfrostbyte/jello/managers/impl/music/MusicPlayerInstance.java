package com.mentalfrostbyte.jello.managers.impl.music;

import com.mentalfrostbyte.jello.gui.impl.MusicPlayer;
import com.mentalfrostbyte.jello.gui.unmapped.ButtonPanel;
import com.mentalfrostbyte.jello.gui.unmapped.Class4286;
import com.mentalfrostbyte.jello.gui.unmapped.MusicTabs;
import com.mentalfrostbyte.jello.util.ColorHelper;
import com.mentalfrostbyte.jello.util.ResourceRegistry;

public class MusicPlayerInstance implements Runnable {
    public final MusicVideoManager thumbnail;
    public final ColorHelper colorHelper;
    public final MusicPlayer musicPlayer;
    public final MusicPlayer musicPlayer2;

    public MusicPlayerInstance(MusicPlayer var1, MusicVideoManager var2, ColorHelper var3, MusicPlayer var4) {
        this.musicPlayer2 = var1;
        this.thumbnail = var2;
        this.colorHelper = var3;
        this.musicPlayer = var4;
    }

    @Override
    public void run() {
        if (!MusicPlayer.method13206(this.musicPlayer2).method13231(this.thumbnail.videoId)) {
            ButtonPanel var3;
            MusicPlayer.method13206(this.musicPlayer2)
                    .addToList(
                            var3 = new ButtonPanel(
                                    MusicPlayer.method13206(this.musicPlayer2),
                                    this.thumbnail.videoId,
                                    0,
                                    MusicPlayer.method13206(this.musicPlayer2).getButton().getChildren().size() * MusicPlayer.method13207(this.musicPlayer2),
                                    MusicPlayer.method13208(this.musicPlayer2),
                                    MusicPlayer.method13207(this.musicPlayer2),
                                    this.colorHelper,
                                    this.thumbnail.name,
                                    ResourceRegistry.JelloLightFont14
                            )
                    );
            MusicTabs var4;
            this.musicPlayer
                    .addToList(
                            var4 = new MusicTabs(
                                    this.musicPlayer,
                                    this.thumbnail.videoId,
                                    MusicPlayer.method13208(this.musicPlayer2),
                                    0,
                                    this.musicPlayer.getWidthA() - MusicPlayer.method13208(this.musicPlayer2),
                                    this.musicPlayer.getHeightA() - MusicPlayer.method13209(this.musicPlayer2),
                                    ColorHelper.field27961,
                                    this.thumbnail.name
                            )
                    );
            var4.method13514(true);
            var4.setEnabled(false);
            var4.method13300(false);
            if (this.thumbnail.videoList != null) {
                for (int var5 = 0; var5 < this.thumbnail.videoList.size(); var5++) {
                    YoutubeVideoData var6 = this.thumbnail.videoList.get(var5);
                    Class4286 var7 = null;
                    int var8 = 65;
                    int var9 = 10;
                    if (!var4.method13231(this.thumbnail.videoId)) {
                        var4.addToList(
                                var7 = new Class4286(
                                        var4,
                                        var9 + var5 % 3 * 183 - (var5 % 3 <= 0 ? 0 : var9) - (var5 % 3 <= 1 ? 0 : var9),
                                        var8 + var9 + (var5 - var5 % 3) / 3 * 210,
                                        183,
                                        220,
                                        var6
                                )
                        );
                        var7.doThis((var3x, var4x) -> MusicPlayer.method13211(this.musicPlayer2, thumbnail, var6));
                    }
                }
            }

            var3.doThis((var2, var3x) -> MusicPlayer.method13210(this.musicPlayer2, var4));
        }
    }
}
