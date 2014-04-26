import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import de.kopis.minecraft.RegionPrinter;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RegionPrinterTest {
    private final String baseDir = "src/test/resources/saves";
    private final String worldName = "TestWorld";

    @Test
    public void worldCanBeLoaded() {
        CompoundTag r = getDataTagFor(new File(baseDir), worldName);
        assertNotNull(r);
        assertEquals("Wrong worldName name", worldName, r.getString("LevelName"));
        //for(Tag tag : r.getAllTags()) {
        //    System.out.println(tag.getName());
        //}
    }

    @Test
    public void blocksCanBeLoaded() throws IOException {
        final RegionPrinter regionPrinter = new RegionPrinter();
        final List<File> regionFiles = regionPrinter.getRegionFiles("src/test/resources/saves", worldName);
        assertEquals(2, regionFiles.size());
        final List<ListTag> sections = regionPrinter.getSectionsFromChunk(
                new RegionFile(regionFiles.get(0)), 12, 3);
        assertEquals("Unexpected number of sections found", 1, sections.size());
        //regionPrinter.printSection((CompoundTag) sections.get(0).get(0));
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
