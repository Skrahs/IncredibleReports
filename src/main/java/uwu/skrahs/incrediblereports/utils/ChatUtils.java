package uwu.skrahs.incrediblereports.utils;

import net.kyori.adventure.text.Component;

public class ChatUtils {

    public static Component color(String s){
        return Component.text(s.replace("&","§"));
    }
}
