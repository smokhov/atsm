#!/bin/sh

mkdir -p test/softcore
#mkdir -p test/hardcore

# Softcore

cd test/softcore

cal > cal.txt
pwd > pwd.txt

mkdir subdir1 subdir2 subdir3
ln -s subdir2 subdir2.symlink

cd subdir1

mkdir subsubdir1 subsubdir2 subsubdir3
ln -s ../cal.txt cal.symlink
ps > ps.txt

cd ..

ln -s subdir1/subsubdir2 subsubdir2.symlink

cd subdir1/subsubdir3

ls > ls.txt
du > ../../subdir2/du.txt
ln -s ../../subdir2/du.txt du.symlink

cd ../../subdir2

ln -s .. softcore.symlink

# Hardcore

cd ../..

cp -r softcore hardcore 

cd hardcore

ln -s / root.symlink

cd subdir1/subsubdir2

ln -s ../../subsubdir2.symlink recursive.symlink

cd ../../subdir2

cal > ../subdir1/junk
ln -s ../subdir1/junk dead.symlink
rm ../subdir1/junk

# EOF
