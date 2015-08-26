package org.dimansel.projection3d;

import java.util.ArrayList;

public class Keyboard {
    private static ArrayList<Integer> keys = new ArrayList<>();

    public static void addKey(int keyCode) {
        if (!keys.contains(keyCode)) keys.add(keyCode);
    }

    public static void removeKey(int keyCode) {
        Object o = keyCode;
        keys.remove(o);
    }

    public static boolean isKeyDown(int keyCode) {
        return keys.contains(keyCode);
    }
}
