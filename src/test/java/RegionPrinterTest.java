import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import de.kopis.minecraft.RegionPrinter;
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
                regionFiles.get(0), 12, 3);
        assertEquals("Unexpected number of sections found", 1, sections.size());

        printSection((CompoundTag) sections.get(0).get(0));
    }

    byte Nibble4(byte[] arr, int index) {
        return (byte) (index % 2 == 0 ? arr[index / 2] & 0x0F : (arr[index / 2] >> 4) & 0x0F);
    }

    private void printSection(CompoundTag section) {
        byte[] data = section.getByteArray("Data");
        byte[] blocks = section.getByteArray("Blocks");
        byte[] skyLight = section.getByteArray("SkyLight");
        byte[] blockLight = section.getByteArray("BlockLight");
        byte[] add = section.getByteArray("Add");
        byte y = section.getByte("Y");
        printBlock(data, blocks, add);
    }

    private void printBlock(byte[] data, byte[] blocks, byte[] add) {
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int blockPos = y * 16 * 16 + z * 16 + x;
                    byte blockIdA = blocks[blockPos];
                    byte blockIdB = 0;
                    short blockId = (short) (blockIdA);
                    if (add != null && add.length > 0) {
                        blockId += (blockIdB << 8);
                    }
                    byte blockData = Nibble4(data, blockPos);

                    System.out.println(String.format("%d %d %d: id=%d data=%d", y, z, x, blockId, blockData));
                    if (blockId == 56) {
                        System.out.println("DIAMONDS!");
                    }
                }
            }
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
