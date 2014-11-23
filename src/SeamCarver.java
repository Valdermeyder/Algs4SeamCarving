import java.awt.*;
import java.util.Arrays;

public class SeamCarver {
    private static final int BORDER_ENERGY = 195075;
    private double[][] energyMatrix;
    private Color[][] colorsMatrix;

    public SeamCarver(Picture picture) {
        colorsMatrix = new Color[picture.height()][picture.width()];
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                colorsMatrix[i][j] = picture.get(j, i);
            }
        }
        createEnergyMatrix();
    }               // create a seam carver object based on the given picture

    private void createEnergyMatrix() {
        energyMatrix = new double[height()][width()];
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                energyMatrix[i][j] = calculateEnergy(j, i);
            }
        }
    }

    private EdgeWeightedDigraph createVerticalPictureGraph() {
        final int height = height();
        final int width = width();
        final int graphSize = height * width + 2;
        EdgeWeightedDigraph verticalPictureGraph = new EdgeWeightedDigraph(graphSize);
        final int lastXIndexInRow = width - 1;
        final int firstVIndexInLastRow = graphSize - width - 1;
        final int lastYIndex = height - 1;
        for (int x = 0; x < width; x++) {
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
        return verticalPictureGraph;
    }

    private EdgeWeightedDigraph createHorizontalPictureGraph() {
        final int height = height();
        final int width = width();
        final int graphSize = height * width + 2;
        EdgeWeightedDigraph horizontalPictureGraph = new EdgeWeightedDigraph(graphSize);
        final int lastYIndexInColumn = height - 1;
        final int firstVIndexInLastColumn = graphSize - height - 1;
        final int lastXIndex = width - 1;
        for (int y = 0; y < height; y++) {
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
        return horizontalPictureGraph;
    }

    public Picture picture() {
        Picture newPicture = new Picture(width(), height());
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                newPicture.set(x, y, colorsMatrix[y][x]);
            }
        }
        return newPicture;
    }                        // current picture

    public int width() {
        return colorsMatrix[0].length;
    }                          // width of current picture

    public int height() {
        return colorsMatrix.length;
    }                         // height of current picture

    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= width() || y >= height()) {
            throw new IndexOutOfBoundsException("x = " + x + "; y = " + y);
        }
        return energyMatrix[y][x];
    }              // energy of pixel at column x and row y

    private double calculateEnergy(int x, int y) {
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1) {
            return BORDER_ENERGY;
        }
        final Color leftColor = colorsMatrix[y][x - 1];
        final Color rightColor = colorsMatrix[y][x + 1];
        final Color topColor = colorsMatrix[y - 1][x];
        final Color bottomColor = colorsMatrix[y + 1][x];
        int rx = rightColor.getRed() - leftColor.getRed();
        int gx = rightColor.getGreen() - leftColor.getGreen();
        int bx = rightColor.getBlue() - leftColor.getBlue();
        int ry = topColor.getRed() - bottomColor.getRed();
        int gy = topColor.getGreen() - bottomColor.getGreen();
        int by = topColor.getBlue() - bottomColor.getBlue();
        return rx * rx + gx * gx + bx * bx + ry * ry + gy * gy + by * by;
    }

    public int[] findHorizontalSeam() {
        EdgeWeightedDigraph horizontalPictureGraph = createHorizontalPictureGraph();
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
            horizontalSeamArray[0] = horizontalSeamArray[1] == 0 ? 0 : horizontalSeamArray[1] - 1;
            horizontalSeamArray[lastIndexInSeamArray] = horizontalSeamArray[lastIndexInSeamArray - 1] == 0 ? 0 : horizontalSeamArray[lastIndexInSeamArray - 1] - 1;
            return Arrays.copyOf(horizontalSeamArray, width());
        }
        return null;
    }              // sequence of indices for horizontal seam

    public int[] findVerticalSeam() {
        EdgeWeightedDigraph verticalPictureGraph = createVerticalPictureGraph();
        AcyclicSP acyclicSP = new AcyclicSP(verticalPictureGraph, 0);
        if (acyclicSP.hasPathTo(verticalPictureGraph.V() - 1)) {
            final Iterable<DirectedEdge> verticalSeam = acyclicSP.pathTo(verticalPictureGraph.V() - 1);
            int[] verticalSeamArray = new int[height() + 1];
            int index = 0;
            for (DirectedEdge directedEdge : verticalSeam) {
                verticalSeamArray[index++] = directedEdge.to();
            }
            final int lastIndexInSeamArray = height() - 1;
            for (int i = 1; i < lastIndexInSeamArray; i++) {
                verticalSeamArray[i] = (verticalSeamArray[i] - 1) % width();
            }
            verticalSeamArray[0] = verticalSeamArray[1] == 0 ? 0 : verticalSeamArray[1] - 1;
            verticalSeamArray[lastIndexInSeamArray] = verticalSeamArray[lastIndexInSeamArray - 1] == 0 ? 0 : verticalSeamArray[lastIndexInSeamArray - 1] - 1;
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
        final int newHeight = height() - 1;
        for (int i = 0; i < seam.length; i++) {
            for (int j = seam[i]; j < newHeight; j++) {
                colorsMatrix[j][i] = colorsMatrix[j + 1][i];
            }
        }
        colorsMatrix = Arrays.copyOf(colorsMatrix, newHeight);
        createEnergyMatrix();
    }  // remove horizontal seam from current picture


    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new NullPointerException("seam is null");
        }
        if (seam.length > height() || seam.length <= 1) {
            throw new IllegalArgumentException("seem = " + seam.length);
        }
        for (int i = 0; i < seam.length; i++) {
            final int newWidth = energyMatrix[i].length - 1;
            System.arraycopy(colorsMatrix[i], seam[i] + 1, colorsMatrix[i], seam[i], newWidth - seam[i]);
            colorsMatrix[i] = Arrays.copyOf(colorsMatrix[i], newWidth);
        }
        createEnergyMatrix();
    }    // remove vertical seam from current picture
}
