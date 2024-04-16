def delete_folder_via_kernel_module(folder_path):
    proc_path = "/proc/delete_folder"  # Path to the kernel module

    try:
        # Open the proc file in write mode
        with open(proc_path, "w") as proc_file:
            # Write the folder path you want to delete
            proc_file.write(folder_path)
            print(f"Requested deletion of folder: {folder_path}")
    except IOError as e:
        print(f"An error occurred while trying to delete the folder: {e}")

# Specify the path of the folder you want to delete
folder_to_delete = "/home/richard/Documents/test-folder"

# Call the function to delete the folder
delete_folder_via_kernel_module(folder_to_delete)
