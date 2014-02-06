/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.engine.html_presentation.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.conqat.engine.commons.format.DeltaSummary;
import org.conqat.lib.commons.assessment.AssessmentUtils;

/**
 * Draws a visual representation of a delta summary. Currently this is an arrow
 * pointing up, down, or straight. The arrow's color is determined from the
 * summary.
 * 
 * @author $Author: steidl $
 * @version $Rev: 43636 $
 * @ConQAT.Rating GREEN Hash: 4976DDF0988036486F6035133977C869
 */
public class DeltaSummaryDrawer extends SummaryDrawerBase<DeltaSummary> {
    
    /** Shape for the arrow pointing up-right. */
    private Shape arrowUp;

    /** Shape for the arrow pointing down-right. */
    private Shape arrowDown;

    /** Shape for the arrow pointing straight-right. */
    private Shape arrowStraight;
    
    /**
     * Constructs a new delta summary as a square with the given edge length.
     */
    public DeltaSummaryDrawer(int size) {
        super(size, size);
        createArrows();
    }

    /** {@inheritDoc} */
    @Override
    protected void drawUndefined(Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
    }

    /** {@inheritDoc} */
    @Override
    protected void drawSummary(DeltaSummary summary, Graphics2D graphics) {
        
        drawUndefined(graphics); // This is used to clear the area.        
        graphics.setColor(AssessmentUtils.getColor(summary.getColor()));
        
        // Draw the arrow depending on whether the corresponding value went up,
        // down, or is unchanged.
        if (summary.getDelta() == 0) {
            graphics.fill(arrowStraight);
        } else if (summary.getDelta() > 0) {
            graphics.fill(arrowUp);
        } else {
            graphics.fill(arrowDown);
        }
    }
    
    /** Creates and caches the arrow shapes. */
    private void createArrows() {
        final int d   = width;
        final int d25 = (int)(width * 0.28);
        final int d50 = (int)(width * 0.5);
        final int d75 = (int)(width * 0.72);
        
        Polygon p = new Polygon();
        // head
        p.addPoint(d50, 0);
        p.addPoint(d, d50);
        p.addPoint(d50, d);

        // tail
        p.addPoint(d50, d75);
        p.addPoint(0, d75);
        p.addPoint(0, d25);
        p.addPoint(d50, d25);
        
        arrowStraight = p;
        arrowUp       = rotate(arrowStraight, -45);
        arrowDown     = rotate(arrowStraight,  45);
    }
    
    /**
     * Returns a copy of the polygon rotated by the given number of degrees. The
     * result is centered within the bounds of this drawer.
     */
    private Shape rotate(Shape s, double degrees) {
        AffineTransform tRotate = new AffineTransform();
        tRotate.rotate((Math.PI / 180) * degrees, width / 2.0, height / 2.0);
        Shape result = tRotate.createTransformedShape(s);
        
        // centering
        Rectangle2D bounds = result.getBounds2D();
        AffineTransform tTranslate = new AffineTransform();
        double tx = ((width  - bounds.getWidth())  / 2.0) - bounds.getMinX();
        double ty = ((height - bounds.getHeight()) / 2.0) - bounds.getMinY();
        tTranslate.translate(tx, ty);
        
        return tTranslate.createTransformedShape(result);
    }
}
