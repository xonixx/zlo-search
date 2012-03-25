import glob
from os.path import split, splitext

CONF_D = '../../../src/main/java/info/xonix/zlo/search/config/forums'

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

if __name__ == '__main__':
    print ALL_SITE_NAMES