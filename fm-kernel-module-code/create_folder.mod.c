#include <linux/module.h>
#define INCLUDE_VERMAGIC
#include <linux/build-salt.h>
#include <linux/elfnote-lto.h>
#include <linux/export-internal.h>
#include <linux/vermagic.h>
#include <linux/compiler.h>

#ifdef CONFIG_UNWINDER_ORC
#include <asm/orc_header.h>
ORC_HEADER;
#endif

BUILD_SALT;
BUILD_LTO_INFO;

MODULE_INFO(vermagic, VERMAGIC_STRING);
MODULE_INFO(name, KBUILD_MODNAME);

__visible struct module __this_module
__section(".gnu.linkonce.this_module") = {
	.name = KBUILD_MODNAME,
	.init = init_module,
#ifdef CONFIG_MODULE_UNLOAD
	.exit = cleanup_module,
#endif
	.arch = MODULE_ARCH_INIT,
};

#ifdef CONFIG_RETPOLINE
MODULE_INFO(retpoline, "Y");
#endif



static const struct modversion_info ____versions[]
__used __section("__versions") = {
	{ 0x5b8239ca, "__x86_return_thunk" },
	{ 0xce168946, "proc_remove" },
	{ 0xeb233a45, "__kmalloc" },
	{ 0x88db9f48, "__check_object_size" },
	{ 0x13c49cc2, "_copy_from_user" },
	{ 0xa916b694, "strnlen" },
	{ 0x3c3ff9fd, "sprintf" },
	{ 0x9f984513, "strrchr" },
	{ 0xc6f29188, "kern_path" },
	{ 0x754d539c, "strlen" },
	{ 0xd2f7c8a0, "d_lookup" },
	{ 0x9ea7d77, "vfs_mkdir" },
	{ 0xd6196dad, "path_put" },
	{ 0x37a0cba, "kfree" },
	{ 0x885e32f2, "d_alloc_name" },
	{ 0xfd54ca7d, "d_instantiate" },
	{ 0xf0fdf6cb, "__stack_chk_fail" },
	{ 0xcbd4898c, "fortify_panic" },
	{ 0xbdfb6dbb, "__fentry__" },
	{ 0x80efbd79, "proc_create" },
	{ 0x122c3a7e, "_printk" },
	{ 0x2fa5cadd, "module_layout" },
};

MODULE_INFO(depends, "");


MODULE_INFO(srcversion, "45D4A0084D0A6F7B8AD88AA");
