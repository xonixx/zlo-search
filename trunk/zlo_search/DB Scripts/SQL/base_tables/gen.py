from os.path import exists, join
import os

SITE = 'x'

SQLS = [ 'create_db_dict.sql',
         'create_messages_table.sql',
         'create_table_nickhost.sql',
]

CWD = os.path.dirname(__file__)
print('CWD:', CWD)

if not exists(SITE):
    os.makedirs(SITE)

import re

for sql_file in SQLS:
    with open(sql_file) as inp:
        sql = inp.read()
        with open(join(CWD, join(SITE, sql_file)), 'w') as out:
            sql = re.sub('\\b(db_dict)\\b', SITE + '_\\1', sql)
            sql = re.sub('\\b(messages)\\b', SITE + '_\\1', sql)
            sql = re.sub('\\b(nickhost)\\b', SITE + '_\\1', sql)
            sql = re.sub('\\b(trigger_nickhost)\\b', SITE + '_\\1', sql)
            out.write(sql)



    