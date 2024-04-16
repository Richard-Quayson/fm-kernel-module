# Python script to use the kernel module

# Define the path to the proc file
proc_file_path = '/proc/retrieve_file_details'

# Define the file path you want to retrieve information about
file_path = '/home/richard/Documents/test-file.txt'

# Write the file path to the proc file
with open(proc_file_path, 'w') as proc_file:
    proc_file.write(file_path)

# Read the response from the proc file
with open(proc_file_path, 'r') as proc_file:
    # Read the file information returned by the kernel module
    file_info = proc_file.read()

# Print the file information
print(f"File information for {file_path}:\n{file_info}")
