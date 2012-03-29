from os.path import exists, join
import os

#SITE = 'x'
#SITE = 'dolgopa'
#SITE = 'votalka'
SITE = 'x_mipt_cc'

SQLS = [ 'create_db_dict.sql',
         'create_messages_table.sql',
         'create_table_nickhost.sql',
         'create_autocomplete.sql',
         'creare_topics_table.sql',
]

TBLS = ['db_dict',
        'messages',
        'nickhost',
        'trigger_nickhost',
        'autocomplete',
        'topics',
        ]

CWD = os.path.dirname(__file__)
print('CWD:', CWD)

if not exists(SITE):
    os.makedirs(SITE)

import re

all_sql = []

for sql_file in SQLS:
    with open(sql_file) as inp:
        sql = inp.read()
        with open(join(CWD, join(SITE, sql_file)), 'w') as out:

            for t in TBLS:
                sql = re.sub('\\b(%s)\\b' % t, SITE + '_\\1', sql)

            all_sql.append(sql)
            out.write(sql)

with open(join(CWD, join(SITE, '__all.sql')), 'w') as out:
    out.write('\n\n'.join(all_sql))



    