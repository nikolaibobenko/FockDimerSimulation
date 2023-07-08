package dimerSim; 

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
// MyPanel extends JPanel, which will eventually be placed in a JFrame
public class GridPanel extends JPanel { 
    private BufferedImage paintImage;
    private BufferedImage weightsImage;

    private MarkovSimZ2 sim;

    private Color[] dimerColors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
    public int scaling = 4;

    public GridPanel(MarkovSimZ2 sim) {
        super();
        this.sim = sim;
        paintImage = new BufferedImage(sim.lattice.N * scaling, sim.lattice.M * scaling, BufferedImage.TYPE_3BYTE_BGR);
        weightsImage = new BufferedImage(sim.lattice.N * scaling, sim.lattice.M * scaling, BufferedImage.TYPE_3BYTE_BGR);
    }
    // custom painting is performed by the paintComponent method
    @Override
    public void paintComponent(Graphics g){
        // clear the previous painting
        super.paintComponent(g);
        // cast Graphics to Graphics2D
        g.drawImage(paintImage, 0, 0, null);
    }

    public void updatePaint() {
        Graphics2D g2 = paintImage.createGraphics();
        Graphics2D gWeights = weightsImage.createGraphics();

        double maxWeight = 0;
        for (int i = 0; i < sim.lattice.N; i++) {
            for (int j = 0; j < sim.lattice.M; j++) {
                maxWeight = Math.max(maxWeight, sim.lattice.flipFaceWeights[i][j]);
            }
        }
        gWeights.setColor(Color.WHITE);
        gWeights.drawString("maxWeight: " + maxWeight, 30, 30);

        for (int i = 0; i < sim.lattice.N; i++) {
            for (int j = 0; j < sim.lattice.M; j++) {
                if (sim.insideBoundary[i][j]) {
                    gWeights.setColor(new Color((float) (sim.lattice.flipFaceWeights[i][j]/maxWeight), 0f, 0f));
                    // sim.lattice.getUnflippedFaceWeight(i, j)
                    gWeights.fillRect(i * scaling, j * scaling, 1 * scaling, 1 * scaling);
                    for (int dir = 0; dir < dimerColors.length; dir++) {
                        int dimerType = getDimerType(new Index(i, j), dir);
                        if (dimerType != 4) {
                            g2.setColor(dimerColors[dimerType]);
                            g2.fill(getDominoRect(i, j, dir));
                            g2.setColor(Color.BLACK);
                            g2.draw(getDominoRect(i, j, dir));
                        }
                    }
                }
            }
        }

        g2.dispose();
        repaint();
    }
    
    public void save(String filePath) throws IOException{
        ImageIO.write(paintImage, "PNG", new File(filePath));
    }

    public void saveWeightsPic(String filePath) throws IOException {
        ImageIO.write(weightsImage, "PNG", new File(filePath));
    }

    private Rectangle getDominoRect(int i, int j, int dir) {
        boolean isHorizontal = (dir % 2) == 1;
        int width = scaling * (isHorizontal ? 2 : 1);
        int height = scaling * (isHorizontal ? 1 : 2);
        int x = scaling * (i - (dir != 2 ? 1 : 0));
        int y = scaling * (j + (dir == 3 ? 1 : 0));
        return new Rectangle(x, y, width, height);
    }

    private int getDimerType(Index coords, int dir) {
        if(sim.isDimer(coords, dir)) {
            if(coords.isEven()) {
                return dir;
            } else {
                return (dir + 2) % 4;
            }
        }
        return 4;
    }
}