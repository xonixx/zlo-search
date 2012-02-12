#!/bin/sh

SRC_DIR="D:/stuff/test/java/zlo-search/zlo_search/src/main/java"

JAXB_HOME="D:/DISTR/JAVA_J2EE/JAXB/jaxb-ri-20110601"
#JAXB_HOME="D:/!DISTRIBs/Programming/Java/xml/jaxb-2_1_7/jaxb-ri"

export JAXB_HOME

JAXB_OPTS="-no-header " # don't add timestamp

OUT_DIR="$SRC_DIR/info/xonix/zlo/search/forumxmlprotocol/xjccompiled"

PATH="$JAXB_HOME/bin:$PATH"

#rm -rf $OUT_DIR
sh xjc.sh $JAXB_OPTS -p info.xonix.zlo.search.xmlfp.jaxb_generated -d $SRC_DIR descriptor.xsd message.xsd lastMessageNumber.xsd

#echo "Copying xsd"
#copy *.xsd D:/TEST/JAVA/ZloSearcher/trunk/zlo_web/resources/xmlfp/xsd

#pause
