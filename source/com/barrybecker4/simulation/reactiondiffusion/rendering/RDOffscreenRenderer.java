/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.simulation.common.rendering.ColorRect;
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel;
import com.barrybecker4.ui.renderers.OfflineGraphics;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Renders the state of the GrayScottController model to an offscreen image,
 * then copies the whole image to the screen.
 * @author Barry Becker
 */
public class RDOffscreenRenderer extends RDRenderer {

    /** offline rendering is fast  (I wish it was anyway)  */
    private OfflineGraphics offlineGraphics_;

    private ImageObserver observer_;

    /**
     * Constructor
     */
    public RDOffscreenRenderer(GrayScottModel model, ColorMap cmap, RDRenderingOptions options,
                               Container imageObserver) {
        super(model, cmap, options);
        observer_ = imageObserver;
        offlineGraphics_ = new OfflineGraphics(imageObserver.getSize(), Color.BLACK);
    }

    /**
     * Renders a rectangular strip of pixels.
     */
    @Override
    public void renderStrip(int minX, ColorRect rect, Graphics2D g2) {

        Image img = rect.getAsImage();
        offlineGraphics_.drawImage(img, minX, 0, null);
    }


    @Override
    protected void postRender(Graphics2D g2) {
        g2.drawImage(offlineGraphics_.getOfflineImage(), 0, 0, observer_);
    }

}
