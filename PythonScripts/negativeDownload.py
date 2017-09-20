import urllib.request
import urllib.error

url = 'http://tutorial-haartraining.googlecode.com/svn/trunk/data/negatives/neg-'

def downloadNegative(name):
	fullName = "0" + str(name) + ".jpg"
	try:
		urllib.request.urlretrieve(url+fullName,"C:/Users/Darryn/Desktop/weightDetection/negative/" + fullName)
		print(name)
	except urllib.error.URLError:
		print(str(name) + " doesn't work")
	except urllib.error.HTTPError:
		print(str(name) + " doesn't work")


for x in range(211,999):
	downloadNegative(x)