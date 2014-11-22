import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SeamCarverTest {
    @Test
    public void test6to5Picture() {
        Picture picture = new Picture("6x5.png");
        SeamCarver seamCarver = new SeamCarver(picture);
        assertEquals("Border pixel should be 195075", 195075, seamCarver.energy(0, 0), 0);
        assertEquals("Border pixel should be 195075", 195075, seamCarver.energy(1, 0), 0);
        assertEquals("Border pixel should be 195075", 195075, seamCarver.energy(seamCarver.width() - 1, 0), 0);
        assertEquals("Border pixel should be 195075", 195075, seamCarver.energy(1, seamCarver.height() - 1), 0);
        assertEquals("2 2 pixel should be 61346", 61346, seamCarver.energy(2, 2), 0);
        assertArrayEquals("Vertical is wrong", new int[]{2, 3, 3, 3, 2}, seamCarver.findVerticalSeam());
    }

    @Test
    public void test3By4Picture() {
        Picture picture = new Picture(3, 4);
        picture.set(0, 0, new Color(255, 101, 51));
        picture.set(0, 1, new Color(255, 153, 51));
        picture.set(0, 2, new Color(255, 203, 51));
        picture.set(0, 3, new Color(255, 255, 51));
        picture.set(1, 0, new Color(255, 101, 153));
        picture.set(1, 1, new Color(255, 153, 153));
        picture.set(1, 2, new Color(255, 204, 153));
        picture.set(1, 3, new Color(255, 255, 153));
        picture.set(2, 0, new Color(255, 101, 255));
        picture.set(2, 1, new Color(255, 153, 255));
        picture.set(2, 2, new Color(255, 205, 255));
        picture.set(2, 3, new Color(255, 255, 255));
        SeamCarver seamCarver = new SeamCarver(picture);
        assertEquals("Border pixel should be 195075", 195075, seamCarver.energy(0, 0), 0);
        assertEquals("Border pixel should be 195075", 195075, seamCarver.energy(1, 0), 0);
        assertEquals("Border pixel should be 195075", 195075, seamCarver.energy(seamCarver.width() - 1, 0), 0);
        assertEquals("Border pixel should be 195075", 195075, seamCarver.energy(1, seamCarver.height() - 1), 0);
        assertEquals("1 2 pixel should be 61346", 52024, seamCarver.energy(1, 2), 0);
        assertEquals("1 1 pixel should be 61346", 52225.0, seamCarver.energy(1, 1), 0);
    }
}