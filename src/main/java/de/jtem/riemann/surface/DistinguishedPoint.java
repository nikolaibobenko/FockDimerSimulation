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

package de.jtem.riemann.surface;

import java.io.Serializable;

import de.jtem.mfc.field.Complex;

public class DistinguishedPoint extends SurfacePoint implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
     
    public int sheetNumber;
    
    public boolean isAlsoBranchPoint = false;

    public DistinguishedPoint( RamifiedCovering aSurface, double x, double y ) {

	super( aSurface, x, y );
    }

    public DistinguishedPoint( double x, double y ) {

	super( x, y );
    }

    public DistinguishedPoint( Complex z ) {
        super( z );
    }

    public DistinguishedPoint( RamifiedCovering aSurface, 
			       double x, double y, int aSheetNumber ) {

	super( aSurface, x, y );

	sheetNumber = aSheetNumber;    
    }

    public DistinguishedPoint( double x, double y, int aSheetNumber ) {

	super( x, y );

	sheetNumber = aSheetNumber;    
    }
 
   
    public DistinguishedPoint( double x, double y, boolean aBoolean ) {

	super( x, y );

	isAlsoBranchPoint = aBoolean;
	
    }  
  
}








