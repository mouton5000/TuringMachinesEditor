import os
import shutil

files = os.listdir("./")
files = [f for f in files if f.startswith('help')]
files.sort()
print(files)

size = len(files)
import math
lsize = (int)(math.log10(size))
print(lsize)
for i, f in reversed(list(enumerate(files))):
  shutil.move(f, ('help%0'+str(lsize + 1)+'d.png') % (i + 1)) 
