package de.kopis.minecraft;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import net.minecraft.world.level.chunk.storage.RegionFile;

import java.io.DataInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegionPrinter {
    public void printRegions(String baseDir, String worldName) throws IOException {
        printRegions(getRegionFiles(baseDir, worldName));
    }

    public void printRegions(List<File> regionFiles) throws IOException {
        for (File f : regionFiles) {
            printRegion(f);
        }
    }

    private void printRegion(File f) throws IOException {
        RegionFile region = new RegionFile(f);
        for (int x = 0; x < 32; x++) {
            for (int z = 0; z < 32; z++) {
                try (DataInputStream inputStream = region.getChunkDataInputStream(x, z)) {
                    if (inputStream != null) {
                        System.out.println(String.format("CHUNK %d %d", x, z));
                        CompoundTag chunkData = NbtIo.read(inputStream);
                        chunkData.print(System.out);
                    } else {
                        System.out.println(String.format("No data found in chunk %d %d", x, z));
                    }
                }
            }
        }
    }

    public List<File> getRegionFiles(String baseFolder, String worldName) {
        return addRegionFiles(new File(baseFolder), worldName);
    }

    private List<File> addRegionFiles(File baseFolder, String worldName) {
        final File regionFolder = new File(new File(baseFolder, worldName), "region");
        File[] list = regionFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(RegionFile.ANVIL_EXTENSION);
            }
        });

        final List<File> regionFiles = new ArrayList<File>();
        if (list != null) {
            for (File file : list) {
                regionFiles.add(file);
            }
        } else {
            System.err.println(String.format("No regions found in folder '%s'!", regionFolder.getAbsolutePath()));
        }

        return regionFiles;
    }

    public List<ListTag> getSectionsFromChunk(File regionFile, int x, int z) throws IOException {
        List<ListTag> blocks = new ArrayList<>();

        RegionFile region = new RegionFile(regionFile);
        try (DataInputStream inputStream = region.getChunkDataInputStream(x, z)) {
            if (inputStream != null) {
                System.out.println(String.format("CHUNK %d %d", x, z));
                CompoundTag chunkData = NbtIo.read(inputStream);
                blocks.add(chunkData.getCompound("Level").getList("Sections"));
            } else {
                System.out.println(String.format("No data found in chunk %d %d", x, z));
            }
        }
        return blocks;
    }
}
