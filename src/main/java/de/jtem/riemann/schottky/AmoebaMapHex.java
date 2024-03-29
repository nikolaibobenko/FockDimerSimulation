package de.jtem.riemann.schottky;

import de.jtem.mfc.field.Complex;

public class AmoebaMapHex extends AmoebaMap{
    
    boolean isDoubleCover;

    private boolean calculateBoundaryDerivatives = false;
    Complex[][] angleBoundaryDerivatives;

    AmoebaMapHex(SchottkyDimers schottky, Complex P0) {
        super(schottky, P0);

        isDoubleCover = (schottky.getAngles().length == 6);
        angleBoundaryDerivatives = new Complex[3][];
        for (int i = 0; i < angleBoundaryDerivatives.length; i++) {
            angleBoundaryDerivatives[i] = new Complex[schottky.getAngles()[i].length];
        }
    }

    AmoebaMapHex(SchottkyDimers schottky, Complex P0, double[] boundaryResidues) {
        this(schottky, P0);
        this.boundaryResidues = boundaryResidues;
    }


    @Override
    protected void calculateIncrements(final SchottkyGroupElement element) {
        if (isDoubleCover) {
            doubleCoverIncrements(element);
            if(calculateBoundaryDerivatives) {
                addBoundaryDerivates(element);
            }
        } else {
            simpleIncrements(element);
        }
        addResidues();
    }

    public Complex[][] calculateBoundaryDerivativesByAngles() {
        // calculates the derivatives with respect to all angle parameters of dXiBoundary at 0.
        for (int i = 0; i < angleBoundaryDerivatives.length; i++) {
            for (int j = 0; j < angleBoundaryDerivatives[i].length; j++) {
                angleBoundaryDerivatives[i][j] = new Complex();
            }
        }
        calculateBoundaryDerivatives = true;
        getDifferentials(new Complex(), schottky.acc);
        calculateBoundaryDerivatives = false;
        return angleBoundaryDerivatives.clone();
    }

