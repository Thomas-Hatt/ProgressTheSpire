package ProgressTheSpire.achievement_classes;

// Yoinked from Packmaster :)


import ProgressTheSpire.ProgressTheSpire;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.ui.buttons.ReturnToMenuButton;
import javassist.CtBehavior;

import java.io.IOException;

@SpirePatch(
        clz = DeathScreen.class,
        method = "update"
)
public class DeathScreenUnlockPatch {

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(DeathScreen  __instance) {
        ProgressTheSpire.logger.info("Checking for achievement unlocks!");


        // Check for victory-related achievements
        if (ProgressTheSpire.achievements.containsKey("first_victory")) {
            Achievement firstVictory = ProgressTheSpire.achievements.get("first_victory");
            if (!firstVictory.unlocked) {
                firstVictory.unlock();
                ProgressTheSpire.logger.info("Unlocked first victory achievement!");
            }
        }

        // Check for ascension-related achievements
        if (AbstractDungeon.ascensionLevel == 20 && AbstractDungeon.actNum >= 3) {
            if (ProgressTheSpire.achievements.containsKey("ascension_20_victory")) {
                Achievement ascension20Victory = ProgressTheSpire.achievements.get("ascension_20_victory");
                if (!ascension20Victory.unlocked) {
                    ascension20Victory.unlock();
                    ProgressTheSpire.logger.info("Unlocked Ascension 20 victory achievement!");
                }
            }
        }

        // Save the updated achievements
        try {
            ProgressTheSpire.SaveAchievements();
            ProgressTheSpire.logger.info("Saved updated achievements!");
        } catch (IOException e) {
            ProgressTheSpire.logger.error("Failed to save achievements: " + e.getMessage());
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(ReturnToMenuButton.class, "hide");
            return LineFinder.findAllInOrder(ctMethodToPatch, methodCallMatcher);
        }
    }
}
