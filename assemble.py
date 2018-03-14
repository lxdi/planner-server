import os
import subprocess
import shutil

webclientfiles = '/Users/alexander/Projects/planner-web-client/build'
webapp = '/Users/alexander/Projects/planner-server/src/main/webapp/pages'


serverfolder = '/Users/alexander/Projects/apache-tomcat-7.0.85/webapps'
warpath = '/Users/alexander/Projects/planner-server/build/libs/ROOT.war'

#shutil.copy2(webclientfiles, webapp)
src_files = os.listdir(webclientfiles)
for file_name in src_files:
    full_file_name = os.path.join(webclientfiles, file_name)
    if (os.path.isfile(full_file_name)):
        shutil.copy2(full_file_name, webapp)

print(subprocess.check_output(['gradle', 'war']))
shutil.copy2(warpath, serverfolder)
