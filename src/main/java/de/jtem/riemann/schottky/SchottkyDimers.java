package de.jtem.riemann.schottky;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jzy3d.plot3d.pipelines.NotImplementedException;

import de.jtem.mfc.field.Complex;

public class SchottkyDimers extends Schottky{


    public double[] angles;
    // uniformization is either 1,2, or 0. Stands for U1, U2 or other.
    public int uniformization;
    protected AmoebaMap amoebaMap;

    public SchottkyDimers(SchottkyData data, double[] angles) {
        super(data);
        this.angles = angles;
        uniformization = getUniformization();

        // TODO: P0 should be chosen such that it's in the fundamental domain.
        Complex P0 = new Complex(0, 1);

        amoebaMap = new AmoebaMap(this, P0);

    }

    public Complex amoebaMap(Complex P) throws Exception {
        if (!isInFundamentalDomain(P)) {
            throw new Exception("P needs to be in fundamental domain");
        }
        throw new NotImplementedException();
    }

    public Complex aztecMap(Complex P) throws Exception {
        if (!isInFundamentalDomain(P)) {
            throw new Exception("P needs to be in fundamental domain");
        }
        throw new NotImplementedException();
    }

    public Complex[][] parametrizeRealOvals(int numPointsPerSegment) {
        int numAngles = angles.length;
        Complex[][] points = new Complex[numAngles + numGenerators][];
        // exclusion interval only supports genus 1 for now

        double[] ABinterval = {0,0};
        if (uniformization == 1){
            ABinterval = new double[] {getCenterOfCircle(0, false).re - getRadius(0), getCenterOfCircle(0, true).re + getRadius(0)};
        }
        for (int i = 0; i < numAngles; i++) {
            points[i] = getPointArrayOnRealLine(angles[i], angles[(i+1) % numAngles], numPointsPerSegment, ABinterval);
        }
        for(int i = 0; i < numGenerators; i++){
            if (uniformization == 1) {
                points[i + numAngles] = getPointArrayOnRealLineExponential(getCenterOfCircle(i, false).re + getRadius(i), getCenterOfCircle(i, true).re - getRadius(i), numPointsPerSegment, new double[]{0., 0.});
            }
            else {
                points[i + numAngles] = getPointArrayOnCircle(getCenterOfCircle(i), getRadius(i) * 1.00001, numPointsPerSegment);
            }
        }

        return points;
    }

    public Complex[] getAngles() {
        Complex[] complexAngles = new Complex[angles.length];
        for (int i = 0; i < complexAngles.length; i++) {
            complexAngles[i] = new Complex(angles[i], 0);
        }
        return complexAngles;
    }

    public Complex[] getPointArrayOnRealLine(double a, double b, int numPoints, double[] excludingInterval){
        List<Complex> res = new LinkedList<Complex>();
        // take values from equidistant sampling on circle C(0, 1) and mapping [1, i, -1] via Moebius to [b, infty, a].
        double imPart = 0.000001;
        if (b < a) {
            double rotationAngle = Math.PI / numPoints;
            double theta = 0.0;
            for (int i = 0; i < numPoints; i++) {
                double phi_theta = Math.signum(theta - Math.PI / 2) * Math.sqrt((1 + Math.sin(theta)) / (1 - Math.sin(theta)));
                Complex point = new Complex((a-b) / 2 * phi_theta + ((a + b) / 2), imPart);
                if(point.re >= excludingInterval[1] || point.re <= excludingInterval[0]) {
                    if(isInFundamentalDomain(point)) {
                        res.add(point);
                    }
                }
                theta += rotationAngle;
            }
            // for (int i = numPoints/2; i < numPoints; i++) {
            //     res[i] = new Complex(b - Math.pow(numPoints - i, 2), 0);
            // }
        } else {
            for (int i = 0; i < numPoints; i++) {
                Complex point = new Complex(a + ((b - a) / numPoints * i), imPart);
                if (a < excludingInterval[0] && b > excludingInterval[1]) {
                    if (i < numPoints / 2) {
                        point = new Complex(a + ((excludingInterval[0] - a) / numPoints * 2 * i), imPart);
                    }
                    else {
                        point = new Complex(excludingInterval[1] + ((b - excludingInterval[1]) / numPoints * 2 * (i - numPoints / 2)), imPart);
                    }
                }
                if(point.re >= excludingInterval[1] || point.re <= excludingInterval[0]) {
                    if(isInFundamentalDomain(point)) {
                        res.add(point);
                    }                
                }
            }
        }
        return res.toArray(Complex[]::new);
    }

    public Complex[] getPointArrayOnRealLineExponential(double a, double b, int numPointsPerSide, double[] excludingInterval){
        List<Complex> res = new LinkedList<Complex>();
        // add left side of interval
        for(int i = 0; i < numPointsPerSide; i++){
            Complex point = new Complex(a + (b-a) / 2 * Math.pow(1.2, -i), 0);
            if(point.re >= excludingInterval[1] || point.re <= excludingInterval[0]) {
                if(isInFundamentalDomain(point)) {
                    res.add(point);
                }            
            }
        }
        Collections.reverse(res);
        // add right side of interval
        for(int i = 1; i < numPointsPerSide; i++){
            Complex point = new Complex(b - (b-a) / 2 * Math.pow(1.2, -i), 0);
            if(point.re >= excludingInterval[1] || point.re <= excludingInterval[0]) {
                if(isInFundamentalDomain(point)) {
                    res.add(point);
                }            
            }
        }
        return res.toArray(Complex[]::new);
    }

    public Complex[] getPointArrayOnCircle(Complex center, double radius, int numPoints) {
        Complex[] res = new Complex[numPoints];
        Complex rotation = Complex.exp(new Complex(0, 2 * Math.PI / (numPoints - 1)));
        Complex currentRotation = new Complex(radius, 0);
        for (int i = 0; i < numPoints; i++) {
            res[i] = new Complex(center.plus(currentRotation));
            currentRotation.assignTimes(rotation);
        }
        return res;
    }

    // check the conditions to figure out which uniformization we are in.
    private int getUniformization() {
        boolean U1 = true;
        boolean U2 = true;
        for (int i = 0; i < numGenerators; i++) {
            if(!(fixpoint[i][0].im == 0 && fixpoint[i][1].im == 0)) {
                U1 = false;
            }
            if(!fixpoint[i][0].equals(fixpoint[i][1].conjugate())){
                U2 = false;
            }
        }
        if(U1) return 1;
        if(U2) return 2;
        return 0;
    }


}
