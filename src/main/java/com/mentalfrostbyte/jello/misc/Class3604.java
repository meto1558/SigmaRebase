package com.mentalfrostbyte.jello.misc;

import com.mentalfrostbyte.jello.util.game.world.pathing.Path;
import com.mentalfrostbyte.jello.util.game.world.pathing.PathFinder;

import java.util.Comparator;

public class Class3604 implements Comparator<Path> {
    public final PathFinder field19567;

    public Class3604(PathFinder var1) {
        this.field19567 = var1;
    }

    public int compare(Path var1, Path var2) {
        return (int)(var1.method30357() + var1.method30364() - (var2.method30357() + var2.method30364()));
    }
}