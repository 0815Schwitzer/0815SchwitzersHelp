package org.polyfrost.example.config;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.migration.VigilanceName;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.libs.checker.signature.qual.FieldDescriptor;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class TestConfig extends Config {

    private static TestConfig instance; // Singleton Instanz

    private static final transient String Addons = "Addons";
    private static final transient String WallHacks = "Wall hacks stuff";
    private static final transient String Dungeons = "Dungeons";







    //Bedwars


    @VigilanceName(name = "Player ESP", category = WallHacks, subcategory = "Player ESP")
    @Switch(name = "Player ESP", category = WallHacks, subcategory = "Player ESP")
    public boolean entitiy_ESP = true;

    @VigilanceName(name = "Color", category = WallHacks, subcategory = "Player ESP")
    @Color(name = "Color", category = WallHacks, subcategory = "Player ESP")
    public OneColor player_ESP_color = new OneColor(0, 100, 100, 255);

    @VigilanceName(name = "Bed ESP", category = WallHacks, subcategory = "Bed ESP")
    @Switch(name = "Bed ESP", category = WallHacks, subcategory = "Bed ESP")
    public boolean bed_ESP = true;

    @VigilanceName(name = "Color", category = WallHacks, subcategory = "Bed ESP")
    @Color(name = "Bed Color", category = WallHacks, subcategory = "Bed ESP")
    public OneColor bed_ESP_color = new OneColor(290, 100, 100, 255);

    @VigilanceName(name = "Bed Protection", category = WallHacks, subcategory = "Bed ESP")
    @Switch(name = "Bed Protection", category = WallHacks, subcategory = "Bed ESP")
    public boolean bed_protection = true;

    @VigilanceName(name = "Color", category = WallHacks, subcategory = "Bed ESP")
    @Color(name = "Obsidian Color", category = WallHacks, subcategory = "Bed ESP")
    public OneColor bedrock_color = new OneColor(0, 0, 0, 255);

    @VigilanceName(name = "Color", category = WallHacks, subcategory = "Bed ESP")
    @Color(name = "Endstone Color", category = WallHacks, subcategory = "Bed ESP")
    public OneColor endstone_color = new OneColor(50, 100, 100, 255);

    @VigilanceName(name = "Color", category = WallHacks, subcategory = "Bed ESP")
    @Color(name = "Wood Color", category = WallHacks, subcategory = "Bed ESP")
    public OneColor wood_color = new OneColor(25, 100, 80, 255);




    //Carneval

    //Dungeons

    @VigilanceName(name = "Key Mob ESP", category = Dungeons, subcategory = "Key Mobs ESP")

        @Switch(name = "Key Mobs ESP", category = Dungeons, subcategory = "Key Mob ESP")
        public boolean key_mob_esp = true;

        @Color(name = "Color of the Key Mobs", category = Dungeons, subcategory = "Key Mob ESP")
        public OneColor key_mob_esp_color = new OneColor(50, 100, 100, 255);

    @VigilanceName(name = "Auto requeue", category = Dungeons, subcategory = "Auto requeue")

        @Switch(name = "Auto requeue", category = Dungeons, subcategory = "Auto requeue")
        public boolean requeue = true;

        @Number(name = "Auto requeue (in ms)", category = Dungeons, subcategory = "Auto requeue", min = 0, max = 10000)
        public int requeue_timer;

        @Number(name = "Auto requeue randomnis (in ms)", category = Dungeons, subcategory = "Auto requeue", min = 0, max = 5000)
        public int requeue_timer_randomnis;

        @Dropdown(name = "Floor", category = Dungeons, subcategory = "Auto requeue", options = {"1", "2", "3", "4", "5", "6", "7"})
        public int floorString;

        @Switch(name = "Check for full Party", category = Dungeons, subcategory = "Auto requeue")
        public boolean ckeckPartySize = true;




    public TestConfig() {
        super(new Mod("Schwitzers help", ModType.PVP), "schwitzers_conf.json");

        // Initialisierung der Konfiguration
        initialize();
    }

    // Methode, um die einzige Instanz der Klasse zu erhalten
    public static synchronized TestConfig getInstance() {
        if (instance == null) {
            instance = new TestConfig();
        }
        return instance;
    }

    //Bedwars





    public boolean isEntitiy_ESP() {
        return entitiy_ESP;
    }

    public boolean isBett_ESP() {
        return bed_ESP;
    }

    public OneColor playerESP_color()
    {
        return player_ESP_color;
    }

    public OneColor getBed_ESP_color()
    {
        return bed_ESP_color;
    }

    public boolean isBed_protection()
    {
        return bed_protection;
    }

    public OneColor getBedrock_color()
    {
        return bedrock_color;
    }

    public OneColor getEntstone_color()
    {
        return endstone_color;
    }

    public OneColor getWood_color()
    {
        return wood_color;
    }

    public boolean getKey_mob_esp()
    {
        return key_mob_esp;
    }

    public OneColor getKey_mob_esp_color()
    {
        return key_mob_esp_color;
    }

    public boolean getrequeue()
    {
        return requeue;
    }

    public int getrequeueTimer() {
        return requeue_timer;
    }

    public int getRequeueTimerRandomnis() {
        return requeue_timer_randomnis;
    }

    public int getFloor() {
        return floorString;
    }

    public boolean getCheckPartySize()
    {
        return ckeckPartySize;
    }


}

