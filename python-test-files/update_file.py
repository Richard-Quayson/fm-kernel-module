# Import necessary modules
import os

# Define the file path and the new data to append
file_path = "/home/richard/Documents/test-file.txt"
new_data = "Adding an extra line to the file\n"

# Set the overwrite flag to False (0) for appending data
overwrite_flag = 0 # 0 for append, 1 for overwrite

# Prepare the input string for the kernel module
# The format is: file_path | new_data | overwrite_flag
input_data = f"{file_path}|{new_data}|{overwrite_flag}"

# Specify the path to the kernel module
proc_update_file_path = "/proc/update_file"

# Open the proc file for writing
with open(proc_update_file_path, "w") as proc_file:
    # Write the input data to the proc file
    proc_file.write(input_data)

# Print a message indicating that the update is complete
print(f"Appended line to {file_path} using kernel module.")
