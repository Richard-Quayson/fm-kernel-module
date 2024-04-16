#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>          // For file system operations
#include <linux/proc_fs.h>     // For proc_create and proc_remove
#include <linux/uaccess.h>     // For copy_from_user and copy_to_user
#include <linux/slab.h>        // For kzalloc and kfree
#include <linux/namei.h>       // For file operations (e.g., kern_path, LOOKUP_FOLLOW)
#include <linux/stat.h>        // For file statistics (e.g., struct stat)
#include <linux/time.h>        // For time operations (e.g., timespec64_to_ns)
#include <linux/dcache.h>      // For dentry and path operations

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

// Function to retrieve file information
static ssize_t get_file_info(const char *file_path, char *buffer, size_t buffer_size) {
    struct path file_path_struct;
    struct kstat file_stat;
    int ret;

    // Resolve the file path
    ret = kern_path(file_path, LOOKUP_FOLLOW, &file_path_struct);
    if (ret != 0) {
        printk(KERN_ERR "Failed to resolve file path: %d\n", ret);
        return ret;
    }

    // Retrieve file statistics using vfs_getattr
    ret = vfs_getattr(&file_path_struct, &file_stat, STATX_BASIC_STATS, AT_STATX_SYNC_AS_STAT);
    if (ret != 0) {
        printk(KERN_ERR "Failed to retrieve file statistics: %d\n", ret);
        path_put(&file_path_struct);
        return ret;
    }

    // Retrieve file details
    struct dentry *dentry = file_path_struct.dentry;
    const char *file_name = dentry->d_name.name;
    mode_t file_type = dentry->d_inode->i_mode;
    u64 size = file_stat.size;
    u64 date_created = timespec64_to_ns(&file_stat.ctime);
    u64 date_modified = timespec64_to_ns(&file_stat.mtime);

    // Format the file information as a string
    int len = snprintf(buffer, buffer_size,
                       "File Name: %s\nFile Type: 0x%04o\nFile Path: %s\nDate Created: %llu\nDate Modified: %llu\nSize: %llu bytes\n",
                       file_name, file_type, file_path, date_created, date_modified, size);

    // Clean up
    path_put(&file_path_struct);

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
    proc_entry = proc_create("file_info", 0666, NULL, &pops);
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
