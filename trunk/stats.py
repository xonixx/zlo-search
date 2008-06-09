

import os
import os.path
import glob

src_dirs = [ 
	r'D:\TEST\JAVA\ZloSearcher\trunk\zlo_search\src',
	r'D:\TEST\JAVA\ZloSearcher\trunk\zlo_web\src',
	r'D:\TEST\JAVA\ZloSearcher\trunk\zlo_web\WEB-INF']

exts = [
	'.java', '.jsp', '.xml'
]

exclude_dirs = [
	'.svn', 'exploded'
]

TRACE = False

	
def process_dir(_dir):
	if os.path.basename(_dir) in exclude_dirs:
		return
	if TRACE: print 'Scanning', _dir
	for _file in os.listdir(_dir):
		f = _dir + '/' + _file
		if os.path.isdir(f):
			process_dir(f)
		elif os.path.isfile(f) and any([f.endswith(ext) for ext in exts]):
			process_file(f)
			
def process_file(f_name):
	f = open(f_name)
	file_content = f.read()
	f.close()
	if TRACE: print '\tFile:', f_name, get_loc(file_content), get_f_size(f_name)
	ext = get_ext(f_name)
	stat[ext]["loc"] += get_loc(file_content)
	stat[ext]["size"] += get_f_size(f_name)
	stat[ext]["count"] += 1

def get_ext(f_name):
	if '.' in f_name:
		return f_name.rsplit('.', 1)[1]
	else:
		return ''
		
def get_loc(content):
	return len(content.split('\n'))
	
def get_f_size(f_name):
	return os.path.getsize(f_name)
	
def init():
	global stat
	stat = {}
	for ext in exts:
		stat[ext[1:]] = {"count":0, "loc":0, "size":0}

def format_size(size):
	return ''.join(['{', str(size / 1024), 'kb ', str(size % 1024), 'b}'])

def report():
	total_loc = 0
	total_size = 0
	total_cnt = 0
	print "Ext | count | loc | size"
	for ext in stat:
		print " | ".join(str(s) for s in [ext, stat[ext]["count"], stat[ext]["loc"] , format_size(stat[ext]["size"])])
		total_loc += stat[ext]["loc"]
		total_size += stat[ext]["size"]
		total_cnt += stat[ext]["count"]
	print " | ".join(str(s) for s in ["Total", total_cnt, total_loc, format_size(total_size)])
	


if __name__ == '__main__':
	init()
	process_dir('.')
	report()
