package com.itedya.kasixmc.utils;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ClipboardUtil {
    public static Clipboard loadClipboard(String fileName) {
        File file = new File(PathUtil.getDataFilePath("schematics/" + fileName));

        Clipboard clipboard = null;

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try {
            ClipboardReader reader = format.getReader(new FileInputStream(file));
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clipboard;
    }
}
