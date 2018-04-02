import os
import subprocess
import shutil


#CONFIGS####################################################################
OS_NAME = 'win'
COPY_FROM_FRONT_END = False
START_SERVER_IN_THE_END = True

if OS_NAME == 'win':
    webclientfiles = 'e:\\001\Documents\Programming\IdeaProjects\js\\reactjs\helloworld\\build\\'
    webapp = 'e:\\001\Documents\Programming\IdeaProjects\gradletutorial\src\main\webapp\pages\\'
    warpath = 'e:\\001\Documents\Programming\IdeaProjects\gradletutorial\\build\libs\ROOT.war'
    serverfolder = 'c:\\apache-tomcat-7.0.67\webapps\\'
    startServerScript = serverfolder + 'startup.lnk'

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
shutil.copy2(warpath, serverfolder)

if START_SERVER_IN_THE_END is True:
    print("Starting server")
    if OS_NAME == 'win':
        subprocess.call(['start', startServerScript], shell=True)
else:
    print("Server won't be started")
