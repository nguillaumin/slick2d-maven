package org.newdawn.slick;

import org.newdawn.slick.tests.util.TestWithDisplay;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups="display")
public class ImageTest extends TestWithDisplay {

    public void testGetColor() throws SlickException {
        Image i = new Image("testdata/testcard.png");
        getAllColors(i);

        Assert.assertEquals(i.getColor(0, 0), Color.red);
        Assert.assertEquals(i.getColor(26, 15), Color.green);
        Assert.assertEquals(i.getColor(51, 50), Color.blue);

    }

    public void testGetColorFlipped() throws SlickException {
        Image i = new Image("testdata/testcard.png", true).getFlippedCopy(true, true);
        getAllColors(i);

        Assert.assertEquals(i.getColor(99, 0), Color.red);
        Assert.assertEquals(i.getColor(74, 15), Color.green);
        Assert.assertEquals(i.getColor(49, 50), Color.blue);
    }
    

    public void testGetColorScaled() throws SlickException {
        Image i = new Image("testdata/testcard.png", true).getScaledCopy(2f);
        getAllColors(i);

        Assert.assertEquals(i.getColor(0, 0), Color.red);
        Assert.assertEquals(i.getColor(26, 15), Color.green);
        Assert.assertEquals(i.getColor(51, 50), Color.blue);
        
        // Account for 100-128 being black (128 = texture width)
        // then texture wraps
        Assert.assertEquals(i.getColor(128, 0), Color.red);
        Assert.assertEquals(i.getColor(154, 15), Color.green);
        Assert.assertEquals(i.getColor(179, 50), Color.blue);


    }
    
    public void testGetColorFlippedScaled() throws SlickException {
        Image i = new Image("testdata/testcard.png", true).getFlippedCopy(true, true).getScaledCopy(2f);

        getAllColors(i);
        
        Assert.assertEquals(i.getColor(1, 0), Color.blue);
        Assert.assertEquals(i.getColor(26, 15), Color.green);
        Assert.assertEquals(i.getColor(51, 50), Color.red);
        
        // Account for 100-128 being black (128 = texture width)
        Assert.assertEquals(i.getColor(80, 50).r, 0f);
        Assert.assertEquals(i.getColor(80, 50).g, 0f);
        Assert.assertEquals(i.getColor(80, 50).b, 0f);
        
        // ...then texture wraps (and is flipped)
        Assert.assertEquals(i.getColor(130, 0), Color.blue);
        Assert.assertEquals(i.getColor(154, 15), Color.green);
        Assert.assertEquals(i.getColor(179, 50), Color.red);
    }
    
    /**
     * Ensure that all color data can be accessed without throwing
     * exceptions.
     * @param i Image to test.
     */
    private void getAllColors(Image i) {
        for (int x=0; x<i.getWidth(); x++) {
            for (int y=0; y<i.getHeight(); y++) {
                i.getColor(x, y);
            }
        }
    }

}
