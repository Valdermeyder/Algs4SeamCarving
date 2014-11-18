import java.awt.*;
import java.util.Arrays;

public class SeamCarver {
    private static final int BORDER_ENERGY = 195075;
    private Picture picture;
    private EdgeWeightedDigraph verticalPictureGraph;

    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        final int height = picture.height();
        final int width = picture.width();
        final int graphSize = height * width - width + 2;
        verticalPictureGraph = new EdgeWeightedDigraph(graphSize);
        final int lastYIndex = width - 1;
        final int firstPixelInLastRow = graphSize - width - 2;
        for (int i = 0; i < width; i++) {
            verticalPictureGraph.addEdge(new DirectedEdge(0, i + 1, energy(0, i)));
            verticalPictureGraph.addEdge(new DirectedEdge(firstPixelInLastRow + i, graphSize - 1, energy(height - 1, i)));
        }
        for (int i = 0; i < height - 2; i++) {
            final int currentXCoordinate = i * width + 1;
            final int firstYCoordinate = currentXCoordinate + width;
            verticalPictureGraph.addEdge(new DirectedEdge(currentXCoordinate, firstYCoordinate + 1, energy(i + 1, 1)));
            verticalPictureGraph.addEdge(new DirectedEdge(currentXCoordinate, firstYCoordinate, energy(i + 1, 0)));
            for (int j = 1; j < lastYIndex; j++) {
                verticalPictureGraph.addEdge(new DirectedEdge(currentXCoordinate, firstYCoordinate + j - 1, energy(i + 1, j)));
                verticalPictureGraph.addEdge(new DirectedEdge(currentXCoordinate, firstYCoordinate + j, energy(i + 1, j)));
                verticalPictureGraph.addEdge(new DirectedEdge(currentXCoordinate, firstYCoordinate + j + 1, energy(i + 1, j)));
            }
            verticalPictureGraph.addEdge(new DirectedEdge(currentXCoordinate, firstYCoordinate + lastYIndex - 1, energy(i + 1, width - 1)));
            verticalPictureGraph.addEdge(new DirectedEdge(currentXCoordinate, firstYCoordinate + lastYIndex, energy(i + 1, width - 1)));
        }
    }               // create a seam carver object based on the given picture

    public Picture picture() {
        return picture;
    }                        // current picture

    public int width() {
        return picture.width();
    }                          // width of current picture

    public int height() {
        return picture.height();
    }                         // height of current picture

    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= width() || y >= height()) {
            throw new IndexOutOfBoundsException("x = " + x + "; y = " + y);
        }
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1) {
            return BORDER_ENERGY;
        }
        final Color leftColor = picture.get(x - 1, y);
        final Color rightColor = picture.get(x + 1, y);
        final Color topColor = picture.get(x, y - 1);
        final Color bottomColor = picture.get(x, y + 1);
        int rx = rightColor.getRed() - leftColor.getRed();
        int gx = rightColor.getGreen() - rightColor.getGreen();
        int bx = rightColor.getBlue() - rightColor.getBlue();
        int ry = topColor.getRed() - bottomColor.getRed();
        int gy = topColor.getGreen() - bottomColor.getGreen();
        int by = topColor.getBlue() - bottomColor.getBlue();
        return rx * rx + gx * gx + bx * bx + ry * ry + gy * gy + by * by;
    }              // energy of pixel at column x and row y

    public int[] findHorizontalSeam() {
        return null;
    }              // sequence of indices for horizontal seam

    public int[] findVerticalSeam() {
        AcyclicSP acyclicSP = new AcyclicSP(verticalPictureGraph, 0);
        if (acyclicSP.hasPathTo(verticalPictureGraph.V() - 1)) {
            final Iterable<DirectedEdge> verticalSeam = acyclicSP.pathTo(verticalPictureGraph.V() - 1);
            int[] verticalSeamArray = new int[height()];
            int index = 0;
            for (DirectedEdge directedEdge : verticalSeam) {
                verticalSeamArray[index++] = directedEdge.to();
            }
            for (int i = 1; i < height() - 1; i++) {
                verticalSeamArray[i] = (verticalSeamArray[i] - 1) % width();
            }
            verticalSeamArray[0] = verticalSeamArray[1];
            verticalSeamArray[height() - 1] = verticalSeamArray[height() - 2];
            return Arrays.copyOf(verticalSeamArray, height());
        }
        return null;
    }               // sequence of indices for vertical seam

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new NullPointerException("seam is null");
        }
        if (seam.length > width() || seam.length <= 1) {
            throw new IllegalArgumentException("seem = " + seam.length);
        }
    }  // remove horizontal seam from current picture

    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new NullPointerException("seam is null");
        }
        if (seam.length > height() || seam.length <= 1) {
            throw new IllegalArgumentException("seem = " + seam.length);
        }
    }    // remove vertical seam from current picture
}
