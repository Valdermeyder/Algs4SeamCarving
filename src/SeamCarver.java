import java.awt.*;
import java.util.Arrays;

public class SeamCarver {
    private static final int BORDER_ENERGY = 195075;
    private Picture picture;
    private EdgeWeightedDigraph verticalPictureGraph;
    private EdgeWeightedDigraph horizontalPictureGraph;

    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        createVerticalAndHorizontalGraphs();
    }               // create a seam carver object based on the given picture

    private void createVerticalAndHorizontalGraphs() {
        final int height = picture.height();
        final int width = picture.width();
        final int graphSize = height * width + 2;
        createVerticalPictureGraph(height, width, graphSize);
        createHorizontalPictureGraph(height, width, graphSize);
    }

    private void createVerticalPictureGraph(int height, int width, int graphSize) {
        verticalPictureGraph = new EdgeWeightedDigraph(graphSize);
        final int lastXIndexInRow = width - 1;
        final int firstVIndexInLastRow = graphSize - width;
        final int lastYIndex = height - 1;
        for (int x = 0; x < lastXIndexInRow; x++) {
            verticalPictureGraph.addEdge(new DirectedEdge(0, x + 1, energy(x, 0)));
            verticalPictureGraph.addEdge(new DirectedEdge(firstVIndexInLastRow + x, graphSize - 1, energy(x, lastYIndex)));
        }
        for (int y = 0; y < height - 1; y++) {
            final int firstVinRow = y * width + 1;
            final int firstWinRow = firstVinRow + width;
            final int currentYIndex = y + 1;
            verticalPictureGraph.addEdge(new DirectedEdge(firstVinRow, firstWinRow, energy(0, currentYIndex)));
            verticalPictureGraph.addEdge(new DirectedEdge(firstVinRow, firstWinRow + 1, energy(1, currentYIndex)));
            for (int x = 1; x < lastXIndexInRow; x++) {
                final int vInRow = firstVinRow + x;
                final int wInRow = firstWinRow + x;
                verticalPictureGraph.addEdge(new DirectedEdge(vInRow, wInRow - 1, energy(x - 1, currentYIndex)));
                verticalPictureGraph.addEdge(new DirectedEdge(vInRow, wInRow, energy(x, currentYIndex)));
                verticalPictureGraph.addEdge(new DirectedEdge(vInRow, wInRow + 1, energy(x + 1, currentYIndex)));
            }
            final int lastVinRow = firstVinRow + lastXIndexInRow;
            final int lastWinRow = firstWinRow + lastXIndexInRow;
            verticalPictureGraph.addEdge(new DirectedEdge(lastVinRow, lastWinRow - 1, energy(lastXIndexInRow - 1, currentYIndex)));
            verticalPictureGraph.addEdge(new DirectedEdge(lastVinRow, lastWinRow, energy(lastXIndexInRow, currentYIndex)));
        }
    }

    private void createHorizontalPictureGraph(int height, int width, int graphSize) {
        horizontalPictureGraph = new EdgeWeightedDigraph(graphSize);
        final int lastYIndexInColumn = height - 1;
        final int firstVIndexInLastColumn = graphSize - height;
        final int lastXIndex = width - 1;
        for (int y = 0; y < lastYIndexInColumn; y++) {
            horizontalPictureGraph.addEdge(new DirectedEdge(0, y + 1, energy(0, y)));
            horizontalPictureGraph.addEdge(new DirectedEdge(firstVIndexInLastColumn + y, graphSize - 1, energy(lastXIndex, y)));
        }
        for (int x = 0; x < width - 1; x++) {
            final int firstVInColumn = x * height + 1;
            final int firstWInColumn = firstVInColumn + height;
            final int currentXIndex = x + 1;
            horizontalPictureGraph.addEdge(new DirectedEdge(firstVInColumn, firstWInColumn, energy(currentXIndex, 0)));
            horizontalPictureGraph.addEdge(new DirectedEdge(firstVInColumn, firstWInColumn + 1, energy(currentXIndex, 1)));
            for (int y = 1; y < lastYIndexInColumn; y++) {
                final int vInRow = firstVInColumn + y;
                final int wInRow = firstWInColumn + y;
                horizontalPictureGraph.addEdge(new DirectedEdge(vInRow, wInRow - 1, energy(currentXIndex, y - 1)));
                horizontalPictureGraph.addEdge(new DirectedEdge(vInRow, wInRow, energy(currentXIndex, y)));
                horizontalPictureGraph.addEdge(new DirectedEdge(vInRow, wInRow + 1, energy(currentXIndex, y + 1)));
            }
            final int lastVinRow = firstVInColumn + lastYIndexInColumn;
            final int lastWinRow = firstWInColumn + lastYIndexInColumn;
            horizontalPictureGraph.addEdge(new DirectedEdge(lastVinRow, lastWinRow - 1, energy(currentXIndex, lastYIndexInColumn - 1)));
            horizontalPictureGraph.addEdge(new DirectedEdge(lastVinRow, lastWinRow, energy(currentXIndex, lastYIndexInColumn)));
        }
    }

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
        int gx = rightColor.getGreen() - leftColor.getGreen();
        int bx = rightColor.getBlue() - leftColor.getBlue();
        int ry = topColor.getRed() - bottomColor.getRed();
        int gy = topColor.getGreen() - bottomColor.getGreen();
        int by = topColor.getBlue() - bottomColor.getBlue();
        return rx * rx + gx * gx + bx * bx + ry * ry + gy * gy + by * by;
    }              // energy of pixel at column x and row y

    public int[] findHorizontalSeam() {
        AcyclicSP acyclicSP = new AcyclicSP(horizontalPictureGraph, 0);
        if (acyclicSP.hasPathTo(horizontalPictureGraph.V() - 1)) {
            final Iterable<DirectedEdge> horizontalSeam = acyclicSP.pathTo(horizontalPictureGraph.V() - 1);
            int[] horizontalSeamArray = new int[width() + 1];
            int index = 0;
            for (DirectedEdge directedEdge : horizontalSeam) {
                horizontalSeamArray[index++] = directedEdge.to();
            }
            final int lastIndexInSeamArray = width() - 1;
            for (int i = 1; i < lastIndexInSeamArray; i++) {
                horizontalSeamArray[i] = (horizontalSeamArray[i] - 1) % height();
            }
            horizontalSeamArray[0] = horizontalSeamArray[1] - 1;
            horizontalSeamArray[lastIndexInSeamArray] = horizontalSeamArray[lastIndexInSeamArray - 1] - 1;
            return Arrays.copyOf(horizontalSeamArray, width());
        }
        return null;
    }              // sequence of indices for horizontal seam

    public int[] findVerticalSeam() {
        AcyclicSP acyclicSP = new AcyclicSP(verticalPictureGraph, 0);
        if (acyclicSP.hasPathTo(verticalPictureGraph.V() - 1)) {
            final Iterable<DirectedEdge> verticalSeam = acyclicSP.pathTo(verticalPictureGraph.V() - 1);
            int[] verticalSeamArray = new int[height() + 1];
            int index = 0;
            for (DirectedEdge directedEdge : verticalSeam) {
                verticalSeamArray[index++] = directedEdge.to();
            }
            for (int i = 1; i < height() - 1; i++) {
                verticalSeamArray[i] = (verticalSeamArray[i] - 1) % width();
            }
            verticalSeamArray[0] = verticalSeamArray[1] - 1;
            verticalSeamArray[height() - 1] = verticalSeamArray[height() - 2] - 1;
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
        Picture newPicture = new Picture(width(), height() - 1);
        boolean seamReached;
        for (int x = 0; x < width(); x++) {
            seamReached = false;
            for (int y = 0; y < height(); y++) {
                if (seam[x] != y) {
                    if (seamReached) {
                        newPicture.set(x, y - 1, picture.get(x, y));
                    } else {
                        newPicture.set(x, y, picture.get(x, y));
                    }
                } else {
                    seamReached = true;
                }
            }
        }
        picture = newPicture;
        createVerticalAndHorizontalGraphs();
    }  // remove horizontal seam from current picture

    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new NullPointerException("seam is null");
        }
        if (seam.length > height() || seam.length <= 1) {
            throw new IllegalArgumentException("seem = " + seam.length);
        }
        Picture newPicture = new Picture(width() - 1, height());
        boolean seamReached;
        for (int y = 0; y < height(); y++) {
            seamReached = false;
            for (int x = 0; x < width(); x++) {
                if (seam[y] != x) {
                    if (seamReached) {
                        newPicture.set(x - 1, y, picture.get(x, y));
                    } else {
                        newPicture.set(x, y, picture.get(x, y));
                    }
                } else {
                    seamReached = true;
                }
            }
        }
        picture = newPicture;
        createVerticalAndHorizontalGraphs();
    }    // remove vertical seam from current picture
}
