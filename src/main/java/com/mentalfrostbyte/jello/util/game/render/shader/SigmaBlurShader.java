package com.mentalfrostbyte.jello.util.game.render.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SigmaBlurShader implements IResourceManager {

    public @NotNull Set<String> getResourceNamespaces() {
        return Minecraft.getInstance().getResourceManager().getResourceNamespaces();
    }

    public @NotNull IResource getResource(ResourceLocation resource) throws IOException {
        return !resource.getPath().equals("jelloblur") ? Minecraft.getInstance().getResourceManager().getResource(resource) : new JelloBlurJSON();
    }

    public boolean hasResource(ResourceLocation resource) {
        return resource.getPath().equals("jelloblur") || Minecraft.getInstance().getResourceManager().hasResource(resource);
    }

    public @NotNull List<IResource> getAllResources(@NotNull ResourceLocation resource) throws IOException {
        return Minecraft.getInstance().getResourceManager().getAllResources(resource);
    }

    public @NotNull Collection<ResourceLocation> getAllResourceLocations(@NotNull String var1, @NotNull Predicate<String> var2) {
        return Minecraft.getInstance().getResourceManager().getAllResourceLocations(var1, var2);
    }

    @Override
    public Stream<IResourcePack> getResourcePackStream() {
        return null;
    }
}
