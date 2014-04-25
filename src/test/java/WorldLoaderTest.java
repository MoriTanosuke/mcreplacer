import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.Tag;
import de.kopis.minecraft.RegionPrinter;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WorldLoaderTest {
    private final String baseDir = "src/test/resources/saves";
    private final String worldName = "TestWorld";

    @Test
    public void worldCanBeLoaded() {
        CompoundTag r = getDataTagFor(new File(baseDir), worldName);
        assertNotNull(r);
        assertEquals("Wrong worldName name", worldName, r.getString("LevelName"));
        for(Tag tag : r.getAllTags()) {
            System.out.println(tag.getName());
        }
    }

    @Test
    public void regionCanBeLoaded() throws IOException {
        final RegionPrinter regionPrinter = new RegionPrinter();
        final List<File> regionFiles = regionPrinter.getRegionFiles("src/test/resources/saves", worldName);
        assertEquals("Unexpected number of regions: " + regionFiles.size(),
                2, regionFiles.size());
        //regionPrinter.printRegions(regionFiles);
    }

    private CompoundTag getDataTagFor(File baseDir, String levelId) {
        File dir = new File(baseDir, levelId);
        if (!dir.exists()) return null;

        File dataFile = new File(dir, "level.dat");
        if (dataFile.exists()) {
            try {
                CompoundTag root = NbtIo.readCompressed(new FileInputStream(dataFile));
                CompoundTag tag = root.getCompound("Data");
                return tag;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        dataFile = new File(dir, "level.dat_old");
        if (dataFile.exists()) {
            try {
                CompoundTag root = NbtIo.readCompressed(new FileInputStream(dataFile));
                CompoundTag tag = root.getCompound("Data");
                return tag;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
