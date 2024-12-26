package com.mentalfrostbyte.jello.gui.unmapped;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.client.gui.screen.Screen;
import totalcross.json.JSONException;
import totalcross.json.JSONObject;

public class TargetThing {
   private int key = -1;
   private Object target;

   public TargetThing(JSONObject var1) {
      this.method27050(var1);
   }

   public TargetThing(int var1, Module var2) {
      this.key = var1;
      this.target = var2;
   }

   public TargetThing(int var1, Class<? extends Screen> var2) {
      this.key = var1;
      this.target = var2;
   }

   public void method27050(JSONObject var1) {
      if (var1.has("target")) {
         try {
            if (var1.has("key")) {
               this.key = var1.getInt("key");
            }

            if (var1.has("type")) {
               String var4 = var1.getString("type");
               switch (var4) {
                  case "mod":
                     for (Module var7 : Client.getInstance().moduleManager.getModuleMap().values()) {
                        if (var1.getString("target").equals(var7.getName())) {
                           this.target = var7;
                        }
                     }
                  case "screen":
                     Class var8 = Client.getInstance().guiManager.method33477(var1.getString("target"));
                     if (var8 != null) {
                        this.target = var8;
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

   public void setKey(int var1) {
      this.key = var1;
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
         if (to instanceof TargetThing) {
            TargetThing other = (TargetThing)to;
            return this.getTarget().equals(other.getTarget());
         } else {
            return false;
         }
      } else {
         return true;
      }
   }
}
