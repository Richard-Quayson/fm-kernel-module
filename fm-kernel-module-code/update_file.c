#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>
#include <linux/proc_fs.h>
#include <linux/uaccess.h>
#include <linux/err.h>
#include <linux/slab.h> 
#include <linux/dcache.h> 
#include <linux/namei.h>
#include <linux/file.h> 
#include <linux/fcntl.h> 


// module metadata
MODULE_AUTHOR("Richard Quayson & Thomas Quarshie");
MODULE_DESCRIPTION("Update File Kernel Module");
MODULE_LICENSE("GPL");


// proc file entry
static struct proc_dir_entry *proc_entry;


/**
 * update_file_write
 * this function is called when the /proc/update_file file is written to
 * 
 * @param file: file pointer
 * @param user_buffer: user buffer
 * @param count: number of bytes to write
 * @param offset: file offset
 * @return ssize_t: number of bytes written
*/

static ssize_t update_file_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *offset) {
    char *buffer;
    char *file_path = NULL;
    char *file_content = NULL;
    char *newline_pos = NULL;
    char *overwrite_flag_str = NULL;
    int overwrite_flag;

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

    buffer[count] = '\0';               // null-terminate the buffer

    // locate the pipe character ('|') in the buffer
    newline_pos = strchr(buffer, '|');
    if (!newline_pos) {
        kfree(buffer);
        return -EINVAL;                 // invalid input, expected a newline character
    }

    // split the buffer into file path and content
    *newline_pos = '\0';
    file_path = buffer;
    file_content = newline_pos + 1;

    // locate another pipe character to split content and overwrite flag
    newline_pos = strchr(file_content, '|');
    if (!newline_pos) {
        kfree(buffer);
        return -EINVAL;                 // invalid input, expected a newline character
    }

    *newline_pos = '\0';
    overwrite_flag_str = newline_pos + 1;

    // convert overwrite_flag_str to int
    if (kstrtoint(overwrite_flag_str, 10, &overwrite_flag) != 0) {
        kfree(buffer);
        return -EINVAL;                 // invalid overwrite flag input
    }

    // open the file with the appropriate flags
    int file_flags = O_WRONLY | ((overwrite_flag) ? O_TRUNC : O_APPEND);
    struct file *filp = filp_open(file_path, file_flags, 0644);
    if (IS_ERR(filp)) {
        printk(KERN_ERR "Failed to open file: %ld\n", PTR_ERR(filp));
        kfree(buffer);
        return PTR_ERR(filp);
    }

    // write the content to the file
    ssize_t written = kernel_write(filp, file_content, strlen(file_content), &filp->f_pos);
    if (written < 0) {
        printk(KERN_ERR "Failed to write to file: %ld\n", written);
        filp_close(filp, NULL);
        kfree(buffer);
        return written;
    } else {
        printk(KERN_INFO "File updated successfully: %s\n", file_path);
    }

    // close the file
    filp_close(filp, NULL);

    // free allocated memory
    kfree(buffer);

    // return the number of bytes written
    return written;
}


/**
 * pops
 * proc_ops structure for the /proc/update_file file
*/

static const struct proc_ops pops = {
    .proc_write = update_file_write,
};


/**
 * update_file_init
 * module initialization function
 * 
 * @param void
 * @return int: 0 if successful, otherwise the error code
*/

static int __init update_file_init(void) {
    proc_entry = proc_create("update_file", 0222, NULL, &pops);
    if (!proc_entry) {
        printk(KERN_ERR "Failed to create /proc entry\n");
        return -ENOMEM;
    }

    printk(KERN_INFO "Update File Kernel Module loaded.\n");
    return 0;
}


/**
 * update_file_exit
 * module exit function
 * 
 * @param void
 * @return void
*/

static void __exit update_file_exit(void) {
    if (proc_entry) {
        proc_remove(proc_entry);
    }

    printk(KERN_INFO "Update File Kernel Module unloaded.\n");
}


// module initialization and exit macros
module_init(update_file_init);
module_exit(update_file_exit);