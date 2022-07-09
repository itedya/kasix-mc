package com.itedya.skymaster.utils;

public class SkyMasterStringUtil {
//    https://stackoverflow.com/a/40435587
    public static int getIntFromEnd(String string) {
        for (int a = string.length() - 1; a >= 0; a--)
            try {
                int result = Integer.parseInt(string.substring(a));
                // the whole string is integer
                if (a == 0) return result;
            } catch (Exception e) {
                // there is no numbers at the end
                if (a == string.length() - 1) break;
                return Integer.parseInt(string.substring(a + 1));
            }
        // there is no numbers
        return -1;
    }
}
