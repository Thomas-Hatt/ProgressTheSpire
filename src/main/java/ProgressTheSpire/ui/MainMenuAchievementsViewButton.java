package ProgressTheSpire.ui;

import ProgressTheSpire.ProgressTheSpire;
import ProgressTheSpire.util.TexLoader;
import ProgressTheSpire.vfx.GlowParticle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.ui.buttons.Button;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

import static basemod.BaseMod.logger;

public class MainMenuAchievementsViewButton extends Button {
    private static String[] TEXT;
    public static final Texture tex = TexLoader.getTexture(ProgressTheSpire.makeImagePath("ui/MMReminderBtn.png"));
    private static final float PARTICLE_CD = 0.7f;

    private float particleTimer = PARTICLE_CD;
    private ArrayList<AbstractGameEffect> effects = new ArrayList<>();
    private ArrayList<AbstractGameEffect> glows = new ArrayList<>();

    private static final float TRANSPARENCY_LIMIT = 0.4f;
    private boolean fadingOut = false;
    private float transparency = 1f;

    public MainMenuAchievementsViewButton(float x, float y) {
        super(x, y, tex);
        hb = new Hitbox(x, y, tex.getWidth() * Settings.xScale, tex.getHeight() * Settings.yScale);
    }

    @Override
    public void update() {
        if (CardCrawlGame.mainMenuScreen.screen != MainMenuScreen.CurScreen.MAIN_MENU)
            return;

        fadeOutUpdate();
        super.update();
        updateParticles();

        if(pressed) {

        }

        if(hb.hovered) {
            TipHelper.renderGenericTip((float) InputHelper.mX - 350.0F * Settings.xScale,
                    (float) InputHelper.mY + 50.0F * Settings.yScale,
                    TEXT[0],
                    TEXT[1]
            );
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        glows.forEach(g -> g.render(sb));

        if (this.hb.hovered || pressed) {
            inactiveColor.a = transparency;
            sb.setColor(inactiveColor);
        } else {
            sb.setColor(activeColor);
        }

        sb.draw(
                tex,
                x,
                y,
                tex.getWidth() * Settings.scale,
                tex.getHeight() * Settings.scale
        );

        sb.setColor(Color.WHITE);

        if(pressed) {
            FontHelper.renderFontRightAligned(sb, FontHelper.buttonLabelFont, TEXT[2], hb.x, hb.cY, Settings.RED_TEXT_COLOR);
            return;
        }
        hb.render(sb);

        effects.forEach(e -> e.render(sb));
    }

    private static final float GLOW_TIME = 0.75f;
    private float glowtimer = 0;

    protected void updateGlow() {
        glows.forEach(AbstractGameEffect::update);
        glows.removeIf(g -> g.isDone);

        if(pressed) {
            return;
        }

        glowtimer -= Gdx.graphics.getDeltaTime();
        if(glowtimer <= 0) {
            glowtimer = GLOW_TIME;
            glows.add(new GlowParticle(tex, hb.cX, hb.cY, 0, 1.5f));
        }
    }

    protected void updateParticles() {
        effects.forEach(AbstractGameEffect::update);
        effects.removeIf(e -> e.isDone);

        if(pressed) {
            return;
        }

        this.particleTimer -= Gdx.graphics.getDeltaTime();
        if (this.particleTimer < 0.0F) {
            this.particleTimer = PARTICLE_CD;
            float expandMod = 5f * Settings.xScale;

            for (int i = 0; i < MathUtils.random(1, 3); i++) {
                float starX = hb.x - expandMod, endX = starX + hb.width + expandMod;
                float startY = hb.y, endY = startY + hb.height;

                //effects.add(new VictoryConfettiEffect(MathUtils.random(starX, endX), MathUtils.random(startY, endY), MathUtils.random(0.2f, 0.5f)));
            }
        }
    }

    protected void fadeOutUpdate() {
        if(fadingOut && transparency > TRANSPARENCY_LIMIT) {
            transparency -= Gdx.graphics.getDeltaTime() / 2f;
        }
    }

    public static void initStrings() {
        System.out.println("----------------------");
        logger.info("Initializing MainMenuAchievementsButton strings");

        if (CardCrawlGame.languagePack == null) {
            System.out.println("----------------------");
            logger.error("CardCrawlGame.languagePack is null");
            return;
        }

        String uiStringId = ProgressTheSpire.makeID("MainMenuAchievementsButton");
        System.out.println("----------------------");
        logger.info("Looking for UI string with ID: " + uiStringId);
        System.out.println("----------------------");

        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(uiStringId);
        if (uiStrings == null || uiStrings.TEXT == null) {
            System.out.println("----------------------");
            logger.error("Failed to load UI strings for MainMenuAchievementsButton");
            System.out.println("----------------------");
            return;
        }

        TEXT = uiStrings.TEXT;

        System.out.println("----------------------");
        logger.info("MainMenuAchievementsButton strings initialized successfully");
        System.out.println("----------------------");
    }
}