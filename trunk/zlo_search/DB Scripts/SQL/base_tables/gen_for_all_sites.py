from os.path import exists, join, splitext
import os
import re

import cfg

SITES = cfg.ALL_SITE_NAMES

SQLS = ['create_autocomplete.sql',
        ]

TBLS = ['db_dict',
        'messages',
        'nickhost',
        'trigger_nickhost',
        'autocomplete',
        ]

CWD = os.path.dirname(__file__)
print('CWD:', CWD)

for sql_file in SQLS:
    a = splitext(sql_file)[0]
    sql_all_file = a + '__all.sql'

    base_sql_code = open(sql_file).read()

    with open(sql_all_file, 'w') as out:
        for site in SITES:
            print 'Processing:', site

            sql = base_sql_code

            for t in TBLS:
                sql = re.sub('\\b(%s)\\b' % t, site + '_\\1', sql)

            out.write('-- === %s ===' % site)
            out.write(sql)
            out.write('\n\n')

