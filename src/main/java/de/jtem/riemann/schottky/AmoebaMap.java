package de.jtem.riemann.schottky;
import de.jtem.mfc.field.Complex;
import inUtil.ComplexHighPrecision;

import java.io.Serializable;
import java.lang.Math;
import java.math.BigDecimal;
import java.math.MathContext;

import org.jzy3d.plot3d.pipelines.NotImplementedException;


public class AmoebaMap implements Serializable{

    final SchottkyDimers schottky;

    final int numGenerators;

    int updateID;

    double theta1, q1;

    // measures errors for the elements in some way?
    final double[][] rho;

    final double[] L1;

    public double[] boundaryResidues;


    AmoebaMap(SchottkyDimers schottky, Complex P0, double[] boundaryResidues) {
        this.schottky = schottky;
        numGenerators = schottky.numGenerators;
        rho = new double[2][numGenerators];
        this.P0.assign(P0);
        this.boundaryResidues = boundaryResidues;
        L1 = new double[numGenerators];
        update();
    }

    AmoebaMap(SchottkyDimers schottky, Complex P0) {
        this(schottky, P0, new double[] {1,-1,1,-1,1,-1});
    }
    
    final Complex zOfRho = new Complex(Double.NaN);
  
    final Complex P = new Complex();
    final Complex P0 = new Complex();

    final Complex A = new Complex();
    final Complex B = new Complex();

    double product_re = 1.0;
    double product_im = 1.0;
    // H for dXi1
    // G for dXi2
    // K for the boundary value differential.
    final Complex H = new Complex();
    final Complex G = new Complex();
    final Complex K = new Complex();

    final Complex dH = new Complex();
    final Complex dG = new Complex();
    final Complex dK = new Complex();

    final Complex dH_Der = new Complex();
    final Complex dG_Der = new Complex();
    final Complex dK_Der = new Complex();

    final Complex H_corr = new Complex();
    final Complex G_corr = new Complex();
    final Complex K_corr = new Complex();
  
    final Complex sP = new Complex();
    final Complex sP0 = new Complex();
    final Complex dsP = new Complex();

    final Complex xi1 = new Complex();
    final Complex xi2 = new Complex();
    final Complex xiBoundary = new Complex();

    final Complex dXi1 = new Complex();
    final Complex dXi2 = new Complex();
    final Complex dXiBoundary = new Complex();

    final Complex dXi1Der = new Complex();
    final Complex dXi2Der = new Complex();
    final Complex dXiBoundaryDer = new Complex();
  
    // final Complex a = new Complex();
    // final Complex b = new Complex();
  
    final Complex d = new Complex();
  
    int n;
  
    long [] noe;
  
    double acc;
    double eps; // = acc / maxNumberOfElements;
    double maxError = 100000;// Double.MAX_VALUE; // set this dynmically in a sensible way.
    
    void update() {
  
        if (updateID == schottky.updateID) {
          return;
        }
    
        updateID = schottky.updateID;
    
        zOfRho.assign(Double.NaN);
    
        theta1 = schottky.theta1;
        q1 = schottky.q1;

        for (int n = 0; n < numGenerators; n++) {
          L1[n] = L1(n);
        }
      }

  /**
   * Returns rhoIntegral( sigma, z ) for zOfRho.
   * Thus you have to call perepareRho( z ) first.
   * @param sigma
   * @return rho( sigma, zOfRhoDiff )
   */
  final double rho(SchottkyGroupElement sigma) {
    return rho[sigma.leftIsInvert][sigma.left];
  }

