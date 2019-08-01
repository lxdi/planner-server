import os
import shutil
import subprocess
import sys
from properties import *

destination = tomcatPath + '\\webapps\\ROOT\\pages'
source = 'build'
onlyCopy = False

for arg in sys.argv[1:]:
    if (arg == '--only-copy'):
        onlyCopy = True

os.chdir(os.path.join('frontend'))
if (onlyCopy is False):
    print("-----------------------------------------Compilation----------------------------------")
    process = subprocess.Popen(['npm', 'start'], shell=True, stdout = subprocess.PIPE)
    for line in process.stdout:
        print(line)
    process.wait()

    if process.returncode!=0 :
        print("------------------------------------Compilation ERROR----------------------------------------------------")
        exit(process.returncode)

print("-----------------------------------------Copying----------------------------------")
src_files = os.listdir(source)
for file_name in src_files:
    full_file_name = os.path.join(source, file_name)
    if (os.path.isfile(full_file_name)):
        print(full_file_name + " to " +destination)
        shutil.copy2(full_file_name, destination)

print("Success")
