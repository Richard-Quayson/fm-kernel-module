#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>         // For file system operations
#include <linux/proc_fs.h>    // For proc_create and proc_remove
#include <linux/uaccess.h>    // For copy_from_user and copy_to_user
#include <linux/slab.h>       // For kzalloc and kfree
#include <linux/time.h>       // For time operations (e.g., timespec64_to_ns)
#include <linux/dcache.h>     // For dentry and path operations
#include <linux/buffer_head.h> // For struct buffer_head and file I/O operations
#include <asm/segment.h>      // For set_fs and get_fs
#include <linux/iomap.h>      // For i_size_read

// Module metadata
MODULE_AUTHOR("Your Name");
MODULE_DESCRIPTION("File Information Kernel Module");
MODULE_LICENSE("GPL");

// Declaration of the proc file entry
static struct proc_dir_entry *proc_entry;

// Buffer to hold the file path written by the user
static char *file_path_buffer;

// Buffer to hold the file information to be returned
static char *file_info_buffer;
static size_t file_info_buffer_size = 4096;  // Adjust as needed

// Function to open a file using VFS
struct file *file_open(const char *path, int flags, int rights) {
    struct file *file = NULL;
    mm_segment_t old_fs;
    int err;

    old_fs = get_fs();
    set_fs(KERNEL_DS); // Change segment descriptor to kernel data segment
    file = filp_open(path, flags, rights);
    set_fs(old_fs); // Restore original segment descriptor
    if (IS_ERR(file)) {
        err = PTR_ERR(file);
        printk(KERN_ERR "Failed to open file: %d\n", err);
        return NULL;
    }
    return file;
}

// Function to close a file using VFS
void file_close(struct file *file) {
    filp_close(file, NULL);
}

// Function to read data from a file using VFS
int file_read(struct file *file, unsigned long long offset, unsigned char *data, unsigned int size) {
    mm_segment_t old_fs;
    int ret;

    old_fs = get_fs();
    set_fs(KERNEL_DS);
    ret = vfs_read(file, data, size, &offset);
    set_fs(old_fs);
    return ret;
}

// Function to retrieve file information
static ssize_t get_file_info(const char *file_path, char *buffer, size_t buffer_size) {
    struct file *file;
    int ret;

    // Open the file using VFS
    file = file_open(file_path, O_RDONLY, 0);
    if (!file) {
        printk(KERN_ERR "Failed to open file: %s\n", file_path);
        return -EIO;
    }

    // Retrieve file details
    struct dentry *dentry = file->f_path.dentry;
    const char *file_name = dentry->d_name.name;
    mode_t file_type = dentry->d_inode->i_mode;
    u64 size = i_size_read(dentry->d_inode);
    u64 date_created = timespec64_to_ns(&dentry->d_inode->i_ctime);
    u64 date_modified = timespec64_to_ns(&dentry->d_inode->i_mtime);

    // Format the file information as a string
    int len = snprintf(buffer, buffer_size,
                       "File Name: %s\nFile Type: 0x%04o\nFile Path: %s\nDate Created: %llu\nDate Modified: %llu\nSize: %llu bytes\n",
                       file_name, file_type, file_path, date_created, date_modified, size);

    // Clean up and close the file
    file_close(file);

    return len;
}

// Proc write function to handle file path input
static ssize_t file_info_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *offset) {
    ssize_t ret;

    // Allocate memory for file path buffer
    file_path_buffer = kzalloc(count + 1, GFP_KERNEL);
    if (!file_path_buffer) {
        return -ENOMEM;
    }

    // Copy data from user space to kernel space
    if (copy_from_user(file_path_buffer, user_buffer, count)) {
        kfree(file_path_buffer);
        return -EFAULT;
    }

    // Null-terminate the buffer
    file_path_buffer[count] = '\0';

    // Allocate memory for file information buffer
    file_info_buffer = kzalloc(file_info_buffer_size, GFP_KERNEL);
    if (!file_info_buffer) {
        kfree(file_path_buffer);
        return -ENOMEM;
    }

    // Call the function to get file information
    ret = get_file_info(file_path_buffer, file_info_buffer, file_info_buffer_size);
    if (ret < 0) {
        kfree(file_path_buffer);
        kfree(file_info_buffer);
        return ret;
    }

    // Free file path buffer as we no longer need it
    kfree(file_path_buffer);

    // Store the size of file information for subsequent reads
    file_info_buffer_size = ret;
    return count;
}

// Proc read function to return file information to the user
static ssize_t file_info_read(struct file *file, char __user *user_buffer, size_t count, loff_t *offset) {
    // Determine the amount of data to read
    size_t len = file_info_buffer_size - *offset;

    // Limit the data to be read based on the count
    len = min(len, count);

    // Copy data from kernel space to user space
    if (copy_to_user(user_buffer, file_info_buffer + *offset, len)) {
        return -EFAULT;
    }

    // Update offset
    *offset += len;

    // Free file information buffer if we've read all the data
    if (*offset >= file_info_buffer_size) {
        kfree(file_info_buffer);
        file_info_buffer = NULL;
    }

    return len;
}

// Proc file operations structure
static const struct proc_ops pops = {
    .proc_write = file_info_write,
    .proc_read = file_info_read,
};

// Module initialization function
static int __init file_info_init(void) {
    // Create the proc entry
    proc_entry = proc_create("retrieve_file_details", 0666, NULL, &pops);
    if (!proc_entry) {
        printk(KERN_ERR "Failed to create /proc entry\n");
        return -ENOMEM;
    }

    printk(KERN_INFO "File Information Kernel Module loaded.\n");
    return 0;
}

// Module cleanup function
static void __exit file_info_exit(void) {
    // Remove the proc entry if it exists
    if (proc_entry) {
        proc_remove(proc_entry);
    }

    printk(KERN_INFO "File Information Kernel Module unloaded.\n");
}

// Register module init and exit functions
module_init(file_info_init);
module_exit(file_info_exit);
