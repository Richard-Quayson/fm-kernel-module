#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>         
#include <linux/uaccess.h>   
#include <linux/proc_fs.h>   
#include <linux/dcache.h>  
#include <linux/err.h>
#include <linux/slab.h>
#include <linux/namei.h>
#include <linux/path.h>


// module metadata
MODULE_AUTHOR("Richard Quayson & Thomas Quarshie");
MODULE_DESCRIPTION("Delete File Kernel Module");
MODULE_LICENSE("GPL");


// struct to hold the mount idmap
static struct proc_dir_entry *proc_entry;


/**
 * delete_file
 * this function is called to delete a file at the specified path
 * 
 * @param file_path: path to the file to delete
 * @return int: 0 if successful, error code otherwise
*/

static int delete_file(const char *file_path) {
    struct path path;
    int ret;

    // resolve the file path
    ret = kern_path(file_path, LOOKUP_FOLLOW, &path);
    if (ret != 0) {
        printk(KERN_ERR "Failed to resolve file path: %d\n", ret);
        return ret;
    }

    // retrieve `mnt_idmap` from `path`
    struct mnt_idmap *mnt_idmap = path.mnt->mnt_idmap;

    // perform unlink operation
    ret = vfs_unlink(mnt_idmap, path.dentry->d_parent->d_inode, path.dentry, NULL);
    if (ret != 0) {
        printk(KERN_ERR "Failed to delete file: %d\n", ret);
        path_put(&path);
        return ret;
    }

    // clean up
    path_put(&path);
    printk(KERN_INFO "File deleted successfully: %s\n", file_path);
    return 0;
}


/**
 * delete_file_write
 * this function is called when the /proc/delete_file file is written to
 * it deletes the file at the specified path
 * 
 * @param file: file pointer
 * @param user_buffer: user buffer
 * @param count: number of bytes to write
 * @param offset: file offset
 * @return ssize_t: number of bytes written
*/

static ssize_t delete_file_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *offset) {
    char *buffer;

    // allocate memory for buffer
    buffer = kzalloc(count + 1, GFP_KERNEL);
    if (!buffer) {
        return -ENOMEM;
    }

    // copy data from user space to kernel space
    if (copy_from_user(buffer, user_buffer, count)) {
        kfree(buffer);
        return -EFAULT;
    }

    // null-terminate the buffer
    buffer[count] = '\0';

    // call the function to delete the file
    int ret = delete_file(buffer);

    // free the buffer memory
    kfree(buffer);

    // return the number of bytes written or the error code
    return ret == 0 ? count : ret;
}


/**
 * pops
 * struct to hold the proc_ops for the /proc/delete_file file
*/

static const struct proc_ops pops = {
    .proc_write = delete_file_write,
};


/**
 * delete_file_init
 * this function is called when the module is loaded
 * it creates the /proc/delete_file file
 * 
 * @param void
 * @return int: 0 if successful, error code otherwise
*/

static int __init delete_file_init(void) {
    proc_entry = proc_create("delete_file", 0222, NULL, &pops);
    if (!proc_entry) {
        printk(KERN_ERR "Failed to create /proc entry\n");
        return -ENOMEM;
    }

    printk(KERN_INFO "Delete File Kernel Module loaded.\n");
    return 0;
}


/**
 * delete_file_exit
 * this function is called when the module is unloaded
 * it removes the /proc/delete_file file
 * 
 * @param void
 * @return void
*/

static void __exit delete_file_exit(void) {
    // Remove the proc entry if it exists
    if (proc_entry) {
        proc_remove(proc_entry);
    }

    printk(KERN_INFO "Delete File Kernel Module unloaded.\n");
}


// register the module entry and exit points
module_init(delete_file_init);
module_exit(delete_file_exit);