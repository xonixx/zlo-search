from suds.client import Client

c = Client('http://localhost:8080/ws/search?wsdl')

print c

service = c.service

print 'Last saved number, zlo:', service.getLastSavedMsgNumber(0)
print
print 'Last indexed number, zlo:', service.getLastIndexedMsgNumber(0)
print
print 'Msg#1, zlo:', service.getMessage(0, 1).body.encode('cp1251')
print

print 'Getting all messages for search by nick:xonix :'
print

total = 0
last_id = 0

while True:
    print
    print 'fetching next part...'
    print

    l = service.searchShallowStartingId(0, 'nick:xonix', 100, last_id)

    if len(l) == 0:
        break

    total += len(l)

    for m in l:
        print '#%s : [%s] %s' % (m.id, m.topic.encode('cp1251'), m.title.encode('cp1251'))

    last_id = l[-1].id

print 'Total xonix msgs:', total