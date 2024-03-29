package dimerSim;

public class MarkovSimHexWorker extends MarkovSimWorker{

    private byte flippableW = 0b101010;
    private byte flippableE = 0b010101;

    public MarkovSimHexWorker(MarkovSimHex sim, int[] rowIndices) {
        super(sim, rowIndices);
    }

    public void cleanupStep() {
        int parity = cleanupParity;
        for (int index = 0; index < rowIndices.length; index++) {
            int i = rowIndices[index];
            for (int j = 0; j < sim.faceStates[i].length; j++) {
                if ((Math.floorMod(i - j, 3) == parity)) {
                    sim.consolidateFaceStateFromNeighbors(i, j);
                }
            }
        }
    }

    public void markovStep() {
        int parity = stepParity;
        for (int index = 0; index < rowIndices.length; index++) {
            int i = rowIndices[index];
            for (int j = 0; j < sim.faceStates[i].length; j++) {
                if ((Math.floorMod(i - j, 3) == parity) && sim.insideBoundary[i][j]) {
                    double upProp = sim.lattice.flipFaceWeights[i][j];
                    double prop = (sim.faceStates[i][j] == flippableE) ? upProp : 1/upProp;
                    prop *= sim.acceptanceRatioConstant;
                    if (rand.nextDouble() < prop) {
                        sim.flipFaceExclusive(i, j);
                    }
                }
            }
        }
    }
}