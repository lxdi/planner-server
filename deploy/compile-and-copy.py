import os
import shutil
import subprocess
import sys
from properties import *

warSubPath = os.path.join('backend', 'build', 'libs', warName)
destination = os.path.join(tomcatPath, 'webapps')
onlyCopy = False
notRun = False

if projectRoot is None:
    # in case of when the script is called from the project root dir
    projectRoot = os.getcwd()

os.chdir(projectRoot)

#handle arguments
for arg in sys.argv[1:]:
    if (arg == '--only-copy'):
        onlyCopy = True
    if (arg == '--not-run'):
        notRun = True

contextXmlTomcat = os.path.join(tomcatPath, 'conf', 'context.xml')
if os.path.exists(contextXmlTomcat):
    print("-----------------------------------------Removing context.xml in tomcat----------------------------------")
    os.remove(contextXmlTomcat)

print("-----------------------------------------Copying context.xml to tomcat----------------------------------")
shutil.copy2(os.path.join('backend', 'tomcat', 'context.xml'), contextXmlTomcat)

if (onlyCopy is False):
    print("-----------------------------------------Compilation----------------------------------")
    process = subprocess.Popen(['gradle', 'clean', 'war'], shell=True, stdout = subprocess.PIPE)
    for line in process.stdout:
        print(line)
    process.wait()

    if process.returncode!=0 :
        print("------------------------------------Compilation ERROR----------------------------------------------------")
        exit(process.returncode)

print("-----------------------------------------Cleaning webapps dir----------------------------------")
if os.path.exists(destination):
    shutil.rmtree(destination, ignore_errors=True)

os.makedirs(destination)

print("-----------------------------------------Copying----------------------------------")
shutil.copy2(warSubPath, destination)

if notRun is False:
    print("-----------------------------------------Run tomcat----------------------------------")
    os.chdir(os.path.join(tomcatPath, 'bin'))
    #subprocess.Popen(tomcatPath+'\\bin\\'+runScript)
    subprocess.Popen(runScript)

print("-----------------------------------------SUCCESS-----------------------------------------")
