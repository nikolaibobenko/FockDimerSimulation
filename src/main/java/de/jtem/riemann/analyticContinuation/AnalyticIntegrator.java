/**
This file is part of a jTEM project.
All jTEM projects are licensed under the FreeBSD license 
or 2-clause BSD license (see http://www.opensource.org/licenses/bsd-license.php). 

Copyright (c) 2002-2009, Technische Universität Berlin, jTEM
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

-	Redistributions of source code must retain the above copyright notice, 
	this list of conditions and the following disclaimer.

-	Redistributions in binary form must reproduce the above copyright notice, 
	this list of conditions and the following disclaimer in the documentation 
	and/or other materials provided with the distribution.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE.
**/

package de.jtem.riemann.analyticContinuation;

import de.jtem.blas.ComplexVector;
import de.jtem.mfc.field.Complex;
import de.jtem.numericalMethods.calculus.odeSolving.Extrap;

class AnalyticIntegrator {

	final AnalyticContinuationODE ode;
	
	final AlgebraicCurve curve;
	
	final Extrap integrator;
	
	double[] I, H;

	double eps = 1e-8;

	AnalyticIntegrator( AlgebraicCurve curve, AnalyticContinuationODE ode ) {
		
		this.curve = curve;
		
		this.ode = ode;
		
		integrator = new Extrap(4);

		I = new double[5];
		H = new double[1];

	}
	
	void startContinuation(Complex lambda, Complex mu ) {

		I[0] = 0;
		I[1] = lambda.re;
		I[2] = lambda.im;
		I[3] = mu.re;
		I[4] = mu.im;

		H[0] = 0.1;
	}

	
	void continueAlongSegment( Complex P, Complex Q, boolean continueOnLambdaPlane ) {

		ode.setContinueInLambdaPlane( continueOnLambdaPlane);
		
		double distQP = Q.dist(P);

		if (distQP < 1e-10)
			return;

		ode.direction.assignMinus(Q, P);
		ode.direction.assignDivide(distQP);

		integrator.odex(ode, I, I[0] + distQP, H, eps, eps, ode.fcf);
	}
	
	private final Complex P = new Complex();
	private final Complex Q = new Complex();

	final void continueAlongPath(
				ComplexVector path,
				Complex startPoint,
				Complex endPoint, 
				final boolean continuteOnLambdaPlane ) {
		int numOfPoints = path.size();

		if (numOfPoints < 2)
			return;

		path.get(0, P);

		if( continuteOnLambdaPlane ) {
			startContinuation(P, startPoint);
		} else {
			startContinuation( startPoint, P );
		}
		
		for (int i = 1; i < numOfPoints; i++) {
			path.get(i, Q);
			continueAlongSegment( P, Q, continuteOnLambdaPlane );
			P.assign(Q);
		}
		
		if( continuteOnLambdaPlane ) {
			endPoint.assign( I[3], I[4] );  // muAtEndPoint
		} else {
			endPoint.assign( I[1], I[2] );  // lambdaAtEndPoint
		}
	}

}
