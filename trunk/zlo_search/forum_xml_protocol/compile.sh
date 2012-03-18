#!/bin/sh

SRC_DIR="D:/stuff/test/java/zlo-search/zlo_search/src/main/java"
XSD_DIR="D:/stuff/test/java/zlo-search/zlo_search/src/main/resources/info/xonix/zlo/search/xmlfp/xsd"

JAXB_HOME="D:/DISTR/JAVA_J2EE/JAXB/2.1.13/jaxb-ri-20100511"
#JAXB_HOME="D:/DISTR/JAVA_J2EE/JAXB/jaxb-ri-20110601"
#JAXB_HOME="D:/!DISTRIBs/Programming/Java/xml/jaxb-2_1_7/jaxb-ri"

export JAXB_HOME

JAXB_OPTS="-no-header " # don't add timestamp

OUT_DIR="$SRC_DIR/info/xonix/zlo/search/forumxmlprotocol/xjccompiled"

PATH="$JAXB_HOME/bin:$PATH"

#rm -rf $OUT_DIR
sh xjc.sh $JAXB_OPTS -p info.xonix.zlo.search.xmlfp.jaxb_generated -d $SRC_DIR \
    $XSD_DIR/descriptor.xsd \
    $XSD_DIR/message.xsd \
    $XSD_DIR/messages.xsd \
    $XSD_DIR/lastMessageNumber.xsd

#echo "Copying xsd"
#copy *.xsd D:/TEST/JAVA/ZloSearcher/trunk/zlo_web/resources/xmlfp/xsd

#pause
