package org.newdawn.slick.geom;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class RoundedRectangleTest {

    public void baseTest() {
        RoundedRectangle rr = new RoundedRectangle(10, 10, 20, 20, 1);
        
        Assert.assertEquals(rr.getX(), 10f);
        Assert.assertEquals(rr.getY(), 10f);
        Assert.assertEquals(rr.getMaxX(), 29f);
        Assert.assertEquals(rr.getMaxY(), 29f);
        Assert.assertEquals(rr.getCornerRadius(), 1f);
    }
    
    public void setXsetYTest() {
        RoundedRectangle rr = new RoundedRectangle(10, 10, 20, 20, 1);
        
        rr.setX(30);
        rr.setY(30);

        Assert.assertEquals(rr.getX(), 30f);
        Assert.assertEquals(rr.getY(), 30f);
        Assert.assertEquals(rr.getMaxX(), 49f);
        Assert.assertEquals(rr.getMaxY(), 49f);
        Assert.assertEquals(rr.getCornerRadius(), 1f);
    }
    
    public void resize() {
        RoundedRectangle rr = new RoundedRectangle(0, 0, 10, 10, 1);
        
        rr.setWidth(30);
        rr.setHeight(30);
        
        Assert.assertEquals(rr.getX(), 0f);
        Assert.assertEquals(rr.getY(), 0f);
        Assert.assertEquals(rr.getMaxX(), 29f);
        Assert.assertEquals(rr.getMaxY(), 29f);
        Assert.assertEquals(rr.getCornerRadius(), 1f);
    }
    
    public void resizeContains() {
        RoundedRectangle rr = new RoundedRectangle(0, 0, 10, 10, 1);
        
        Assert.assertFalse(rr.contains(15, 15));
        
        rr.setWidth(30);
        rr.setHeight(30);
        
        Assert.assertTrue(rr.contains(15, 15));
    }

}
