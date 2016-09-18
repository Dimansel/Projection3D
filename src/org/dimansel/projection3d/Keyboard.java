package org.dimansel.projection3d;

import java.util.ArrayList;

public final class Keyboard {
    private static ArrayList<Integer> keys = new ArrayList<>();

    public static void addKey(int keyCode) {
        if (!keys.contains(keyCode)) keys.add(keyCode);
    }

    public static void removeKey(int keyCode) {
        keys.remove(Integer.valueOf(keyCode));
    }

    public static boolean isKeyDown(int keyCode) {
        return keys.contains(keyCode);
    }
}
