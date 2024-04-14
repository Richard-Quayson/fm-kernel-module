obj-m := create_folder.o rename_folder.o create_file.o delete_file.o update_file.o rename_file.o
all:
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) modules

clean:
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) clean
