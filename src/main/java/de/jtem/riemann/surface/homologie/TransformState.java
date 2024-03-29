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

package de.jtem.riemann.surface.homologie;

import de.jtem.blas.IntegerMatrix;
import de.jtem.blas.IntegerVector;

public class TransformState implements java.io.Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private Transform transform;
    
    private final IntegerMatrix g  = new IntegerMatrix ();
    private final IntegerMatrix gl = new IntegerMatrix ();
    private final IntegerMatrix T  = new IntegerMatrix ();
    private       IntegerMatrix h;

    
    private       IntegerVector distinguishedPoints;
    private final IntegerVector monodromyAboutInfinity = new IntegerVector ();
    private final IntegerVector edgeStartPoint         = new IntegerVector ();
    private final IntegerVector edgeBranchPoint        = new IntegerVector ();
    private final IntegerVector edgeEndPoint           = new IntegerVector ();

    TransformState( Transform aTransform ) {
	transform = aTransform;

	if( transform.h != null )
	    h = new IntegerMatrix ();

	if( transform.distinguishedPoints != null )
	    distinguishedPoints = new IntegerVector ();

	save( transform );
    }

    void save( Transform sender ) {

	if( sender != transform )
	    throw new IllegalArgumentException( "transform state does not belong to sender" );
	
	g. assign( transform.g  );
	gl.assign( transform.gl );
	T. assign( transform.T  );

	if( h != null ) 
	    h.assign( transform.h );

	if( distinguishedPoints != null )
	    distinguishedPoints.assign( transform.distinguishedPoints );

	monodromyAboutInfinity.assign( transform.monodromyAboutInfinity );
	edgeStartPoint        .assign( transform.edgeStartPoint         );
	edgeBranchPoint       .assign( transform.edgeBranchPoint        );
	edgeEndPoint          .assign( transform.edgeEndPoint           );
    }

    void load( Transform sender ) {

	if( sender != transform )
	    throw new IllegalArgumentException( "transform state does not belong to sender" );

	transform.g. assign( g  );
	transform.gl.assign( gl );
	transform.T. assign( T  );
	
	if( h != null ) 
	    transform.h.assign( h );

	if( distinguishedPoints != null )		
	    transform.distinguishedPoints.assign( distinguishedPoints );

	transform.monodromyAboutInfinity.assign( monodromyAboutInfinity );
	transform.edgeStartPoint        .assign( edgeStartPoint );
	transform.edgeBranchPoint       .assign( edgeBranchPoint );
	transform.edgeEndPoint          .assign( edgeEndPoint );
    }

}
