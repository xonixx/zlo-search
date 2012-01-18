# coding: UTF-8

f = r'D:\stuff\test\java\zlo-search\zlo_search\src\main\java\info\xonix\zlo\search\utils\obscene\obscene.txt'

words = open(f).read().split()

#print ', '.join(words)

sql_select = '''
SELECT * FROM zlo_autocomplete WHERE search_text RLIKE '%s'
''' % '|'.join('[[:<:]]%s' % w for w in words)

sql_delete = '''
DELETE FROM zlo_autocomplete WHERE search_text RLIKE '%s'
''' % '|'.join('[[:<:]]%s' % w for w in words)

print sql_select
print sql_delete
