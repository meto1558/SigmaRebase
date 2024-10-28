package com.mentalfrostbyte.jello.util.unmapped;

import com.mentalfrostbyte.jello.util.render.unmapped.Renderer;

public final class Class8748 {
   public static void method31562() {
      try {
         Renderer.get().method18413();
      } catch (NullPointerException var3) {
         throw new RuntimeException(
            "OpenGL based resources (images, fonts, sprites etc) must be loaded as part of init() or the game loop. They cannot be loaded before initialisation."
         );
      }
   }
}