    private void addBoundaryDerivates(final SchottkyGroupElement element) {
        Complex[][] angles = schottky.getAngles();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < angles[i].length; j++) {
                Complex summand = element.applyTo(new Complex()).minus(angles[i][j]).invert().pow(2).plus(element.applyTo(new Complex()).plus(angles[i][j]).invert().pow(2));
                summand.assignTimes(angles[i][j].times(new Complex(0, boundaryResidues[i])).times(element.applyDifferentialTo(new Complex())));
                angleBoundaryDerivatives[i][j].assignPlus(summand);
                // Complex summand = element.applyDifferentialTo(angles[i][j]).divide(element.applyTo(angles[i][j]).pow(2));
                // summand.assignTimes(angles[i][j].times(new Complex(0, boundaryResidues[i] * 2)));
                // angleBoundaryDerivatives[i][j].assignPlus(summand);
            }
        }
    }

    private void simpleIncrements(final SchottkyGroupElement element) {
        for (Complex angle : schottky.getAngles()[0]) {
            Complex diff = P.minus(element.applyTo(angle));
            dH.assignPlus(diff.invert());
            dH_Der.assignPlus(diff.pow(2).invert());
            // H.assignTimes(diff);
            // H_corr.assignTimes(sP0.minus(angle));
            H.assignPlus(diff.log());
            H_corr.assignPlus(sP0.minus(angle).log());
        }
          for (Complex angle : schottky.getAngles()[1]) {
            Complex diff = P.minus(element.applyTo(angle));
            dG.assignPlus(diff.invert());
            dG_Der.assignPlus(diff.pow(2).invert());
            // G.assignTimes(diff);
            // G_corr.assignTimes(sP0.minus(angle));
            G.assignPlus(diff.log());
            G_corr.assignPlus(sP0.minus(angle).log());
        }
          for (Complex angle : schottky.getAngles()[2]) {
            Complex diff = P.minus(element.applyTo(angle));
            dH.assignMinus(diff.invert());
            dG.assignMinus(diff.invert());
            dH_Der.assignMinus(diff.pow(2).invert());
            dG_Der.assignMinus(diff.pow(2).invert());
            // H.assignDivide(diff);
            // G.assignDivide(diff);
            // H_corr.assignDivide(sP0.minus(angle));
            // G_corr.assignDivide(sP0.minus(angle));
            H.assignMinus(diff.log());
            H_corr.assignMinus(sP0.minus(angle).log());
            G.assignMinus(diff.log());
            G_corr.assignMinus(sP0.minus(angle).log());
        }
    }

    private void doubleCoverIncrements(final SchottkyGroupElement element) {
        // sP0.assign(element.applyTo(P.invert().conjugate()));
        for (Complex angle : schottky.getAngles()[0]) {
            Complex diff = P.minus(element.applyTo(angle));
            dH.assignPlus(diff.invert());
            dH_Der.assignMinus(diff.pow(2).invert());
            // H.assignTimes(diff);
            // H_corr.assignTimes(sP0.minus(angle));
            H.assignPlus(diff.log());
            H_corr.assignPlus(P0.minus(element.applyTo(angle)).log());
            dK.assignPlus(diff.invert().times(boundaryResidues[0]));
            dK_Der.assignMinus(diff.pow(2).invert().times(boundaryResidues[0]));
        }
        for (Complex angle : schottky.getAngles()[1]) {
            Complex diff = P.minus(element.applyTo(angle));
            dG.assignPlus(diff.invert());
            dG_Der.assignMinus(diff.pow(2).invert());
            // G.assignTimes(diff);
            // G_corr.assignTimes(P0.minus(element.applyTo(angle)));
            G.assignPlus(diff.log());
            G_corr.assignPlus(P0.minus(element.applyTo(angle)).log());
            dK.assignPlus(diff.invert().times(boundaryResidues[1]));
            dK_Der.assignMinus(diff.pow(2).invert().times(boundaryResidues[1]));
        }
        for (Complex angle : schottky.getAngles()[2]) {
            Complex diff = P.minus(element.applyTo(angle));
            dH.assignMinus(diff.invert());
            dG.assignMinus(diff.invert());
            dH_Der.assignPlus(diff.pow(2).invert());
            dG_Der.assignPlus(diff.pow(2).invert());
            // H.assignDivide(diff);
            // G.assignDivide(diff);
            // H_corr.assignDivide(P0.minus(element.applyTo(angle)));
            // G_corr.assignDivide(P0.minus(element.applyTo(angle)));
            H.assignMinus(diff.log());
            H_corr.assignMinus(P0.minus(element.applyTo(angle)).log());
            G.assignMinus(diff.log());
            G_corr.assignMinus(P0.minus(element.applyTo(angle)).log());
            dK.assignPlus(diff.invert().times(boundaryResidues[2]));
            dK_Der.assignMinus(diff.pow(2).invert().times(boundaryResidues[2]));
        }
        for (Complex angle : schottky.getAngles()[3]) {
            Complex diff = P.minus(element.applyTo(angle));
            dH.assignPlus(diff.invert());
            dH_Der.assignMinus(diff.pow(2).invert());
            // H.assignTimes(diff);
            // H_corr.assignTimes(P0.minus(element.applyTo(angle)));
            H.assignPlus(diff.log());
            H_corr.assignPlus(P0.minus(element.applyTo(angle)).log());
            dK.assignPlus(diff.invert().times(boundaryResidues[3]));
            dK_Der.assignMinus(diff.pow(2).invert().times(boundaryResidues[3]));
        }
        for (Complex angle : schottky.getAngles()[4]) {
            Complex diff = P.minus(element.applyTo(angle));
            dG.assignPlus(diff.invert());
            dG_Der.assignMinus(diff.pow(2).invert());
            // G.assignTimes(diff);
            // G_corr.assignTimes(P0.minus(element.applyTo(angle)));
            G.assignPlus(diff.log());
            G_corr.assignPlus(P0.minus(element.applyTo(angle)).log());
            dK.assignPlus(diff.invert().times(boundaryResidues[4]));
            dK_Der.assignMinus(diff.pow(2).invert().times(boundaryResidues[4]));
        }
        for (Complex angle : schottky.getAngles()[5]) {
            Complex diff = P.minus(element.applyTo(angle));
            dH.assignMinus(diff.invert());
            dG.assignMinus(diff.invert());
            dH_Der.assignPlus(diff.pow(2).invert());
            dG_Der.assignPlus(diff.pow(2).invert());
            // H.assignDivide(diff);
            // G.assignDivide(diff);
            // H_corr.assignDivide(P0.minus(element.applyTo(angle)));
            // G_corr.assignDivide(P0.minus(element.applyTo(angle)));
            H.assignMinus(diff.log());
            H_corr.assignMinus(P0.minus(element.applyTo(angle)).log());
            G.assignMinus(diff.log());
            G_corr.assignMinus(P0.minus(element.applyTo(angle)).log());
            dK.assignPlus(diff.invert().times(boundaryResidues[5]));
            dK_Der.assignMinus(diff.pow(2).invert().times(boundaryResidues[5]));
        }
    }

    protected void addResidues() {
        // This assumes the unitary case.
        // Find all angles that are to to right of the path and add (residue*PI*i) to the corresponding integrals.
        Complex[][] angles = schottky.getAngles();
        double[] residues1 = {1, 0, -1, 1, 0, -1};
        double[] residues2 = {0, 1, -1, 0, 1, -1};
        // 1 or -1 multiplier depending on if our integration contour is moving upwards or downwards.
        double comingFromBottom = sP0.minus(sP).im > 0 ? 1 : -1;
        boolean isInsideCircle = (sP0.abs() <= 1) && (sP.abs() <= 1);
        boolean leftOfCircle = !isInsideCircle && ((sP0.re < 0) || (sP.re < 0)) && ((Math.abs(sP0.im) <= 1) || (Math.abs(sP.im) <= 1));

        

        for (int i = 0; i < angles.length; i++) {
            for (Complex angle : angles[i]) {
                boolean imBetween = ((angle.im < sP0.im) && (angle.im > sP.im)) || ((angle.im < sP.im) && (angle.im > sP0.im));
                boolean isOnRightSide = angle.re > 0;
                boolean includeAngle = false;
                if (leftOfCircle || isInsideCircle) {
                    includeAngle = imBetween;
                }
                if (isInsideCircle) {
                    includeAngle &= isOnRightSide;
                }
                if(includeAngle) {
                    xi1.assignPlus(new Complex(0, 2 * Math.PI * residues1[i] * comingFromBottom));
                    xi2.assignPlus(new Complex(0, 2 * Math.PI * residues2[i] * comingFromBottom));
                }
            }
        }
    }
}
