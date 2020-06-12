import yaml
import sys

def meta_constructor(loader,node):
    value = loader.construct_mapping(node)
    return value

yaml.add_constructor(u'tag:yaml.org,2002:Orchestrator.Messages.OrchestratorResource',meta_constructor)

def addNetworkResource(nr):
    f = open('resource.yaml','r')
    yamlObj = yaml.load(f)
    f.close()
    resYaml = yamlObj
    nwResources= yamlObj['NetworkResources']
    nwResources.append(nr)
    resYaml['NetworkResources'] = nwResources
    g = open('resource.yaml','w')
    yaml.dump(resYaml,g)
    g.close()
    print(resYaml)

def addServiceResource(sr):
    f = open('resource.yaml','r')
    yamlObj = yaml.load(f)
    f.close()
    resYaml = yamlObj
    svResources= yamlObj['ServiceResources']
    svResources.append(sr)
    resYaml['ServiceResources'] = svResources
    g = open('resource.yaml','w')
    yaml.dump(resYaml,g)
    g.close()
    print(resYaml)

def delNetworkResource(nr):
    f = open('resource.yaml','r')
    yamlObj = yaml.load(f)
    f.close()
    resYaml = yamlObj
    nwResources= yamlObj['NetworkResources']
    i = nwResources.index(nr)
    print("Element found at index",str(i))
    nwResources.pop(i)
    resYaml['NetworkResources'] = nwResources
    g = open('resource.yaml','w')
    yaml.dump(resYaml,g)
    g.close()
    print(resYaml)

def delServiceResource(sr):
    f = open('resource.yaml','r')
    yamlObj = yaml.load(f)
    f.close()
    resYaml = yamlObj
    svResources= yamlObj['ServiceResources']
    i = svResources.index(sr)
    print("Element found at index",str(i))
    svResources.pop(i)
    resYaml['ServiceResources'] = svResources
    g = open('resource.yaml','w')
    yaml.dump(resYaml,g)
    g.close()
    print(resYaml)


if __name__=='__main__':
   ## print(sys.argv[0])  Just the name of the python file
   ## print(len(sys.argv)) Including name of file
   #delNetworkResource("Nl2")
   resrc = ""
   for i in range(len(sys.argv)-3):
       resrc += sys.argv[i+3]
       resrc += ""
   if(sys.argv[1] == '--add') :
       if(sys.argv[2] == '--network') :
        print('Adding Network Resource :',resrc)
        addNetworkResource(resrc)
       elif(sys.argv[2] == '--service') :
        print('Adding Service Resource :',resrc)
        addServiceResource(resrc)
   elif(sys.argv[1] == '--del') :
       if(sys.argv[2] == '--network') :
        print('Deleting Network Resource :',resrc)
        delNetworkResource(resrc)
       elif(sys.argv[2] == '--service') :
        print('Deleting Service Resource :',resrc)
        delServiceResource(resrc)



