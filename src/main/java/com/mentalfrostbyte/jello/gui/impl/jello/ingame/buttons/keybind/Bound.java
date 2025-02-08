package com.mentalfrostbyte.jello.gui.impl.jello.ingame.buttons.keybind;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.client.gui.screen.Screen;
import totalcross.json.JSONException;
import totalcross.json.JSONObject;

public class Bound {
   private int key = -1;
   private Object target;

   public Bound(JSONObject json) {
      this.loadFromJSON(json);
   }

   public Bound(int key, Module target) {
      this.key = key;
      this.target = target;
   }

   public Bound(int key, Class<? extends Screen> target) {
      this.key = key;
      this.target = target;
   }

   public void loadFromJSON(JSONObject from) {
      if (from.has("target")) {
         try {
            if (from.has("key")) {
               this.key = from.getInt("key");
            }

            if (from.has("type")) {
               String var4 = from.getString("type");
               switch (var4) {
                  case "mod":
                     for (Module module : Client.getInstance().moduleManager.getModuleMap().values()) {
                        if (from.getString("target").equals(module.getName())) {
                           this.target = module;
                        }
                     }
                  case "screen":
                     Class screen = Client.getInstance().guiManager.method33477(from.getString("target"));
                     if (screen != null) {
                        this.target = screen;
                     }
               }
            }
         } catch (JSONException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public JSONObject getKeybindData() {
      JSONObject obj = new JSONObject();
       switch (KeybindTypesWrapper.values[this.getKeybindTypes().ordinal()]) {
          case 1:
             obj.put("type", "mod");
             obj.put("target", ((Module)this.target).getName());
             break;
          case 2:
             obj.put("type", "screen");
             obj.put("target", Client.getInstance().guiManager.getNameForTarget((Class<? extends Screen>)this.target));
       }

       obj.put("key", this.key);
       return obj;
   }

   public boolean hasTarget() {
      return this.target != null;
   }

   public int getKeybind() {
      return this.key;
   }

   public void setKey(int key) {
      this.key = key;
   }

   public KeybindTypes getKeybindTypes() {
      return !(this.target instanceof Module) ? KeybindTypes.SCREEN : KeybindTypes.MODULE;
   }

   public Object getTarget() {
      return this.target;
   }

   public Class<? extends Screen> getScreenTarget() {
      return (Class<? extends Screen>)this.target;
   }

   public Module getModuleTarget() {
      return (Module)this.target;
   }

   @Override
   public boolean equals(Object to) {
      if (to != this) {
         if (to instanceof Bound other) {
             return this.getTarget().equals(other.getTarget());
         } else {
            return false;
         }
      } else {
         return true;
      }
   }
}