  /**
   * Prepares rho for value z.
   * @param z
   */
  final void prepareRho(Complex z) {

    if (z.equals(zOfRho)) {
      return;
    }

    zOfRho.assign(z);

    if (schottky.useFancyError) {
      final double q_ = theta1 * theta1;
      final double R2 = schottky.r(q_);
      final double R2Minus = schottky.rMinus(R2, q_);
      final double R2Plus = schottky.rPlus(R2, q_);

      //final double[][] k2 = schottky.k2(z);
      final double[][] k2 = schottky.kIndexed(z, 3);

      for (int j = 0; j < 2; j++) {
        for (int m = 0; m < numGenerators; m++) {

          double rhoJM = 0;

          for (int i = 0; i < 2; i++) {
            for (int n = 0; n < numGenerators; n++) {

              if (n == m) {
                if (i == j) {
                  rhoJM += R2Plus / k2[i][n];
                }
                else {
                  rhoJM += R2Minus / k2[i][n];
                }
              }
              else {
                rhoJM += R2 / k2[i][n];
              }
            }
          }
          rho[j][m] = rhoJM;
        }
      }
    }
    else {


      //double dOfZ = schottky.d2(z);
      double dOfZ = schottky.k(z,3);

      for (int j = 0; j < 2; j++) {
        for (int m = 0; m < numGenerators; m++) {
          rho[j][m] = 1 / dOfZ
              / (1 - q1);
        }
      }
    }
  }

  final double L(SchottkyGroupElement sigma, int n) {

    if (sigma.left == n) {
      throw new RuntimeException("do not compute L for this n");
    }

    return L(sigma, schottky.fixpoint[n][0], schottky.fixpoint[n][1]);
  }

  final double L(SchottkyGroupElement sigma, Complex A, Complex B) {
    return A.dist(B) / schottky.dist(sigma, A) / schottky.dist(sigma, B);
    }

  final double L1(int n) {

    double max = 0;

    for (int i = 0; i < numGenerators; i++) {

      if (i != n) {

        final double L1OfI = Math.max(L(schottky.generator[i], n),
                                      L(schottky.generatorInv[i], n));

        if (L1OfI > max) {
          max = L1OfI;
        }
      }
    }

    return max;
  }

  protected void cleanIncrements() {
      dH.assign(0);
      dG.assign(0);
      dK.assign(0);
      dH_Der.assign(0);
      dG_Der.assign(0);
      dK_Der.assign(0);
      H.assign(0);
      G.assign(0);
      K.assign(0);
      H_corr.assign(0);
      G_corr.assign(0);
      K_corr.assign(0);
  }

  protected void cleanDiffs() {
    xi1.assign(0, 0);
    xi2.assign(0, 0);
    xiBoundary.assign(0, 0);

    dXi1.assign(0, 0);
    dXi2.assign(0, 0);
    dXiBoundary.assign(0, 0);

    dXi1Der.assign(0, 0);
    dXi2Der.assign(0, 0);
    dXiBoundaryDer.assign(0, 0);
  }

      // // Adds the U1 correction to the Amoeba map. g=1 only for now. Therefore n = 0 only instead of doing some Matrix inversion.

      // final void addU1Correction(Complex P) {
      //   // double abelIntegral = schottky.abelianIntegralOf1stKind(P, 0).re;
      //   Complex P0 = new Complex(0.5, 0);
      //   double abelIntegral = Math.log(Complex.crossRatio(B, P0, A, P).abs());
      //   Complex factor1 = new Complex();
      //   Complex factor2 = new Complex();
      //   factor1.assignCrossRatio(schottky.getA(0), alpha, schottky.getB(0), gamma);
      //   double f1 = Math.log(factor1.re) / Math.log(schottky.getMu(0).re) * abelIntegral;
      //   factor2.assignCrossRatio(schottky.getA(0), beta, schottky.getB(0), gamma);
      //   double f2 = Math.log(factor2.re) / Math.log(schottky.getMu(0).re) * abelIntegral;
      //   product_re -= f1;
      //   product_im -= f2;
      // }



