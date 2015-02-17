# slick2d-maven changelog

## Next release

* Bump LWJGL to v2.9.3 to fix a build problem on Mac OS X
* Fix Javadoc for JDK 8 thanks to cdlm

## v1.0.1 (2014-05-18)

* Now using Java 7 source level
* Bumped all plugins / dependencies to latest versions
* Moved all `slick2d-core` tests in `src/test/` and added test data. Test classes are not shipped any more in the main JAR
* Updated `Renderable` to include more `draw()` methods common to all implementations, thanks to [lucas_cimon](https://bitbucket.org/kevglass/slick/pull-request/15/making-renderable-class-more-polymorphic/diff)
* Fixed `RoundedRectangle.contains()` after calling `setWidth()` / `setHeight()` thanks to [Joshua Hertlein](https://bitbucket.org/kevglass/slick/issue/41/roundedrectangles-setwidth-and-setheight)
* Fixed `AppContainer.setMouseCursor()` by using correct image dimensions thanks to [Peter W](https://bitbucket.org/kevglass/slick/issue/40/using-wrong-variable)
* Fixed `SpriteSheetFont.drawString()` with indexes thanks to [Emmanuel Rousseau](https://bitbucket.org/kevglass/slick/issue/39/rendering-bug-with-spritesheetfont)
* Fixed `Music` [not stopping](https://bitbucket.org/kevglass/slick/issue/30/musicfade-stopafterfade-does-not-work) when using `fade(.., ..., true)`
* Fixed `Image.getColor()` for [scaled images](https://bitbucket.org/kevglass/slick/issue/42/getcolor-crashes-for-a-flipped-image)

## v1.0.0 (2014-04-16)

* Initial release based on the latest sources available from the [upstream repository](https://bitbucket.org/kevglass/slick/) (Last upstream commit: 2013-03-28)
