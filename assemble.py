import os
import subprocess
import shutil


#CONFIGS####################################################################
OS_NAME = 'win'
COPY_FROM_FRONT_END = True
START_SERVER_IN_THE_END = False

if OS_NAME == 'win':
    webclientfiles = 'e:\\001\Documents\Programming\IdeaProjects\js\\reactjs\helloworld\\build\\'
    webapp = 'e:\\001\Documents\Programming\IdeaProjects\gradletutorial\src\main\webapp\pages\\'
    warpath = 'e:\\001\Documents\Programming\IdeaProjects\gradletutorial\\build\libs\ROOT.war'
    serverfolder = 'c:\\apache-tomcat-7.0.67\\'

if OS_NAME == 'mac':
    webclientfiles = '/Users/alexander/Projects/planner-web-client/build'
    webapp = '/Users/alexander/Projects/planner-server/src/main/webapp/pages'
    serverfolder = '/Users/alexander/Projects/apache-tomcat-7.0.85/webapps'
    warpath = '/Users/alexander/Projects/planner-server/build/libs/ROOT.war'

#########################################################################

print('Copying all compiled files from the front-end to the server-side')
if COPY_FROM_FRONT_END is True:
    # Copy all compiled files from the front end to the server side
    #shutil.copy2(webclientfiles, webapp)
    src_files = os.listdir(webclientfiles)
    for file_name in src_files:
        full_file_name = os.path.join(webclientfiles, file_name)
        if (os.path.isfile(full_file_name)):
            shutil.copy2(full_file_name, webapp)
else:
    print('- Skipped')

# Run build war file
print('Building war')
print(subprocess.check_output(['gradle', 'war'], shell=True))

# Copy war file to web-server
print('Copy war file to web-server')
shutil.copy2(warpath, serverfolder+'webapps')
print('Deleting ROOT folder')
if OS_NAME == 'win':
    subprocess.call(['rmdir', '/s', '/q', serverfolder + 'webapps\\ROOT'], shell=True)

if START_SERVER_IN_THE_END is True:
    print('Shutdown server')
    if OS_NAME == 'win':
        subprocess.call(['start', serverfolder+'bin\\shutdown.lnk'], shell=True)
    print("Starting server")
    if OS_NAME == 'win':
        subprocess.call(['start', serverfolder+'bin\\startup.lnk'], shell=True)
        #subprocess.Popen(serverfolder+'bin\\catalina.bat', creationflags=subprocess.CREATE_NEW_CONSOLE)
else:
    print("Server won't be started")
