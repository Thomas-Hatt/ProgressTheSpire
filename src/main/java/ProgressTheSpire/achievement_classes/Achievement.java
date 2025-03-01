package ProgressTheSpire.achievement_classes;

import java.io.IOException;
import ProgressTheSpire.ProgressTheSpire;

public class Achievement {
    public String key;
    public String description;
    public boolean unlocked;
    public int progress;
    public int target;

    public Achievement(String key, String description, boolean unlocked, int progress) {
        this.key = key;
        this.description = description;
        this.unlocked = unlocked;
        this.progress = progress;
        this.target = 0;
    }

    public Achievement(String key, String description, boolean unlocked, int progress, int target) {
        this.key = key;
        this.description = description;
        this.unlocked = unlocked;
        this.progress = progress;
        this.target = target;
    }

    public void updateProgress(int increment) {
        if (!unlocked) {
            progress += increment;

            if (progress >= target) {
                unlock();
            }
        }
    }

    public void unlock() {
        if (!unlocked) {
            unlocked = true;
            progress = target;
            System.out.println("-----------------------");
            System.out.println("Achievement unlocked: " + description);
            System.out.println("-----------------------");
            // You could add more unlock logic here, like displaying a notification
            try {
                ProgressTheSpire.SaveAchievements();
            } catch (IOException e) {
                ProgressTheSpire.logger.error("Failed to save achievements: " + e.getMessage());
            }
        }
    }
}