    // calculates the amoebaMap based on a quadGrid setup. Thus there need to be four angles picked.
    final void processSchottkyElement(final SchottkyGroupElement element) {

      if (element.updateID != updateID) {
        schottky.updateElement(element);
      }

      // Not sure what to do here instead...
      // element.diff(B, A, d);
      double error = maxError - 1;
      if (element != schottky.id) {
        //   error = (Math.abs(d.re) + Math.abs(d.im)) * rho(element);
          error = L1[n] * element.norm * rho(element);
      }

      // if (error > maxError || Double.isNaN(error)) {
      //   return;
      // }
  
      // This way we get genus 0.
      if (element.wordLength > 5) {
        return;
      }

      // if (error * noe[element.wordLength] < acc || error < eps ) {
      //   acc += acc / noe[element.wordLength] - error;
      //   return;
      // }

      cleanIncrements();
      
      
      element.applyTo(P, sP);
      element.applyTo(P0, sP0);

      element.applyDifferentialTo(P, dsP);

      calculateIncrements(element);

      addIncrements();

      if (element.child == null) {
        schottky.createLeftChilds(element);
      }
  
      final SchottkyGroupElement[] child = element.child;
  
      final int numChildren = child.length;
  
      String[] childrenWords = new String[numChildren];
      for (int i = 0; i < numChildren; i++) {
        childrenWords[i] = child[i].word();
      }
      for (int i = 0; i < numChildren; i++) {
        // String childWord = child[i].word();
        processSchottkyElement(child[i]);
      }
    }

    protected void calculateIncrements(final SchottkyGroupElement element) {
      throw new NotImplementedException();
    }

    protected void addIncrements() {
      if(!dH.isNaN()){
        dXi1.assignPlus(dH);
      }
      if(!dG.isNaN()){
        dXi2.assignPlus(dG);
      }
      if(!dK.isNaN()){
        dXiBoundary.assignPlus(dK);
      }
      // if(!H.divide(H_corr).log().isNaN()){
      //   xi1.assignTimes(H.divide(H_corr));
      // }
      // if(!G.divide(G_corr).log().isNaN()){
      //   xi2.assignTimes(G.divide(G_corr));
      // }
      if(!H.minus(H_corr).isNaN()){
        xi1.assignPlus(H.minus(H_corr));
      }
      if(!G.minus(G_corr).isNaN()){
        xi2.assignPlus(G.minus(G_corr));
      }
      if(!K.divide(K_corr).log().isNaN()){
        xiBoundary.assignTimes(K.divide(K_corr));
      }
      if(!dH_Der.isNaN()){
        dXi1Der.assignPlus(dH_Der);
      }
      if(!dG_Der.isNaN()){
        dXi2Der.assignPlus(dG_Der);
      }
      if(!dK_Der.isNaN()){
        dXiBoundaryDer.assignPlus(dK_Der);
      }
    }

    protected void addResidues() {
      throw new NotImplementedException();
    }

      final Complex[] getDifferentials(final Complex P, final double accuracy) {
    
        // calculates and returns xi1, xi2 and xiTilde.
        // (xi1.re, xi2.re) is then Amoeba Map.
        this.noe = schottky.numOfElementsOfCosetWithWordLength;
        
        this.A.assign(schottky.fixpoint[n][0]);
        this.B.assign(schottky.fixpoint[n][1]);

        this.P.assign(P);

        cleanDiffs();

        this.acc = accuracy;
        this.eps = accuracy / schottky.maxNumOfElements;

        prepareRho(P);

        processSchottkyElement(schottky.id);

        int nE = schottky.getNumElements();

        // xi1.assignLog();
        // xi2.assignLog();

        // addResidues();

        // Correction in the U1 uniformization case.
        // if(schottky.uniformization == 1) {
        //   addU1Correction(P);
        // }

        if(this.acc < 0 ) // this test is needed because of the eps crieteria
          throw new RuntimeException( "could not evaluate series because of numerical instabilities" );
        
        // r.assign(new Complex(Math.log(product_re), Math.log(product_im)));
        return new Complex[]{dXi1.copy(), dXi2.copy(), dXiBoundary.copy(), xi1.copy(), xi2.copy(), xiBoundary.copy(), dXi1Der.copy(), dXi2Der.copy(), dXiBoundaryDer.copy()};
      }

