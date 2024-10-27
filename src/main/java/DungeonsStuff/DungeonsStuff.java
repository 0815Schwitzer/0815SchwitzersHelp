package DungeonsStuff;

import cc.polyfrost.oneconfig.libs.checker.units.qual.C;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.polyfrost.example.config.TestConfig;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod(modid = "dungeonsStuff", version = "1.0")
public class DungeonsStuff {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private boolean requeue;
    private int requeue_timer;
    private int requeue_timer_randomnis;
    private int floor;

    int random_time;

    TestConfig config = TestConfig.getInstance();
    Random random = new Random();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        requeue = config.getrequeue();
        requeue_timer = config.getrequeueTimer();
        requeue_timer_randomnis = config.getRequeueTimerRandomnis();
        floor = config.getFloor() + 1;

        if(requeue) {
            // Einstellungen aus der Konfiguration laden

            if (event.type != 0) {
                return;  // Nur auf normale Chatnachrichten reagieren, kein Systemtext
            }

            // Nachricht ohne Kontrollcodes (Farbcodes) abfragen
            String message = StringUtils.stripControlCodes(event.message.getUnformattedText());

            if (message.contains("to re-queue into")) {

                String floor_String = null;

                switch (floor) {
                    case 1: floor_String = "one"; break;  // floor 2 entspricht "one"
                    case 2: floor_String = "two"; break;  // floor 3 entspricht "two"
                    case 3: floor_String = "three"; break; // floor 4 entspricht "three"
                    case 4: floor_String = "four"; break;  // floor 5 entspricht "four"
                    case 5: floor_String = "five"; break;  // floor 6 entspricht "five"
                    case 6: floor_String = "six"; break;
                    case 7: floor_String = "seven"; break;
                }

                // Zufällige Verzögerungszeit generieren
                random_time = 100 + random.nextInt(Math.max(1, requeue_timer_randomnis - 100));
                int totalDelay = requeue_timer * 1000 + random_time;

                // Verzögerte Ausführung des Befehls planen
                final String finalFloor_String = floor_String;
                scheduler.schedule(() -> {
                    if (Minecraft.getMinecraft().thePlayer != null) {
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/joininstance catacombs_floor_" + finalFloor_String);
                        clickChestSlot(0);
                    }
                }, totalDelay, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void clickChestSlot(int startSlot) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        // Berechne den Ziel-Slot (5 nach rechts und 2 nach unten)
        int targetSlot = startSlot + 5 + (9 * 2); // Hier wird angenommen, dass du bei 'startSlot' beginnst

        // Überprüfe, ob der Spieler existiert
        if (targetSlot >= 0 && targetSlot < player.openContainer.inventorySlots.size()) {
            // Sende den Klick-Paket an den Server
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C0EPacketClickWindow(0, targetSlot, 0, 0, player.inventory.getCurrentItem(), (short) 0));
        }
    }
}
