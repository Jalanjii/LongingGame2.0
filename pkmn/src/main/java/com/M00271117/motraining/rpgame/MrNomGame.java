package com.M00271117.motraining.rpgame;

import com.M00271117.motraining.framework.Screen;
import com.M00271117.motraining.framework.impl.AndroidGame;

public class MrNomGame extends AndroidGame {
    @Override
    public Screen getStartScreen() {
        return new LoadingScreen(this);
    }
}