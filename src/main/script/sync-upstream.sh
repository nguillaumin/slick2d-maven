#!/bin/sh

if [ -z $1 ]; then
    echo "Usage: $0 </path/to/upstream/hg/repo>"
    exit 1
fi

OLD_PWD=`pwd`

# Check upstream location
cd $1
UPSTREAM=$PWD/trunk/Slick
cd $OLD_PWD
if [ ! -d $UPSTREAM -a ! -f $UPSTREAM/build.xml ]; then
    echo "$UPSTREAM doesn't seem to contain the Slick mercurial repository"
    echo "Expected to find $UPSTREAM/build.xml"
    exit 1
fi

# Check current location
cd "`dirname $0`"
BASE=$PWD/../../..
cd $OLD_PWD
if [ ! -f $BASE/pom.xml -a ! -f $BASE/slick-core/ ]; then
    echo "Script ran from invalid directory"
    echo "Expected to find $BASE/pom.xml and $BASE/slick-core/"
    exit 1
fi

RSYNC_FLAGS="-rzvv --delete"

# Sync core
rsync $RSYNC_FLAGS $UPSTREAM/authors.txt $BASE/authors.txt
rsync $RSYNC_FLAGS --exclude 'ibxm' --exclude 'version' $UPSTREAM/src/ $BASE/slick2d-core/src/main/java/

# Sync examples
rsync $RSYNC_FLAGS $UPSTREAM/examples/ $BASE/slick2d-examples/src/main/java/
 
# Sync tools, not the absence of the trailing slash
for TOOL in hiero packulike peditor scalar; do
    rsync $RSYNC_FLAGS $UPSTREAM/tools/org/newdawn/slick/tools/$TOOL $BASE/slick2d-$TOOL/src/main/java/org/newdawn/slick/tools/
done

