# Python script to use the kernel module
folder_path = "/home/richard/Documents/test-create"

# Open the /proc/create_folder file for writing
with open('/proc/create_folder', 'w') as proc_file:
    proc_file.write(folder_path)

print(f"Requested the creation of folder: {folder_path}")
