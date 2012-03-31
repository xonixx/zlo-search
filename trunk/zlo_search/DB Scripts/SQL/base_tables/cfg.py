#import glob
#from os.path import split, splitext
import re

#CONF_D = '../../../src/main/java/info/xonix/zlo/search/config/forums'
FORUMS_XML = '../../../src/main/java/info/xonix/zlo/search/config/forums/beans.xml'

"""
def globs(gg):
    res = []
    for g in gg:
        res += glob.glob(g)
    return set(res)


ALL_SITE_NAMES = [
    splitext(split(p)[1])[0]
    for p in globs([
        '%s/*.properties' % CONF_D,
        '%s/*/*.properties' % CONF_D])
    if 'db.daemon.period.to.scan' in open(p).read()
]
"""

ALL_SITE_NAMES = [m for m in re.findall(
    r'''<constructor-arg value="\d+"/>\s+<constructor-arg value="(.+?)"''', open(FORUMS_XML).read()) ]

if __name__ == '__main__':
    print ALL_SITE_NAMES