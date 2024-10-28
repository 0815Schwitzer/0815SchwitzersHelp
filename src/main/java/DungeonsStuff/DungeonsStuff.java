package DungeonsStuff;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
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
    TestConfig config = TestConfig.getInstance();
    Random random = new Random();

    private boolean shouldCheckForOpenGui = false;

    private final Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onGuiOpen(GuiOpenEvent event) {
        if(shouldCheckForOpenGui)
        {
            if (event.gui instanceof GuiContainer) {
                GuiContainer gui = (GuiContainer) event.gui;

                if (gui.inventorySlots != null) {
                    clickSlot13(gui);
                }
            }
        }
    }

    private void clickSlot13(GuiContainer gui) {
        //check if slot 13 exists
        Slot slot13 = gui.inventorySlots.getSlot(14);
        if (slot13 != null && slot13.getHasStack()) {
            String itemName = slot13.getStack().getDisplayName().trim();
            if (!itemName.isEmpty()) {
                // simulate clicking on slot 13
                scheduler.schedule(() -> {
                    mc.playerController.windowClick(gui.inventorySlots.windowId, 13, 0, 0, mc.thePlayer); // Klick auf Slot 14
                }, 1000 + requeue_timer_randomness, TimeUnit.MILLISECONDS);
                shouldCheckForOpenGui = false;
            }
        }
    }

    // Chat event listener for re-queueing based on messages
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        requeue = config.getrequeue();
        requeue_timer = config.getrequeueTimer();
        requeue_timer_randomness = config.getRequeueTimerRandomnis();
        floor = config.getFloor() + 1;

        if (requeue) {
            if (event.type != 0) {
                return; // Only react to normal chat messages, not system messages
            }

            String message = event.message.getUnformattedText();

            if (message.contains("to re-queue into")) {
                String floorString = getFloorString(floor);

                // Set delay with randomness
                random_time = 100 + random.nextInt(Math.max(1, requeue_timer_randomness - 100));
                int totalDelay = requeue_timer * 1000 + random_time;

                scheduler.schedule(() -> {
                    if (Minecraft.getMinecraft().thePlayer != null) {
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/joininstance catacombs_floor_" + floorString);
                        shouldCheckForOpenGui = true;
                    }
                }, totalDelay, TimeUnit.MILLISECONDS);
            }
        }
    }

    // Utility function to get floor string
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
