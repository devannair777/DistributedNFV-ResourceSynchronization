import yaml

def meta_constructor(loader,node):
    value = loader.construct_mapping(node)
    return value

yaml.add_constructor(u'tag:yaml.org,2002:Orchestrator.Messages.SynchronizedOrchestratorResource',meta_constructor)

class orchObject:
    def __init__(self,resource,hostId):
        self.resource = resource
        self.hostId = hostId

    def getHostId(self):
        return self.hostId

    def getResources(self):
        return self.resource
    
def toTuple(list):
    return tuple(list)

def validator():
    k  = 0
    
    node_index = ['n1','n2','n3','n25','n26','n27','n28','n29']
    node_lists = []
    for i in node_index:
        resource_list = []
        f= open('pwd/' + i +'.conf/Resources.yaml','r')
        yObj = yaml.load(f)
        for j in yObj:
            o1 = orchObject(j['resource'],j['hostId'])
            resource_list.append(o1)
        resource_list.sort(key=orchObject.getHostId)

        node_lists.append(toTuple(resource_list))
    return toTuple(node_lists)

if __name__=='__main__':
  k = validator()
 
  for i in range(7):
      for j in range(8):
          if(k[0][j].getResources() == k[i+1][j].getResources()):
              print ("Resource Entries Matched")
      print("Node",str(1)," Compared with Node",str(i + 2))