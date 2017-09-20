from __future__ import print_function
from glob import glob
import os


my_folder = 'C:/Users/Darryn/Desktop/weightDetection/positive'
f = open('C:/Users/Darryn/Desktop/weightDetection/weight.info', 'w')

files_list = glob(os.path.join(my_folder, '*.jpg'))
for a_file in sorted(files_list):
  split = a_file.split('\\')
  f.write("positive/" + split[1] + " 1 0 0 200 200" +"\n")
