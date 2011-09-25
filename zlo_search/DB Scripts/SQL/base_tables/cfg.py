import glob
from os.path import split, splitext

CONF_D = '../../../src/main/java/info/xonix/zlo/search/config'

ALL_SITE_NAMES = [
    splitext(split(p)[1])[0]
    for p in glob.glob('%s/*.properties' % CONF_D)
    if 'site.number' in open(p).read()
]

if __name__ == '__main__':
    print ALL_SITE_NAMES