      public Complex amoebaMap(final Complex P, final double accuracy) {
        Complex[] diffs = getDifferentials(P, accuracy);
        return new Complex(diffs[3].re, diffs[4].re);
      }
      
      public Complex getSlope(final Complex P, final double accuracy) {
        Complex[] diffs = getDifferentials(P, accuracy);
        Complex slope = new Complex(diffs[3].im, diffs[4].im);
        // slope.assignPlus(Math.PI, Math.PI);
        return slope.divide(Math.PI);
      }
      
      
      public Complex boundaryMap(final Complex P, final double accuracy) {
        Complex[] diffs = getDifferentials(P, accuracy);

        Complex R1 = diffs[0].divide(diffs[2]);
        Complex R2 = diffs[1].divide(diffs[2]);
        // psi, eta are the Boundary coordinates.
        if ((Math.abs(R1.im) > 0.1 || Math.abs(R2.im) > 0.1)) {
          System.out.println("P: " + P + ", R1.im " + R1.im + ", R2.im: " + R2.im);
        }
        // double psi = - R2.re + R1.re * (R2.im/R1.im);
        // double eta = R1.re - R2.re * (R1.im/R2.im);
        double psi = -R2.invert().im / R1.divide(R2).im;
        double eta = R1.invert().im / R2.divide(R1).im;
        return new Complex(psi, -eta);

        // probably should switch to a more numerically stable version here. Consider using BigDecimal.

        // ComplexHighPrecision dX1 = new ComplexHighPrecision(diffs[0]);
        // ComplexHighPrecision dX2 = new ComplexHighPrecision(diffs[1]);
        // ComplexHighPrecision dX = new ComplexHighPrecision(diffs[2]);
        // BigDecimal factor = dX1.times(dX2.conjugate()).im;
        // BigDecimal x = dX1.times(dX.conjugate()).im;
        // BigDecimal y = dX2.times(dX.conjugate()).im;
        // BigDecimal psi = x.divide(factor, MathContext.DECIMAL128);
        // BigDecimal eta = y.divide(factor, MathContext.DECIMAL128);
        // return new Complex(psi.doubleValue(), -eta.doubleValue());

        // double factor = diffs[0].times(diffs[1].conjugate()).im;
        // double psi = diffs[0].times(diffs[2].conjugate()).im;
        // double eta = diffs[1].times(diffs[2].conjugate()).im;

        // // if (Math.abs(psi/factor - eta/factor) > 1) {
        // //   System.out.println(psi/factor + ", " + eta/factor);
        // // }
        // return new Complex(psi/factor, -eta/factor);
      }

      public Complex boundaryCurve(final Complex P, final double accuracy) {
        // non-singular parametrization of boundary curves using derivatives.
        Complex[] diffsP = getDifferentials(P, accuracy);
        Complex[] diffsPDer = {dXi1Der, dXi2Der, dXiBoundaryDer};
        Complex eta = diffsPDer[2].times(diffsP[1]).minus(diffsPDer[1].times(diffsP[2]));
        eta.assignDivide(diffsPDer[0].times(diffsP[1]).minus(diffsPDer[1].times(diffsP[0])));
        Complex psi = diffsPDer[2].times(diffsP[0]).minus(diffsPDer[0].times(diffsP[2]));
        psi.assignDivide(diffsPDer[1].times(diffsP[0]).minus(diffsPDer[0].times(diffsP[1])));
        if (Math.abs(psi.im) > 0.1 | Math.abs(eta.im) > 0.1) {
          System.out.println("boundaryCurve non-real");
        }
        return new Complex(psi.re, eta.re);
      }

}
