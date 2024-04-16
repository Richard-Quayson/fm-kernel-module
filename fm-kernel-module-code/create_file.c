#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>
#include <linux/proc_fs.h>
#include <linux/uaccess.h>
#include <linux/namei.h>
#include <linux/err.h>
#include <linux/slab.h>
#include <linux/dcache.h>


// Module metadata
MODULE_AUTHOR("Richard Quayson & Thomas Quarshie");
MODULE_DESCRIPTION("Create File Kernel Module");
MODULE_LICENSE("GPL");


// proc file entry
static struct proc_dir_entry *proc_entry;


/**
 * create_file_write
 * this function is called when the /proc/create_file file is written to
 * it creates a file with the specified path and writes the content to it
 * 
 * @param file: file pointer
 * @param user_buffer: user buffer
 * @param count: number of bytes to write
 * @param offset: file offset
 * @return ssize_t: number of bytes written
*/

static ssize_t create_file_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *offset) {
    char *buffer;
    char *file_path = NULL;
    char *file_content = NULL;
    char *newline_pos = NULL;

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

    buffer[count] = '\0'; // null-terminate the buffer

    // locate the newline character ('\n') in the buffer
    newline_pos = strchr(buffer, '\n');
    if (!newline_pos) {
        kfree(buffer);
        return -EINVAL; // invalid input, expected a newline character
    }

    // split the buffer into file path and content
    *newline_pos = '\0';
    file_path = buffer;
    file_content = newline_pos + 1;

    // create the file and open it
    struct file *filp = filp_open(file_path, O_CREAT | O_WRONLY | O_TRUNC, 0644);
    if (IS_ERR(filp)) {
        printk(KERN_ERR "Failed to create or open file: %ld\n", PTR_ERR(filp));
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
        printk(KERN_INFO "File created and content written successfully: %s\n", file_path);
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
 * proc_ops structure for the /proc/create_file file
*/

static const struct proc_ops pops = {
    .proc_write = create_file_write,
};


/**
 * create_file_init
 * this function is called when the module is loaded
 * it creates the /proc/create_file file
 * 
 * @param void
 * @return int: 0 if successful, otherwise the error code
*/

static int __init create_file_init(void) {
    proc_entry = proc_create("create_file", 0222, NULL, &pops);
    if (!proc_entry) {
        printk(KERN_ERR "Failed to create /proc entry\n");
        return -ENOMEM;
    }

    printk(KERN_INFO "Create File Kernel Module loaded.\n");
    return 0;
}


/**
 * create_file_exit
 * this function is called when the module is unloaded
 * it removes the /proc/create_file file
 * 
 * @param void
 * @return void
*/

static void __exit create_file_exit(void) {
    if (proc_entry) {
        proc_remove(proc_entry);
    }

    printk(KERN_INFO "Create File Kernel Module unloaded.\n");
}

// register the module initialization and cleanup functions
module_init(create_file_init);
module_exit(create_file_exit);