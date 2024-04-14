# Python script to use the kernel module

# Define the paths
src_folder_path = "/home/richard/Documents/test-create"
new_name = "test-rename"

# Prepare the input for the kernel module
# The expected input format is: "source_path destination_path"
input_data = f"{src_folder_path}\n{new_name}\n"

# Open the /proc/rename_folder file for writing
with open('/proc/rename_folder', 'w') as proc_file:
    proc_file.write(input_data)

print(f"Requested the renaming of folder from '{src_folder_path}' to '{new_name}'")
