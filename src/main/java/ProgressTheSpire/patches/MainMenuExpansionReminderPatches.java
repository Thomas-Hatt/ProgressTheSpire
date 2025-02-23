package ProgressTheSpire.patches;

import ProgressTheSpire.ProgressTheSpire;
import ProgressTheSpire.ui.MainMenuAchievementsViewButton;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.scenes.TitleBackground;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import com.megacrit.cardcrawl.ui.buttons.Button;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import ProgressTheSpire.util.TexLoader;
import ProgressTheSpire.vfx.GlowParticle;

import java.util.ArrayList;

public class MainMenuExpansionReminderPatches {
    //Button logic

    public static Button reminderButton = new MainMenuAchievementsViewButton(
            Settings.WIDTH * 0.93f - ((MainMenuAchievementsViewButton.tex.getWidth() * Settings.xScale) /2f),
            Settings.HEIGHT * 0.35f - ((MainMenuAchievementsViewButton.tex.getHeight() * Settings.yScale) /2f)
    );

    @SpirePatch2(clz = TitleBackground.class, method = "render")
    public static class RenderPatch {
        @SpirePostfixPatch
        public static void patch(TitleBackground __instance, SpriteBatch sb) {
            reminderButton.render(sb);
            effects.forEach(e -> e.render(sb));
        }
    }

    private static ArrayList<AbstractGameEffect> effects = new ArrayList<>();
    private static final float GLOW_TIME = 1f;
    private static float glowtimer = 0;
    @SpirePatch2(clz = TitleBackground.class, method = "update")


    @SpirePatch2(clz = TitleBackground.class, method = "update")
    public static class UpdatePatch {
        @SpirePostfixPatch
        public static void patch(TitleBackground __instance) {
            reminderButton.update();
            effects.forEach(AbstractGameEffect::update);
            effects.removeIf(e -> e.isDone);
        }
    }

    private static Texture highlightImg;
    @SpirePatch2(clz = MenuButton.class, method = SpirePatch.CONSTRUCTOR)
    public static class GetTexture {
        @SpirePostfixPatch
        public static void patch() {
            if(highlightImg == null) {
                highlightImg = ReflectionHacks.getPrivateStatic(MenuButton.class, "highlightImg");
            }
        }
    }
}