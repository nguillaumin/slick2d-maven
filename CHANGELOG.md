# slick2d-maven changelog

## Next release

* Now using Java 7 source level
* Bumped all plugins / dependencies to latest versions
* Moved all `slick2d-core` tests in `src/test/` and added test data. Test classes are not shipped any more in the main JAR
* Updated `Renderable` to include more `draw()` methods common to all implementations, thanks to [lucas_cimon](https://bitbucket.org/kevglass/slick/pull-request/15/making-renderable-class-more-polymorphic/diff)
* Fixed `RoundedRectangle.contains()` after calling `setWidth()` / `setHeight()` thanks to [Joshua Hertlein](https://bitbucket.org/kevglass/slick/issue/41/roundedrectangles-setwidth-and-setheight)
* Fixed `AppContainer.setMouseCursor()` by using correct image dimensions thanks to [Peter W](https://bitbucket.org/kevglass/slick/issue/40/using-wrong-variable)
* Fixed `SpriteSheetFont.drawString()` with indexes thanks to [Emmanuel Rousseau](https://bitbucket.org/kevglass/slick/issue/39/rendering-bug-with-spritesheetfont)
* Fixed `Music` [not stopping](https://bitbucket.org/kevglass/slick/issue/30/musicfade-stopafterfade-does-not-work) when using `fade(.., ..., true)`
* Fixed `Image.getColor()` for [scaled images](https://bitbucket.org/kevglass/slick/issue/42/getcolor-crashes-for-a-flipped-image)

* Merge changes from [development branch](https://bitbucket.org/kevglass/slick/commits/96a4b840204c65241d5bf81afbb4eead5f25c57f) since [Feb 04, 2012](https://bitbucket.org/kevglass/slick/commits/d0a74b4374b88b6abae9f9643c36c0819994c870)
  * Fixes Circle.contains(x,y) for the dev branch.
  * Override Shape#getCenter in Circle.
    This fixes bug #13.
  * Add matching get for Shape#setLocation(Vector2f)  
    This adds Vector2f Shape#getLocation().
  * Fix Circle#contains(x,y).  
    Previous implementation used top left corner as center of circle for contains().
  * Streamline and clean up DistanceFieldEffect and document it better
  * Add a signed distance field effect  
    This can be used for crisp font rendering even at high magnifications,
    at little to no extra runtime cost. For details, see the Valve paper:
    http://www.valvesoftware.com/publications/2007/SIGGRAPH2007_AlphaTestedMagnification.pdf
  * Merge with b7d3f117e9cdbb0bf1180906662f11f3362be5d6
  * small change to UnicodeFont to use codepoints 32-127 for addAsciiGlyphs; otherwise some Mac users report load times greater than 3 seconds
  * fixing strange Java2D bug with UnicodeFont; should reduce loading times on Mac. Note: addAsciiGlyphs now only adds glyphs 0-127
  * Merged in dayrinni/slick-day-animation-cycle/development (pull request #8)
  * Small changes to the animation class that allow for pausing before cycling. ie: Cycle through all of the animation and wait 1000 MS before beginning again. Also made some changes to a constructor to fix an issue with animation and sprite sheets.
  * fixing rotate bug with drawEmbedded
  * fixing slick-util build file
  * building newest slick; upgrading to latest LWJGL; fixing bug with TGA cursor loading
  * overloading image constructor
  * fixing headless mode
  * fixing typo in ShaderProgram
  * Improved cursor loader.  
    The new cursor loader is now able to fix cursor images so the host system is able to work with them properly. It performs changes of the texture size and is able to fix alpha values. It works with cursors that are too small for the host system and with cursor too large. How ever the support for larger cursors is limited to avoid crashes. The cursor will likely look crappy.
  * Removed some bogus modifier in a interface (public, abstract)
  * Fixed loading of PNG data using the TWL PNG loader
  * allowing symbols in text fields, using CharSequence instead of String for fonts
  * fixing advanced shader test
  * adding shader lessons
  * 
    - added Pauseable interface for pausing update or render calls
    - added Pauseable to GameState and StateBasedGame
    - states can now be paused (update or render)
    - added test for new paues feature
  * fixed typo in TiledMapPlus#write orthogonal was written as orthoganal.
  * fixing sound releasing / audio deletion
  * adding release() to NullAudio
  * adding release() and isPaused() to Sound/Music
  * more accurate FPS counter in GameContainer
  * fixing small bug in tiled ObjectGroup parsing
  * fixing drawWarped docs
  * pedigree now catches all exceptions
  * trying to patch transparent color bug; fixed incorrect mouse Y (it's now 1 pixel lower)
  * "base=" is now the baseline/ascent for AngelCodeFont for better compatibility with TWL Font Tool
  * adding resize feature to AppGameContainer
  * moving shader tests to their own package
  * ensuring getGraphics enables the new Graphics context
  * fixing getColor for flipped images
  * small BUG/RFE run: shape points check, image/texture documentation, fire emitter temp fix, ScalableGame scale x/y getters
  * Implements accelerometer input.
  * fixed bug where OpenGL wasn't destroyed from canvas container exit; fixing canvas tests to hide the frame before destroying OpenGL
  * small fix-up with CanvasContainerTest
  * updating Canvas tests to use JFrame; showing proper use of focusing/etc; updating game container to reset background color after resize to whatever was last set on Graphics.setBackground
  * fixing a null pointer in BufferedImageUtil
  * updating testdata font ref in tests
  * destroying sound on applet exit
  * small fixes and typos
  * reverting changes in UnicodeFontTest
  * Merge with d58328a324146d8f0c0d6503e5e33cfc42f8f895
  * fixing bugs with transparency
  * small fixes to canvas container, particle editor
  * Added resume method
    @see http://slick.javaunlimited.net/viewtopic.php?f=27&t=4800
  * Merged Image.java
  * Backed out changeset: a596a10e57ce
  * Added layer opacity/alpha rendering
  * Added default variable init value, makes code look uniform
  * Added support for writing Tiled 0.8 Maps
  * Added more documentation for the drawEmbedded /w transform function
  * Shader support, UNSTABLE
  * Added support for rendering according to a transform (Rotate, FlipX, FlipY) - part of support for Tiled Maps V0.8  
  * 0.8 Feature - IMPLEMENTED Flipping and Rotating Tiles  
    0.8 Feature - IMPLEMENTED Polygon Objects  
    0.8 Feature - IMPLEMENTED Polyline Objects  
    0.8 Feature - IMPLEMENTED Coloured object groups (doesn't render objects in colour as usual)  
    0.8 Feature - IMPLEMENTED Tileset properties  
    Some optimisations and code cleanups here and there
  * Backed out changeset: 8caf0195d4c9
  * Merged @thaaks Isometric Rendering support  
    Added ZLIB compression support  
    Added parsing for opacity values of layers (need to still make render method render opaque layers)  
    http://slick.javaunlimited.net/viewtopic.php?f=1&t=4714 - BUGFIX
  * renaming ShaderProgram.loadSource to ShaderProgram.readFile for clarity
  * improving ShaderProgram.loadSource
  * typos in last commit that resulted in compiler errors; fixed ShaderProgram
  * overloaded drawEmbedded(x, y)
  * fixing shaderprogram, moved getMaxSingleImageSize from BigImage to Image (does not break backwards compatibility)
  * simplifying shader classes
  * cleaning up shader tests
  * advanced shader test with blurring
  * replacing LWJGLUtils.log with Slick's Log in WaveData
  * adding shader support
  * possibly fixed sprite sheet bug with animation
  * fixing again
  * fixing font width bugs
  * javadoc for new copyArea method
  * fixed bug with Graphics.copyArea, added test demonstrating proper copying technique
  * added predraw/postdraw to copyArea, testing copy area bug
  * extended InternalTextureLoader with mipmap generation, included a simple test
  * fixing naming/comments within FontTest
  * Reverted to original
  * Added functionality which will aid in inter-state communication
    Uses a global HashMap etc.
  * Managed Shader support through OpenGL/ARB_Extensions
  * Merge with TiledMapPlus
  * BUGFIX (http://slick.javaunlimited.net/viewtopic.php?f=1&t=4059)  
    Merge with TiledMapPlus
  * fixing comments in FontTest
  * fixed AngelCodeFont.Glyph field visibility; added custom font rendering test
  * added rotate in use, added getGlyph to AngelCodeFont, added ascent/descent to AngelCodeFont (only works with new Hiero exports, otherwise returns 0), added case-ignoring for fonts that only need a single case (e.g. all upper case)
  * added rotate in use for Image and SpriteSheet
  * Slick-AE animation class fix.  Auto update now works.
  * Updating the Slick-AE gdx jars to version 9
  * Adding fix for tiledmaps with tilesets in other directories.
  * Implements SavedState in Slick-AE
    as described by mangelok at http://slick.javaunlimited.net/viewtopic.php?t=3559
  * Failed to actually add QuadBasedLineStripRenderer.java last update adding it now.
  * Fixes a bug that caused shapes to be drawn incorrectly.  
    To solve, the quad based line strip renderer was overwritten to no longer fall back to default line strip renderer when width=1
  * reverted getGraphics; no longer uses setCurrent
  * Image.getGraphics now sets the new graphics to the current
  * Slick-AE Input class updated to work with libgdx 9
  * fixing image flip bug in Hiero; added anti-alias menu item
  * fixed another minor bug with centerX/centerY; added test case to ImageTest
  * Merge with bd5b250a0ec9b2650141cb8e07a1b5d0d1eadfaa
  * fixing centerX/centerY when scaled and with getScaledCopy
  * adding fixes to Image's centerX/centerY when scaled/copied
  * added ADD_ALPHA and MULTIPLY_ALPHA modes
  * Merged in Nitram0815/slick/development (pull request #2)
  * Added test case to demonstrate the difference between the image formats (RGBA, RGB, Grayscale, Grayscale+Alpha)
  * Fixed incorrect usage of the additional image formats in the texture implementations
  * added flushPixelData to destroy(); the pixel data is set to null when we release the image
  * StateBasedGame edited so that a state's enter would be called before render.
  * Edited authors.txt
  * this commit changes the StateBasedGame such that the enter method of a state is called before the render method (by calling it before the entering transition starts)
  * added author
  * fixing image filter constants
  * bugs fixed:  
      Image - various fixes - http://slick.javaunlimited.net/viewtopic.php?f=27&t=4454  
        - (width, height, filter) constructor reduced by using EmptyImageData
        - draw/drawSheared/drawEmbedded now all check for init() before rendering
        - startUse/endUse now rely on Texture instead of Image (i.e. as long as its the same texture, it's ok)
        - Image.setTexture will revert the "destroyed" flag if changed  
    Image/FBO/Pbuffer - fixed offscreen memory leak  
    InternalTextureLoader - added getTextureCount() method - returns the number of active textures  
    TextureImpl - release() now reverts the textureID to zero (subsequent calls to release() is then ignored)  
    Image - center of rotation - http://slick.javaunlimited.net/viewtopic.php?f=1&t=4252  
    SoundStore - last source never played - http://slick.javaunlimited.net/viewtopic.php?f=1&t=3659  
    SoundStore - music volume cannot be set - http://slick.javaunlimited.net/viewtopic.php?f=1&t=4598  
      
    tests fixed:  
    ImageMemTest  
    ImageGraphicsTest  
    SoundTest
  * simple fixes to Image's center x / y
  * Updated ANT build to use proper Java versions  
    Implemented patch to allow Slick to load grayscale images as luminance textures into the OpenGL context  
    Fixed invalid references to the new version of the PNG decoder
  * Modified project settings to use JDK 6.  
    Updated native libraries in root dir to fit lwjgl.jar.  
    Replaced PNGDecoder with latest version from Matthias Mann.  
    Fixed PNGImageData to use PNGDecoder enums.
  * modified authors.txt in development branch
  * Removed obsolete native jar files,
    removed TiledMapPlus stuff as it was not Java 1.4 compliant and broke project settings,
    reverted TiledMap stuff back to initial version from Kev.

## v1.0.0 (2014-04-16)

* Initial release based on the latest sources available from the [upstream repository](https://bitbucket.org/kevglass/slick/) (Last upstream commit: 2013-03-28)
