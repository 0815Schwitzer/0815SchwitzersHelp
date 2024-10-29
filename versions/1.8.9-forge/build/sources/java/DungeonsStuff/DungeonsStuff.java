package DungeonsStuff;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    private int requeue_timer_randomness;
    private int floor;
    private int random_time;
    private boolean checkPartySize;
    private int initialDelay = 2000;
    private int partyCheckDelay = 1000;
    TestConfig config = TestConfig.getInstance();
    Random random = new Random();

    private boolean requeueGuiIsOpen = false;
    private boolean awaitingPartyListResponse = false;
    private final Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onGuiOpen(GuiOpenEvent event) {
        if (requeueGuiIsOpen) {
            if (event.gui instanceof GuiContainer) {
                GuiContainer gui = (GuiContainer) event.gui;

                if (gui.inventorySlots != null) {
                    clickSlot13(gui);
                }
            }
        }
    }

    private void attemptRequeueWithDelay() {
        scheduler.schedule(() -> {
            if (checkPartySize) {
                checkPartySizeWithDelay();
            } else {
                requeueIntoFloorWithDelay();
            }
        }, initialDelay, TimeUnit.MILLISECONDS);
    }

    private void checkPartySizeWithDelay() {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/p list");
        awaitingPartyListResponse = true;
    }

    private void requeueIntoFloorWithDelay() {
        // Wait again after party check, then proceed to requeue
        scheduler.schedule(this::requeueIntoFloor, partyCheckDelay, TimeUnit.MILLISECONDS);
    }

    private void clickSlot13(GuiContainer gui) {
        // Simulate a click on Slot 13 after a 1-second delay
        scheduler.schedule(() -> {
            Minecraft.getMinecraft().playerController.windowClick(
                    gui.inventorySlots.windowId, // Window ID
                    13,                          // Slot 13
                    0,                           // Left-click
                    0,                           // Click type 0 (pickup)
                    Minecraft.getMinecraft().thePlayer // Player performing the click
            );
        }, 1000 + random_time, TimeUnit.MILLISECONDS);

        requeueGuiIsOpen = false;
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        requeue = config.getrequeue();
        requeue_timer = config.getrequeueTimer();
        requeue_timer_randomness = config.getRequeueTimerRandomnis();
        floor = config.getFloor() + 1;
        checkPartySize = config.getCheckPartySize(); // New setting

        // Get the unformatted text of the chat message
        String message = event.message.getUnformattedText();

        if (requeue && awaitingPartyListResponse) {
            if (event.type != 0) {
                return; // Only process normal chat messages
            }

            // Check if this is the response to the /p list command
            if (message.contains("Party Members (") && message.contains(")")) {
                awaitingPartyListResponse = false;

                int startIndex = message.indexOf("Party Members (") + "Party Members (".length();
                int endIndex = message.indexOf(")", startIndex);

                try {
                    int currentPartySize = Integer.parseInt(message.substring(startIndex, endIndex));

                    // Proceed only if the party is full
                    if (currentPartySize >= 5) {
                        requeueIntoFloorWithDelay();
                    } else {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Party not full. Requeue aborted."));
                    }
                } catch (NumberFormatException e) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Failed to read party size. Requeue aborted."));
                }
            }
            return;
        }

        // Trigger the requeue sequence if the message indicates requeue is needed
        if (message.contains("to re-queue into")) {
            attemptRequeueWithDelay();
        }
    }

    private void requeueIntoFloor() {
        String floorString = getFloorString(floor);

        // Set delay with randomness
        random_time = 100 + random.nextInt(Math.max(1, requeue_timer_randomness - 100));
        int totalDelay = requeue_timer * 1000 + random_time;

        scheduler.schedule(() -> {
            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/joininstance catacombs_floor_" + floorString);
                requeueGuiIsOpen = true;
            }
        }, totalDelay, TimeUnit.MILLISECONDS);
    }

    private String getFloorString(int floor) {
        switch (floor) {
            case 1: return "one";
            case 2: return "two";
            case 3: return "three";
            case 4: return "four";
            case 5: return "five";
            case 6: return "six";
            case 7: return "seven";
            default: return "unknown";
        }
    }
}
