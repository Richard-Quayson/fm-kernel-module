def delete_file_via_kernel_module(file_path):
    proc_path = "/proc/delete_file"  # The proc entry from the kernel module

    try:
        # Open the proc entry in write mode
        with open(proc_path, "w") as proc_file:
            # Write the file path you want to delete
            proc_file.write(file_path)
            print(f"Requested deletion of file: {file_path}")
    except IOError as e:
        print(f"An error occurred while trying to delete the file: {e}")

# Specify the path of the file you want to delete
file_to_delete = "/home/richard/Documents/test-file.txt"

# Call the function to delete the file
delete_file_via_kernel_module(file_to_delete)
