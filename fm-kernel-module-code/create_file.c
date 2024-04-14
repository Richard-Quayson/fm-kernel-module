#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>
#include <linux/proc_fs.h>
#include <linux/uaccess.h>
#include <linux/namei.h>
#include <linux/err.h>
#include <linux/slab.h> // kzalloc
#include <linux/dcache.h> // d_lookup, d_alloc_name, d_instantiate

// Module metadata
MODULE_AUTHOR("Richard Quayson & Thomas Quarshie");
MODULE_DESCRIPTION("Create File Kernel Module");
MODULE_LICENSE("GPL");

// Proc file entry
static struct proc_dir_entry *proc_entry;

static ssize_t create_file_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *offset) {
    char *buffer;
    char *file_path = NULL;
    char *file_content = NULL;
    char *newline_pos = NULL;

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

    buffer[count] = '\0'; // Null-terminate the buffer

    // Locate the newline character ('\n') in the buffer
    newline_pos = strchr(buffer, '\n');
    if (!newline_pos) {
        kfree(buffer);
        return -EINVAL; // Invalid input, expected a newline character
    }

    // Split the buffer into file path and content
    *newline_pos = '\0';
    file_path = buffer;
    file_content = newline_pos + 1;

    // Create the file and open it
    struct file *filp = filp_open(file_path, O_CREAT | O_WRONLY | O_TRUNC, 0644);
    if (IS_ERR(filp)) {
        printk(KERN_ERR "Failed to create or open file: %ld\n", PTR_ERR(filp));
        kfree(buffer);
        return PTR_ERR(filp);
    }

    // Write the content to the file
    ssize_t written = kernel_write(filp, file_content, strlen(file_content), &filp->f_pos);
    if (written < 0) {
        printk(KERN_ERR "Failed to write to file: %ld\n", written);
        filp_close(filp, NULL);
        kfree(buffer);
        return written;
    } else {
        printk(KERN_INFO "File created and content written successfully: %s\n", file_path);
    }

    // Close the file
    filp_close(filp, NULL);

    // Free allocated memory
    kfree(buffer);

    // Return the number of bytes written
    return written;
}

// Proc file operations structure
static const struct proc_ops pops = {
    .proc_write = create_file_write,
};

// Module initialization function
static int __init create_file_init(void) {
    proc_entry = proc_create("create_file", 0222, NULL, &pops);
    if (!proc_entry) {
        printk(KERN_ERR "Failed to create /proc entry\n");
        return -ENOMEM;
    }

    printk(KERN_INFO "Create File Kernel Module loaded.\n");
    return 0;
}

// Module cleanup function
static void __exit create_file_exit(void) {
    if (proc_entry) {
        proc_remove(proc_entry);
    }

    printk(KERN_INFO "Create File Kernel Module unloaded.\n");
}

module_init(create_file_init);
module_exit(create_file_exit);
