def rename_file(current_path, new_name):
    proc_file_path = "/proc/rename_file"

    # Create the full buffer by combining current path and new name with a newline separator
    buffer = f"{current_path}\n{new_name}\n"

    try:
        # Open the proc file for writing
        with open(proc_file_path, "w") as proc_file:
            # Write the buffer to the proc file
            proc_file.write(buffer)
            print(f"Successfully sent rename request: {current_path} -> {new_name}")

    except FileNotFoundError:
        print(f"Error: Cannot open {proc_file_path}. The proc file may not exist.")
    except PermissionError:
        print(f"Error: Permission denied. Make sure to run the script with sufficient permissions.")
    except Exception as e:
        print(f"An error occurred while renaming the file: {e}")

# Example usage
if __name__ == "__main__":
    # Define the current file path and new file name
    current_path = "/home/richard/Documents/test-file.txt"
    new_name = "renamed-file.txt"
    
    # Call the function to rename the file
    rename_file(current_path, new_name)
