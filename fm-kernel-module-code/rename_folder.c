#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>
#include <linux/proc_fs.h>
#include <linux/uaccess.h>
#include <linux/slab.h>
#include <linux/err.h>
#include <linux/namei.h>


// module metadata
MODULE_AUTHOR("Richard Quayson & Thomas Quarshie");
MODULE_DESCRIPTION("Rename Folder Kernel Module");
MODULE_LICENSE("GPL");


// proc file entry
static struct proc_dir_entry *proc_entry;


/**
 * renamedata
 * function to hold information about the rename operation
 *  
 * @param file *file: file pointer
 * @param user_buffer: user buffer
 * @param count: number of bytes to write
 * @param offset: file offset
 * @return ssize_t: number of bytes written
*/

static ssize_t rename_folder_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *offset) {
    char *buffer;
    char *current_path = NULL;
    char *new_name = NULL;
    char *newline_pos = NULL;
    char *new_folder_path = NULL;

    // allocate memory for the buffer
    buffer = kzalloc(count + 1, GFP_KERNEL);
    if (!buffer) {
        return -ENOMEM;
    }

    // copy data from user space to kernel space
    if (copy_from_user(buffer, user_buffer, count)) {
        kfree(buffer);
        return -EFAULT;
    }

    buffer[count] = '\0';           // null-terminate the buffer

    // locate the newline character ('\n') in the buffer
    newline_pos = strchr(buffer, '\n');
    if (!newline_pos) {
        kfree(buffer);
        return -EINVAL;              // invalid input, expected a newline character
    }

    // split the buffer into current path and new name
    *newline_pos = '\0';
    current_path = buffer;
    new_name = newline_pos + 1;

    // remove any trailing newline from new name
    size_t new_name_length = strlen(new_name);
    if (new_name[new_name_length - 1] == '\n') {
        new_name[new_name_length - 1] = '\0';
    }

    // find the last slash to determine the directory part of the current path
    char *last_slash = strrchr(current_path, '/');
    if (!last_slash) {
        kfree(buffer);
        return -EINVAL;             // invalid current path
    }

    // determine the length of the directory part
    size_t dir_length = last_slash - current_path + 1;      // including the last slash

    // allocate memory for the new folder path
    new_folder_path = kzalloc(dir_length + strlen(new_name) + 1, GFP_KERNEL);
    if (!new_folder_path) {
        kfree(buffer);
        return -ENOMEM;
    }

    // construct the new folder path
    strncpy(new_folder_path, current_path, dir_length);         // copy the directory part
    strcat(new_folder_path, new_name);                          // append the new folder name
    
    // log old folder path
    printk(KERN_INFO "Old folder path: %s\n", current_path);

    // look up the current folder path
    struct path old_path;
    int err = kern_path(current_path, LOOKUP_FOLLOW, &old_path);
    if (err) {
        printk(KERN_ERR "Failed to find the old folder path: %d\n", err);
        kfree(buffer);
        kfree(new_folder_path);
        return err;
    }
    
    // log new folder path
    printk(KERN_INFO "New folder path: %s\n", new_folder_path);

    // ensure the new folder path is within the same parent directory as the old folder path
    struct dentry *old_parent_dentry = old_path.dentry->d_parent;
    
    // check new folder path within the same parent directory as the old folder path
    if (!old_parent_dentry) {
        printk(KERN_ERR "Old parent directory not found\n");
        path_put(&old_path);
        kfree(new_folder_path);
        kfree(buffer);
        return -EINVAL;
    }
    
    // create the new dentry for the new folder path
    struct dentry *new_dentry = lookup_one_len(new_name, old_parent_dentry, strlen(new_name));
    
    if (IS_ERR(new_dentry)) {
        printk(KERN_ERR "Failed to look up the new folder name: %ld\n", PTR_ERR(new_dentry));
        path_put(&old_path);
        kfree(new_folder_path);
        kfree(buffer);
        return PTR_ERR(new_dentry);
    }
    
    // initialize rename data structure for vfs_rename call
    struct renamedata rd;
    rd.old_mnt_idmap = old_path.mnt->mnt_idmap;
    rd.old_dir = old_path.dentry->d_parent->d_inode;
    rd.old_dentry = old_path.dentry;
    rd.new_mnt_idmap = old_path.mnt->mnt_idmap;
    rd.new_dir = old_path.dentry->d_parent->d_inode;
    rd.new_dentry = new_dentry;
    rd.delegated_inode = NULL;
    rd.flags = 0;

    // call vfs_rename function
    err = vfs_rename(&rd);
    if (err == 0) {
        printk(KERN_INFO "Folder renamed successfully: %s -> %s\n", current_path, new_name);
        err = count;
    } else {
        printk(KERN_ERR "Failed to rename folder: %d\n", err);
    }

    // release resources and free allocated memory
    path_put(&old_path);
    dput(new_dentry);
    kfree(new_folder_path);
    kfree(buffer);

    return err;
}


/**
 * pops
 * this structure defines the file operations for the /proc/rename_folder file
*/

static const struct proc_ops pops = {
    .proc_write = rename_folder_write,
};


/**
 * rename_folder_init
 * module initialization function
 * 
 * @param void
 * @return int: 0 for success, error code for failure
*/

static int __init rename_folder_init(void) {
    proc_entry = proc_create("rename_folder", 0222, NULL, &pops);
    if (!proc_entry) {
        printk(KERN_ERR "Failed to create /proc entry\n");
        return -ENOMEM;
    }

    printk(KERN_INFO "Rename Folder Kernel Module loaded.\n");
    return 0;
}


/**
 * rename_folder_exit
 * module exit function
 * 
 * @param void
 * @return void
*/

static void __exit rename_folder_exit(void) {
    if (proc_entry) {
        proc_remove(proc_entry);
    }

    printk(KERN_INFO "Rename Folder Kernel Module unloaded.\n");
}


// register the module initialization and cleanup functions
module_init(rename_folder_init);
module_exit(rename_folder_exit);