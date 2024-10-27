package DiscordStuff;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Mod(modid = "LoginNotifier", name = "Login Notifier", version = "1.0")
public class SendDiscordName {


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Webhook URL als konstante URL
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1299716468270960641/pIMAZQNHYZvAIvLIBTQUhYgLKFsCIi_FVy8s1JHr4sQN6MskTx9Aq7UrZEbez1HgNji7";
    private static final String USERNAME = "Login Bot";  // Optional: Benutzername für den Webhook
    private static final String AVATAR_URL = "https://example.com/avatar.png";  // Optional: Avatar-URL für den Webhoo

    private boolean hasLoggedIn = false; // Flag, um den Login-Zustand zu verfolgen
    private boolean sendOnce = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer; // Zugriff auf den aktuellen Spieler

        if (player != null) { // Überprüfen, ob der Spieler existiert
            NetHandlerPlayClient netHandler = mc.getNetHandler();

            if (netHandler != null && netHandler.getNetworkManager() != null) {
                if (netHandler.getNetworkManager().isChannelOpen() && mc.getCurrentServerData() != null) {
                    // Spieler ist mit einem Server verbunden
                    if (!hasLoggedIn) { // Überprüfen, ob der Spieler sich noch nicht eingeloggt hat
                        String serverAddress = mc.getCurrentServerData().serverIP;
                        String playerName = player.getName();

                        // Discord-Benachrichtigung senden
                        if(!sendOnce)
                        {
                            sendDiscordNotification(playerName, serverAddress);
                        }

                        // Setze das Flag, um anzuzeigen, dass der Spieler sich eingeloggt hat
                        hasLoggedIn = true;
                        sendOnce = true;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(PlayerEvent.PlayerLoggedOutEvent event) {
        // Setze das Flag zurück, wenn der Spieler die Welt verlässt
        hasLoggedIn = false;
        sendOnce = false;
    }

    public static void sendDiscordNotification(String playerName, String serverAddress) {
        try {
            // JSON-Objekt für die Nachricht erstellen
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("content", String.format("**%s** auf **%s**", playerName, serverAddress)); // Verwendung von Markdown zur Hervorhebung
            jsonObject.addProperty("username", USERNAME);
            jsonObject.addProperty("avatar_url", AVATAR_URL);

            // Verbindung zur Webhook-URL herstellen
            URL url = new URL(WEBHOOK_URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Java-DiscordWebhook");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            // Nachricht senden
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // Antwort vom Server erhalten
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_NO_CONTENT) {
                System.out.println("Nachricht erfolgreich an Discord gesendet!");
            } else {
                System.out.println("Fehler beim Senden der Nachricht. HTTP-Antwortcode: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Fehler beim Senden der Nachricht an Discord:");
            e.printStackTrace();
        }
    }
}
