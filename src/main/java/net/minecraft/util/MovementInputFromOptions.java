package net.minecraft.util;

import com.mentalfrostbyte.jello.event.impl.player.movement.EventMoveInput;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventMoveButton;
import net.minecraft.client.GameSettings;
import team.sdhq.eventBus.EventBus;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings var1) {
        this.gameSettings = var1;
    }


    public void tickMovement(boolean var1) {
        moveForward = 0.0f;
        moveStrafe = 0.0f;

        final EventMoveButton eventMoveButton = new EventMoveButton(
                this.gameSettings.keyBindForward.isKeyDown(),
                this.gameSettings.keyBindBack.isKeyDown(),
                this.gameSettings.keyBindLeft.isKeyDown(),
                this.gameSettings.keyBindRight.isKeyDown(),
                this.gameSettings.keyBindJump.isKeyDown(),
                this.gameSettings.keyBindSneak.isKeyDown()
        );
        EventBus.call(eventMoveButton);

        if (eventMoveButton.isForward()) {
            ++this.moveForward;
        }

        if (eventMoveButton.isBack()) {
            --this.moveForward;
        }

        if (eventMoveButton.isLeft()) {
            ++this.moveStrafe;
        }

        if (eventMoveButton.isRight()) {
            --this.moveStrafe;
        }

        this.jump = eventMoveButton.isJump();
        this.sneaking = eventMoveButton.isSneak();

        final EventMoveInput eventMoveInput = new EventMoveInput(this.moveForward, this.moveStrafe, this.jump, this.sneaking, 0.3F);
        EventBus.register(eventMoveInput);

        this.moveStrafe = eventMoveInput.getStrafe();
        this.moveForward = eventMoveInput.getForward();

        this.jump = eventMoveInput.isJumping();
        this.sneaking = eventMoveInput.isSneaking();

        if (this.sneaking) {
            this.moveStrafe *= eventMoveInput.getSneakFactor();
            this.moveForward *= eventMoveInput.getSneakFactor();
        }
    }
}
