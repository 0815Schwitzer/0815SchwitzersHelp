package util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class Chat {

    Minecraft mc = Minecraft.getMinecraft();

    public void FormatedChatMessage(String message)
    {
        mc.thePlayer.addChatMessage(new ChatComponentText("§0[§cSchwitzersHelp§0]§f " + message));
    }

}
