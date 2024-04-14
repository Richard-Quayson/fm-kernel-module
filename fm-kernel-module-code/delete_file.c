#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>         // For vfs_unlink, path operations
#include <linux/uaccess.h>    // For copy_from_user
#include <linux/proc_fs.h>    // For proc_create and proc_remove
#include <linux/dcache.h>     // For dentry, inode
#include <linux/err.h>        // For error codes
#include <linux/slab.h>       // For kzalloc and kfree
#include <linux/namei.h>      // For kern_path and LOOKUP_FOLLOW
#include <linux/path.h>       // For path_put

// Module metadata
MODULE_AUTHOR("Richard Quayson & Thomas Quarshie");
MODULE_DESCRIPTION("Delete File Kernel Module");
MODULE_LICENSE("GPL");

// Declaration of the proc file entry
static struct proc_dir_entry *proc_entry;

// Function to delete a file given its path
static int delete_file(const char *file_path) {
    struct path path;
    int ret;

    // Resolve the file path
    ret = kern_path(file_path, LOOKUP_FOLLOW, &path);
    if (ret != 0) {
        printk(KERN_ERR "Failed to resolve file path: %d\n", ret);
        return ret;
    }

    // Retrieve `mnt_idmap` from `path`
    struct mnt_idmap *mnt_idmap = path.mnt->mnt_idmap;

    // Perform unlink operation
    ret = vfs_unlink(mnt_idmap, path.dentry->d_parent->d_inode, path.dentry, NULL);
    if (ret != 0) {
        printk(KERN_ERR "Failed to delete file: %d\n", ret);
        path_put(&path);
        return ret;
    }

    // Clean up
    path_put(&path);
    printk(KERN_INFO "File deleted successfully: %s\n", file_path);
    return 0;
}

// Proc write function to handle file path input
static ssize_t delete_file_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *offset) {
    char *buffer;

    // Allocate memory for buffer
    buffer = kzalloc(count + 1, GFP_KERNEL);
    if (!buffer) {
        return -ENOMEM;
    }

    // Copy data from user space to kernel space
    if (copy_from_user(buffer, user_buffer, count)) {
        kfree(buffer);
        return -EFAULT;
    }

    // Null-terminate the buffer
    buffer[count] = '\0';

    // Call the function to delete the file
    int ret = delete_file(buffer);

    // Free the buffer memory
    kfree(buffer);

    // Return the number of bytes written or the error code
    return ret == 0 ? count : ret;
}

// Proc file operations structure
static const struct proc_ops pops = {
    .proc_write = delete_file_write,
};

// Module initialization function
static int __init delete_file_init(void) {
    proc_entry = proc_create("delete_file", 0222, NULL, &pops);
    if (!proc_entry) {
        printk(KERN_ERR "Failed to create /proc entry\n");
        return -ENOMEM;
    }

    printk(KERN_INFO "Delete File Kernel Module loaded.\n");
    return 0;
}

// Module cleanup function
static void __exit delete_file_exit(void) {
    // Remove the proc entry if it exists
    if (proc_entry) {
        proc_remove(proc_entry);
    }

    printk(KERN_INFO "Delete File Kernel Module unloaded.\n");
}

// Register module init and exit functions
module_init(delete_file_init);
module_exit(delete_file_exit);

