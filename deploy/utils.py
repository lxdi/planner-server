def copying(sourceDir, destinationDir):
    src_files = os.listdir(sourceDir)
    for file_name in src_files:
        full_file_name = os.path.join(sourceDir, file_name)
        if (os.path.isfile(full_file_name)):
            print(full_file_name + " to " +destinationDir)
            shutil.copy2(full_file_name, destinationDir)
        else:
            nextdestinationDir = os.path.join(destinationDir, file_name)
            print("/// copying from "+ full_file_name +" to " + nextdestinationDir)
            if not os.path.exists(nextdestinationDir):
                os.makedirs(nextdestinationDir)
            copying(full_file_name, nextdestinationDir)
