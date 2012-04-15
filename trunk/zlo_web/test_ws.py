from suds.client import Client

#OUT_ENCODING = 'cp1251'
OUT_ENCODING = 'UTF-8'

c = Client('http://localhost:8080/ws/search?wsdl')
#c = Client('http://zlo.rt.mipt.ru:7500/ws/search?wsdl')

print c

service = c.service

ZLO_ID = 0

print 'Last saved number, zlo:', service.getLastSavedMsgNumber(ZLO_ID)
print
print 'Last indexed number, zlo:', service.getLastIndexedMsgNumber(ZLO_ID)
print
print 'Msg#1, zlo:', service.getMessage(ZLO_ID, 1).body.encode(OUT_ENCODING)
print

print 'Getting all messages for search by nick:xonix :'
print

total = 0

STEP = 100

while True:
    print
    print 'fetching next %s msgs...' % STEP
    print

    l = service.searchShallow(ZLO_ID, 'nick:xonix', total, STEP)

    if not len(l):
        break

    total += len(l)

    for m in l:
        print '#%s : [%s] %s' % (m.id, m.topic.encode(OUT_ENCODING), m.title.encode(OUT_ENCODING))

print 'Total xonix msgs:', total