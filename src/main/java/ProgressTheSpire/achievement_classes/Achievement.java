package ProgressTheSpire.achievement_classes;

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

    private void unlock() {
        unlocked = true;

        System.out.println("Achievement unlocked: " + description);
    }
}