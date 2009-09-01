
set PATH=D:\!DISTRIBs\Programming\Java\xml\jaxb-2_1_7\jaxb-ri\bin;%PATH%

set SRC_DIR=D:\TEST\JAVA\ZloSearcher\trunk\zlo_search\src
set OUT_DIR=%SRC_DIR%\info\xonix\zlo\search\forumxmlprotocol\xjccompiled

rmdir /S /Q %OUT_DIR%
call xjc.bat -p info.xonix.zlo.search.xmlfp.xjccompiled.message -d %SRC_DIR% message.xsd
call xjc.bat -p info.xonix.zlo.search.xmlfp.xjccompiled.lastMessageNumber -d %SRC_DIR% lastMessageNumber.xsd

echo copying xsd
copy *.xsd D:\TEST\JAVA\ZloSearcher\trunk\zlo_web\resources\xmlfp\xsd

pause
