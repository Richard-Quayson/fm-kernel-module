#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>
#include <linux/proc_fs.h>
#include <linux/uaccess.h>
#include <linux/namei.h>
#include <linux/err.h>
#include <linux/slab.h>
#include <linux/dcache.h>


// module metadata
MODULE_AUTHOR("Richard Quayson & Thomas Quarshie");
MODULE_DESCRIPTION("Create Folder Kernel Module");
MODULE_LICENSE("GPL");


// proc file entry
static struct proc_dir_entry* proc_entry;


/**
 * create_folder_write
 * this function is called when the /proc/create_folder file is written to
 * it creates a folder with the specified path
 * 
 * @param file: file pointer
 * @param user_buffer: user buffer
 * @param count: number of bytes to write
 * @param offset: file offset
 * @return ssize_t: number of bytes written
*/

static ssize_t create_folder_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *offset) {
    char *buffer;
    char *folder_path = NULL;
    char *parent_path = NULL;
    char *folder_name = NULL;
    struct path dir_path;
    struct qstr qstr;

    // copy data from user space
    buffer = kzalloc(count + 1, GFP_KERNEL);
    if (!buffer) {
        return -ENOMEM;
    }

    // copy data from user space to kernel space
    if (copy_from_user(buffer, user_buffer, count)) {
        kfree(buffer);
        return -EFAULT;
    }

    buffer[count] = '\0';

    // separate the path into parent directory and folder name
    folder_path = kzalloc(strlen(buffer) + 2, GFP_KERNEL);      // add 2 for leading '/' and null terminator
    if (!folder_path) {
        kfree(buffer);
        return -ENOMEM;
    }

    sprintf(folder_path, "/%s", buffer);                        // prepend the '/' to the folder path
    
    // find the last '/' in the path to separate parent path and folder name
    char *last_slash = strrchr(folder_path, '/');
    if (last_slash == NULL) {
        kfree(buffer);
        kfree(folder_path);
        return -EINVAL;                                         // invalid argument since the path must contain '/'
    }

    // split the path into parent directory and folder name
    *last_slash = '\0';
    parent_path = folder_path;
    folder_name = last_slash + 1;

    // resolve the parent path
    printk(KERN_INFO "Requested parent directory path: %s\n", parent_path);
    int ret = kern_path(parent_path, LOOKUP_DIRECTORY, &dir_path);
    if (ret != 0) {
        kfree(buffer);
        kfree(folder_path);
        printk(KERN_ERR "Failed to resolve parent directory path: %d\n", ret);
        return ret;
    }

    // create qstr for the folder name
    qstr.name = folder_name;
    qstr.len = strlen(folder_name);

    // find the dentry for the parent directory
    struct dentry *parent_dentry = dir_path.dentry;
    // find or create the dentry for the new folder
    struct dentry *new_folder_dentry = d_lookup(parent_dentry, &qstr);

    // if the dentry for the new folder does not exist, create it
    if (!new_folder_dentry) {
        new_folder_dentry = d_alloc_name(parent_dentry, folder_name);
        if (!new_folder_dentry) {
            kfree(buffer);
            kfree(folder_path);
            path_put(&dir_path);
            printk(KERN_ERR "Failed to allocate dentry for the new folder.\n");
            return -ENOMEM;
        }

        // link the dentry with the parent directory
        d_instantiate(new_folder_dentry, NULL);
    }

    // create the directory
    ret = vfs_mkdir(dir_path.mnt->mnt_idmap, dir_path.dentry->d_inode, new_folder_dentry, 0755);
    if (ret != 0) {
        printk(KERN_ERR "Failed to create directory: %d\n", ret);
    } else {
        printk(KERN_INFO "Directory created successfully: %s\n", buffer);
    }

    // free allocated memory
    path_put(&dir_path);
    kfree(buffer);
    kfree(folder_path);

    // return the number of bytes written
    return ret == 0 ? count : ret;
}


/**
 * pops
 * this structure defines the file operations for the /proc/create_folder file
*/

static const struct proc_ops pops = {
    .proc_write = create_folder_write,
};


/**
 * create_folder_init
 * this function is called when the module is loaded
 * it creates the /proc/create_folder file
 * 
 * @param void
 * @return int: 0 if successful, otherwise the error code
*/

static int __init create_folder_init(void) {
    proc_entry = proc_create("create_folder", 0222, NULL, &pops);
    if (!proc_entry) {
        printk(KERN_ERR "Failed to create /proc entry\n");
        return -ENOMEM;
    }

    printk(KERN_INFO "Create Folder Kernel Module loaded.\n");
    return 0;
}


/**
 * create_folder_exit
 * this function is called when the module is unloaded
 * it removes the /proc/create_folder file
 * 
 * @param void
 * @return void
*/

static void __exit create_folder_exit(void) {
    if (proc_entry) {
        proc_remove(proc_entry);
    }

    printk(KERN_INFO "Create Folder Kernel Module unloaded.\n");
}

// register the module initialization and cleanup functions
module_init(create_folder_init);
module_exit(create_folder_exit);