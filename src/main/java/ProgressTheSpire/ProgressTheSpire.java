package ProgressTheSpire;


import ProgressTheSpire.achievement_classes.Achievement;
import ProgressTheSpire.ui.MainMenuAchievementsViewButton;
import basemod.BaseMod;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import ProgressTheSpire.util.GeneralUtils;
import java.io.IOException;
import ProgressTheSpire.util.KeywordInfo;
import ProgressTheSpire.util.TextureLoader;
import ProgressTheSpire.patches.MainMenuExpansionReminderPatches;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.nio.charset.StandardCharsets;
import java.util.*;
@SuppressWarnings("unused")
@SpireInitializer
public class ProgressTheSpire implements
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        PostInitializeSubscriber {
    public static ModInfo info;
    public static String modID; //Edit your pom.xml to change this
    static { loadModInfo(); }
    private static final String resourcesFolder = checkResourcesPath();
    public static final Logger logger = LogManager.getLogger(modID); //Used to output to the console.

    public static SpireConfig modConfig = null;

    public static boolean initializedStrings = false;

    //This is used to prefix the IDs of various objects like cards and relics,
    //to avoid conflicts between different mods using the same name for things.
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    //This will be called by ModTheSpire because of the @SpireInitializer annotation at the top of the class.
    public static void initialize() {
        new ProgressTheSpire();

        try {
            Properties defaults = new Properties();
            defaults.put("AchievementsUnlocked", "");

            defaults.put("ProgressTheSpireEPSEEN","FALSE");

            initializeAchievements();
            modConfig = new SpireConfig(modID, "GeneralConfig", defaults);
            modConfig.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isEPSEEN() {

        if(modConfig == null) {
            System.out.println("-2-----------------2--");
            logger.info("modConfig is null, returning true for isEPSEEN");
            System.out.println("-2-----------------2--");
            return true;
        }

        boolean seen = modConfig.getBool("ProgressTheSpireEPSEEN");

        System.out.println("123---------------------");
        logger.info("isEPSEEN returning: " + seen);
        System.out.println("123---------------------");
        return seen;
    }


    public static void saveEPSEEN() {
        if(modConfig == null) return;
        try {
            ProgressTheSpire.modConfig.setBool("ProgressTheSpireEPSEEN", true);
            modConfig.save();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void receivePostInitialize() {
        //This loads the image used as an icon in the in-game mods menu.

        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));
        //Set up the mod information displayed in the in-game mods menu.
        //The information used is taken from your pom.xml file.

        initializedStrings = true;
        MainMenuAchievementsViewButton.initStrings();

        //If you want to set up a config panel, that will be done here.
        //You can find information about this on the BaseMod wiki page "Mod Config and Panel".
        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, null);
        initializeConfig();

    }

    private void initializeConfig() {
        UIStrings configStrings = CardCrawlGame.languagePack.getUIString(makeID("ConfigMenuText"));

        // Texture badge = TexLoader.getTexture(makeImagePath("ui/badge.png"));
    }

    // Achievements hashmap
    public static HashMap<String, Achievement> achievements = new HashMap<>();

    // Initialize achievements
    public static void initializeAchievements() {
        achievements.put("first_victory", new Achievement("first_victory", "Win your first run", false, 0, 1));
        achievements.put("ascension_20_victory", new Achievement("ascension_20_victory", "Win on Ascension 20", false, 0, 1));
        // Add other achievements here
    }

    public static void SaveAchievements() throws IOException {
        if (modConfig == null) return;

        ArrayList<String> unlocked = new ArrayList<>();
        ArrayList<String> locked = new ArrayList<>();

        for (Map.Entry<String, Achievement> entry : achievements.entrySet()) {
            Achievement achievement = entry.getValue();
            if (achievement.unlocked) {
                unlocked.add(achievement.key);
            } else {
                locked.add(achievement.key + ":" + achievement.progress);
            }
        }

        modConfig.setString("UnlockedAchievements", String.join(",", unlocked));
        modConfig.setString("LockedAchievements", String.join(",", locked));
        modConfig.save();
    }


    public static void LoadAchievements() {
        if (modConfig == null) return;

        String[] unlockedArray = modConfig.getString("UnlockedAchievements").split(",");
        String[] lockedArray = modConfig.getString("LockedAchievements").split(",");

        for (String key : unlockedArray) {
            if (achievements.containsKey(key)) {
                achievements.get(key).unlocked = true;
            }
        }

        for (String data : lockedArray) {
            String[] parts = data.split(":");
            String key = parts[0];
            int progress = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            if (achievements.containsKey(key)) {
                Achievement achievement = achievements.get(key);
                achievement.progress = progress;
            }
        }
    }


    // Unlocked achievements
    public static ArrayList<String> getUnlockedAchievements() {
      if (modConfig == null) return new ArrayList<>();
      return new ArrayList<>(Arrays.asList(modConfig.getString("AchievementsUnlocked").split(",")));
   }

    public static void saveUnlockedAchievements(ArrayList<String> input) throws IOException {
        if (modConfig == null) return;
        modConfig.setString("AchievementsUnlocked", String.join(",", input));
        modConfig.save();
    }

    public ProgressTheSpire() {
        BaseMod.subscribe(this); //This will make BaseMod trigger all the subscribers at their appropriate times.
        logger.info(modID + " subscribed to BaseMod.");
    }



    // System.out.println("----------------------");

    /*----------Paths----------*/

    public static String makePath(String resourcePath) {
        return resourcesFolder + "/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return resourcesFolder + "/images/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return resourcesFolder + "/images/relics/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return resourcesFolder + "/images/powers/" + resourcePath;
    }

    public static String makeCardPath(String resourcePath) {
        return resourcesFolder + "/images/cards/" + resourcePath;
    }

    public static String makeShaderPath(String resourcePath) {
        return resourcesFolder + "/shaders/" + resourcePath;
    }

    public static String makeOrbPath(String resourcePath) {
        return resourcesFolder + "/images/orbs/" + resourcePath;
    }


    /*----------Localization----------*/

    public static String imagePath(String file) {
        return resourcesFolder + "/images/" + file;
    }
    public static String characterPath(String file) {
        return resourcesFolder + "/images/character/" + file;
    }
    public static String powerPath(String file) {
        return resourcesFolder + "/images/powers/" + file;
    }
    public static String relicPath(String file) {
        return resourcesFolder + "/images/relics/" + file;
    }

    //This is used to load the appropriate localization files based on language.
    private static String getLangString()
    {
        return Settings.language.name().toLowerCase();
    }
    private static final String defaultLanguage = "eng";

    public static final Map<String, KeywordInfo> keywords = new HashMap<>();

    @Override
    public void receiveEditStrings() {
        /*
            First, load the default localization.
            Then, if the current language is different, attempt to load localization for that language.
            This results in the default localization being used for anything that might be missing.
            The same process is used to load keywords slightly below.
        */
        loadLocalization(defaultLanguage); //no exception catching for default localization; you better have at least one that works.
        if (!defaultLanguage.equals(getLangString())) {
            try {
                loadLocalization(getLangString());
            }
            catch (GdxRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadLocalization(String lang) {
        //While this does load every type of localization, most of these files are just outlines so that you can see how they're formatted.
        //Feel free to comment out/delete any that you don't end up using.
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                localizationPath(lang, "CharacterStrings.json"));
        BaseMod.loadCustomStringsFile(EventStrings.class,
                localizationPath(lang, "EventStrings.json"));
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                localizationPath(lang, "OrbStrings.json"));
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                localizationPath(lang, "PotionStrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                localizationPath(lang, "PowerStrings.json"));
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                localizationPath(lang, "RelicStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class, "ProgressTheSpire/localization/eng/UIStrings.json");
        logger.info("Loaded ProgressTheSpire's UIStrings.json for language: " + lang);
    }

    @Override
    public void receiveEditKeywords()
    {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(defaultLanguage, "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        KeywordInfo[] keywords = gson.fromJson(json, KeywordInfo[].class);
        for (KeywordInfo keyword : keywords) {
            keyword.prep();
            registerKeyword(keyword);
        }

        if (!defaultLanguage.equals(getLangString())) {
            try
            {
                json = Gdx.files.internal(localizationPath(getLangString(), "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
                keywords = gson.fromJson(json, KeywordInfo[].class);
                for (KeywordInfo keyword : keywords) {
                    keyword.prep();
                    registerKeyword(keyword);
                }
            }
            catch (Exception e)
            {
                logger.warn(modID + " does not support " + getLangString() + " keywords.");
            }
        }
    }

    private void registerKeyword(KeywordInfo info) {
        BaseMod.addKeyword(modID.toLowerCase(), info.PROPER_NAME, info.NAMES, info.DESCRIPTION);
        if (!info.ID.isEmpty())
        {
            keywords.put(info.ID, info);
        }
    }

    //These methods are used to generate the correct filepaths to various parts of the resources folder.
    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }



    /**
     * Checks the expected resources path based on the package name.
     */
    private static String checkResourcesPath() {
        String name = ProgressTheSpire.class.getName(); //getPackage can be iffy with patching, so class name is used instead.
        int separator = name.indexOf('.');
        if (separator > 0)
            name = name.substring(0, separator);

        FileHandle resources = new LwjglFileHandle(name, Files.FileType.Internal);

        if (!resources.exists()) {
            throw new RuntimeException("\n\tFailed to find resources folder; expected it to be named \"" + name + "\"." +
                    " Either make sure the folder under resources has the same name as your mod's package, or change the line\n" +
                    "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                    "\tat the top of the " + ProgressTheSpire.class.getSimpleName() + " java file.");
        }
        if (!resources.child("images").exists()) {
            throw new RuntimeException("\n\tFailed to find the 'images' folder in the mod's 'resources/" + name + "' folder; Make sure the " +
                    "images folder is in the correct location.");
        }
        if (!resources.child("localization").exists()) {
            throw new RuntimeException("\n\tFailed to find the 'localization' folder in the mod's 'resources/" + name + "' folder; Make sure the " +
                    "localization folder is in the correct location.");
        }

        return name;
    }

    /**
     * This determines the mod's ID based on information stored by ModTheSpire.
     */
    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo)->{
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(ProgressTheSpire.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        }
        else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }
}
