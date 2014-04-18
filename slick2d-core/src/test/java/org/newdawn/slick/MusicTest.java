package org.newdawn.slick;

import org.newdawn.slick.tests.util.TestWithLWJGL;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class MusicTest extends TestWithLWJGL {

    public void testFadeStops() throws SlickException, InterruptedException {
        Music m = new Music("testdata/theme.ogg");
        
        m.play();
        Assert.assertTrue(m.playing());
        
        m.fade(250, .25f, true);
        
        for (int i=0; i<10; i++) {
            Thread.sleep(50);
            m.update(50);
        }
        
        Assert.assertFalse(m.playing());
    }

}
