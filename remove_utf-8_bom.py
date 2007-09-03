
import os
import os.path
import glob

UTF_8_BOM = '\xef\xbb\xbf'

def remove_bom_in_dir(_dir):
	#print 'Scanning', _dir
	for _file in os.listdir(_dir):
		f = _dir + '/' + _file
		if os.path.isdir(f):
			remove_bom_in_dir(f)
		elif os.path.isfile(f) and f.endswith(".java"):
			remove_bom(f)
			
def remove_bom(f_name):
	f = open(f_name)
	file_content = f.read()
	f.close()
	if file_content[:3] == UTF_8_BOM:
		print 'UTF-8 BOM found:', f_name
		f = open(f_name, 'wb')
		f.write(file_content[3:])
		f.close()
		
if __name__ == '__main__':
	remove_bom_in_dir('.')