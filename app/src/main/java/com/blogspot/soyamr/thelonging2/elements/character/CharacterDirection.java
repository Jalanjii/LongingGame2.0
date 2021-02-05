package com.blogspot.soyamr.thelonging2.elements.character;

import com.blogspot.soyamr.thelonging2.helpers.Utils;

import static com.blogspot.soyamr.thelonging2.helpers.Utils.BOTTOM_TO_TOP;
import static com.blogspot.soyamr.thelonging2.helpers.Utils.LEFT_TO_RIGHT;
import static com.blogspot.soyamr.thelonging2.helpers.Utils.RIGHT_TO_LEFT;
import static com.blogspot.soyamr.thelonging2.helpers.Utils.TOP_TO_BOTTOM;

class CharacterDirection {
    private VovaCharacter character;

    /*
     * coordinate on the surfaceView
     */
    int targetX;
    int targetY;
    int movingVectorX = 0;
    int movingVectorY = 0;

    CharacterDirection(VovaCharacter character) {
        this.character = character;
    }

    void reachedWall() {
        // When the game's character touches the edge of the screen, then change direction
        if (character.x < 0) {
            character.x = 0;
            movingVectorX = -movingVectorX;
        } else if (character.x > Utils.screenWidth - Utils.characterWidth) {
            character.x = Utils.screenWidth - Utils.characterWidth;
            movingVectorX = -movingVectorX;
        }

        if (character.y < 0) {
            character.y = 0;
            this.movingVectorY = -this.movingVectorY;
        } else if (character.y > Utils.screenHeight - Utils.characterHeight) {
            character.y = Utils.screenHeight - Utils.characterHeight;
            this.movingVectorY = -this.movingVectorY;
        }
    }

    //returns true to make the character stop calculations
    boolean checkObstacles() {
        if (movingVectorY == 0 &&
                movingVectorX == 0)
            return true;

        //check if reached the target point or not
        //then check if reached door
        //it's more efficient to check if reached door or not when the character stops than
        //checking in  each frame 
        if (hasReachedTargetPoint()) {
            stopCharacter();
            VovaCharacter.DIRECTION = character.rowUsing;
            character.controller.hasReachedDoor(character.x, character.y);
            character.controller.whereAmI(character.x + Utils.characterWidth, character.y);

            return true;
        }

        //if hit the floor end change direction
        if (character.controller.reachedFloorEnd(character.y + Utils.characterHeight)) {
            character.y = character.controller.getCurrentFloorY() - Utils.characterHeight;
//            movingVectorY = -movingVectorY;
            stopCharacter();
        }

        //avoid the chair
        reachedChair();
        return false;
    }

    private void reachedChair() {
        //now rowUsing contains the movement direction
        switch (getMovingDirection()) {
            case TOP_TO_BOTTOM:
            case BOTTOM_TO_TOP:
                if (character.controller
                        .steppingOnRoomObject(character.x + Utils.characterWidth / 2,
                                character.y + Utils.characterHeight)) {
                    movingVectorX = -movingVectorX;
                    movingVectorY = -movingVectorY;
                }
                break;
            case LEFT_TO_RIGHT: {//-->
                if (character.controller
                        .steppingOnRoomObject(character.x + Utils.characterWidth,
                                character.y + Utils.characterHeight))
                    movingVectorX = -movingVectorX;

                break;
            }
            case RIGHT_TO_LEFT: {//<--
                if (character.controller
                        .steppingOnRoomObject(character.x,
                                character.y + Utils.characterHeight)) {
                    character.x += 1;
                    stopCharacter();
                }

                break;
            }
        }
    }

    private void stopCharacter() {
        movingVectorX = movingVectorY = 0;
    }

    private boolean hasReachedTargetPoint() {
        int characterX = character.x + Utils.characterWidth;
        int characterY = character.y + Utils.characterHeight;

        switch (getMovingDirection()) {
            case TOP_TO_BOTTOM:
                if (characterY > targetY)
                    return true;
                break;
            case BOTTOM_TO_TOP:
                if (characterY < targetY)
                    return true;
                break;
            case LEFT_TO_RIGHT:
                if (characterX > targetX)
                    return true;
                break;
            case RIGHT_TO_LEFT:
                if (characterX < targetX)//solved little problem by subtracting 100 pixel
                    return true;
                break;
        }
        return false;
    }

    int getMovingDirection() {
        if (movingVectorX > 0) {
            if (movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY))
                return TOP_TO_BOTTOM;
            else if (movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY))
                return BOTTOM_TO_TOP;
            else
                return LEFT_TO_RIGHT;

        } else {
            if (movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY))
                return TOP_TO_BOTTOM;
            else if (movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY))
                return BOTTOM_TO_TOP;
            else
                return RIGHT_TO_LEFT;
        }
    }

}
