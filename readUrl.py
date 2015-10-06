import sys
import re
import urllib.request
import json
def readUrl():
	input_var = input("Enter something: ")
	words = re.split('\s+',input_var.strip())
	json1="&wt=json&indent=true"
	url="http://localhost:8983/solr/collection1/select?q="
	for val in words:	
		url=url+val+"+"
	url = url[:-1]
	url=url+json1
	response = urllib.request.urlopen(url)
	print("\n")
	encoding=response.info().get_param('charset', 'utf8')
	j=json.loads(response.read().decode(encoding))
	for each in j["response"]["docs"]:
		for key,value in each.items():
			print(key, " :  ", value)
		print("\n")
readUrl()
