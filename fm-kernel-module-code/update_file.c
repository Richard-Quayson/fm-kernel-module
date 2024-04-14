#include <linux/init.h>
#include <linux/module.h>
#include <linux/fs.h>
#include <linux/proc_fs.h>
#include <linux/uaccess.h>
#include <linux/err.h>
#include <linux/slab.h> // kzalloc
#include <linux/dcache.h> // d_lookup
#include <linux/namei.h>
#include <linux/file.h> // filp_open, filp_close, kernel_write
#include <linux/fcntl.h> // O_WRONLY, O_APPEND

// Module metadata
MODULE_AUTHOR("Richard Quayson & Thomas Quarshie");
MODULE_DESCRIPTION("Update File Kernel Module");
MODULE_LICENSE("GPL");

// Proc file entry
static struct proc_dir_entry *proc_entry;

// Function to handle file updates
static ssize_t update_file_write(struct file *file, const char __user *user_buffer, size_t count, loff_t *offset) {
    char *buffer;
    char *file_path = NULL;
    char *file_content = NULL;
    char *newline_pos = NULL;
    char *overwrite_flag_str = NULL;
    int overwrite_flag;

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

    // Locate the pipe character ('|') in the buffer
    newline_pos = strchr(buffer, '|');
    if (!newline_pos) {
        kfree(buffer);
        return -EINVAL; // Invalid input, expected a newline character
    }

    // Split the buffer into file path and content
    *newline_pos = '\0';
    file_path = buffer;
    file_content = newline_pos + 1;

    // Locate another pipe character to split content and overwrite flag
    newline_pos = strchr(file_content, '|');
    if (!newline_pos) {
        kfree(buffer);
        return -EINVAL; // Invalid input, expected a newline character
    }

    *newline_pos = '\0';
    overwrite_flag_str = newline_pos + 1;

    // Convert overwrite_flag_str to int
    if (kstrtoint(overwrite_flag_str, 10, &overwrite_flag) != 0) {
        kfree(buffer);
        return -EINVAL; // Invalid overwrite flag input
    }

    // Open the file with the appropriate flags
    int file_flags = O_WRONLY | ((overwrite_flag) ? O_TRUNC : O_APPEND);
    struct file *filp = filp_open(file_path, file_flags, 0644);
    if (IS_ERR(filp)) {
        printk(KERN_ERR "Failed to open file: %ld\n", PTR_ERR(filp));
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
        printk(KERN_INFO "File updated successfully: %s\n", file_path);
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
    .proc_write = update_file_write,
};

// Module initialization function
static int __init update_file_init(void) {
    proc_entry = proc_create("update_file", 0222, NULL, &pops);
    if (!proc_entry) {
        printk(KERN_ERR "Failed to create /proc entry\n");
        return -ENOMEM;
    }

    printk(KERN_INFO "Update File Kernel Module loaded.\n");
    return 0;
}

// Module cleanup function
static void __exit update_file_exit(void) {
    if (proc_entry) {
        proc_remove(proc_entry);
    }

    printk(KERN_INFO "Update File Kernel Module unloaded.\n");
}

module_init(update_file_init);
module_exit(update_file_exit);

