#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>
#include <linux/proc_fs.h>
#include <linux/uaccess.h>
#include <linux/slab.h>
#include <linux/err.h>
#include <linux/namei.h>

// Module metadata
MODULE_AUTHOR("Richard Quayson & Thomas Quarshie");
MODULE_DESCRIPTION("Rename File Kernel Module");
MODULE_LICENSE("GPL");

// Proc file entry
static struct proc_dir_entry *proc_entry;

// Function to handle file renaming
static ssize_t rename_file_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *offset) {
    char *buffer;
    char *current_path = NULL;
    char *new_name = NULL;
    char *newline_pos = NULL;
    char *new_file_path = NULL;

    // Allocate memory for the buffer
    buffer = kzalloc(count + 1, GFP_KERNEL);
    if (!buffer) {
        return -ENOMEM;
    }

    // Copy data from user space to kernel space
    if (copy_from_user(buffer, user_buffer, count)) {
        kfree(buffer);
        return -EFAULT;
    }

    buffer[count] = '\0'; // Null-terminate the buffer

    // Locate the newline character ('\n') in the buffer
    newline_pos = strchr(buffer, '\n');
    if (!newline_pos) {
        kfree(buffer);
        return -EINVAL; // Invalid input, expected a newline character
    }

    // Split the buffer into current path and new name
    *newline_pos = '\0';
    current_path = buffer;
    new_name = newline_pos + 1;

    // Remove any trailing newline from new name
    size_t new_name_length = strlen(new_name);
    if (new_name[new_name_length - 1] == '\n') {
        new_name[new_name_length - 1] = '\0';
    }

    // Find the last slash to determine the directory part of the current path
    char *last_slash = strrchr(current_path, '/');
    if (!last_slash) {
        kfree(buffer);
        return -EINVAL; // Invalid current path
    }

    // Determine the length of the directory part
    size_t dir_length = last_slash - current_path + 1; // Including the last slash

    // Allocate memory for the new file path
    new_file_path = kzalloc(dir_length + strlen(new_name) + 1, GFP_KERNEL);
    if (!new_file_path) {
        kfree(buffer);
        return -ENOMEM;
    }

    // Construct the new file path
    strncpy(new_file_path, current_path, dir_length); // Copy the directory part
    strcat(new_file_path, new_name); // Append the new file name
    
    // Log old file path
    printk(KERN_INFO "Old file path: %s\n", current_path);

    // Look up the current file path
    struct path old_path;
    int err = kern_path(current_path, LOOKUP_FOLLOW, &old_path);
    if (err) {
        printk(KERN_ERR "Failed to find the old file path: %d\n", err);
        kfree(buffer);
        kfree(new_file_path);
        return err;
    }
    
    // Log new file path
    printk(KERN_INFO "New file path: %s\n", new_file_path);

    // Ensure the new file path is within the same parent directory as the old file path
    struct dentry *old_parent_dentry = old_path.dentry->d_parent;
    
    // Check new file path within the same parent directory as the old file path
    if (!old_parent_dentry) {
        printk(KERN_ERR "Old parent directory not found\n");
        path_put(&old_path);
        kfree(new_file_path);
        kfree(buffer);
        return -EINVAL;
    }
    
    // Create the new dentry for the new file path
    struct dentry *new_dentry = lookup_one_len(new_name, old_parent_dentry, strlen(new_name));
    
    if (IS_ERR(new_dentry)) {
        printk(KERN_ERR "Failed to look up the new file name: %ld\n", PTR_ERR(new_dentry));
        path_put(&old_path);
        kfree(new_file_path);
        kfree(buffer);
        return PTR_ERR(new_dentry);
    }
    
    // Initialize rename data structure for vfs_rename call
    struct renamedata rd;
    rd.old_mnt_idmap = old_path.mnt->mnt_idmap;
    rd.old_dir = old_path.dentry->d_parent->d_inode;
    rd.old_dentry = old_path.dentry;
    rd.new_mnt_idmap = old_path.mnt->mnt_idmap;
    rd.new_dir = old_path.dentry->d_parent->d_inode;
    rd.new_dentry = new_dentry;
    rd.delegated_inode = NULL;
    rd.flags = 0;

    // Call vfs_rename function
    err = vfs_rename(&rd);
    if (err == 0) {
        printk(KERN_INFO "File renamed successfully: %s -> %s\n", current_path, new_name);
        err = count;
    } else {
        printk(KERN_ERR "Failed to rename file: %d\n", err);
    }

    // Release resources and free allocated memory
    path_put(&old_path);
    dput(new_dentry);
    kfree(new_file_path);
    kfree(buffer);

    return err;
}

// Proc file operations structure
static const struct proc_ops pops = {
    .proc_write = rename_file_write,
};

// Module initialization function
static int __init rename_file_init(void) {
    proc_entry = proc_create("rename_file", 0222, NULL, &pops);
    if (!proc_entry) {
        printk(KERN_ERR "Failed to create /proc entry\n");
        return -ENOMEM;
    }

    printk(KERN_INFO "Rename File Kernel Module loaded.\n");
    return 0;
}

// Module cleanup function
static void __exit rename_file_exit(void) {
    if (proc_entry) {
        proc_remove(proc_entry);
    }

    printk(KERN_INFO "Rename File Kernel Module unloaded.\n");
}

module_init(rename_file_init);
module_exit(rename_file_exit);
