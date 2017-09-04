#encoding:utf-8
import urllib2
import urllib
import time
#import MultipartPostHandler
import json
# import MySQLdb
from main.models import TaskProject , QueTask
from main.property import ChinaCrowd_Property
from poster.encode import multipart_encode
from poster.streaminghttp import register_openers
from poster.streaminghttp import StreamingHTTPHandler, StreamingHTTPRedirectHandler, StreamingHTTPSHandler

# MySQLdb.connect()
class TaskCrowdSourcing:
    name="";
    password="";
    requester = "requester";
    developer = "False";
    developer_site = "";
    datas='';
    Login = "/account/login";
    # host and port of ChinaCrowd
    Api_Crowd = ChinaCrowd_Property.Api_Crowd#"http://166.111.71.172:6789";
    # suburl of creating task
    Workflow_Create = "/workflow/create?token=";
    #suburl of uploading question and options
    Workflow_UploadOption = "/workflow/upload-option?token=";
    #suburl of getting options
    Workflow_GetOption = "/workflow/get-option?token=";
    #suburl of getting units which have been rendered
    Workflow_Unit = "/workflow/unit?token=";
    # suburl of uploading data for rendering unit template
    Workflow_UploadData = "/workflow/upload-normal-data?token=";
    # suburl of publish tasks
    Workflow_Publish = "/workflow/publish/?token=";
    #suburl of appending data
    Workflow_Append = "/workflow/append-data?token=";
    #subulr of killing tasks
    Workflow_End = "/workflow/end/?token=";
    #suburl of user's token
    token = "U:16986978796648769938";

    pay_immita=False

  #  # Task_Projection=dict() #'''CREATE TABLE `crowddb`.`main_quetask` (
  # `id` INT NOT NULL AUTO_INCREMENT,
  # `task_id` INT UNSIGNED NULL,
  # `que_id` VARCHAR(200) NULL,
  # PRIMARY KEY (`id`));'''
    question_index=dict()

    # save #unit of different tasks
    task_unitnumber=dict()
    input_number=0
    Inputs=False
    isMTO=False
    need_picture=False


    # suburl of downloading results
    Workflow_DownloadAnswer = "/workflow/download_answer?token=";


    def __init__(self,name,password,requester):
        self.name=name
        self.password=password
        self.requester=requester
    def __init__(self,name,password,requester,developer,developer_site):
        self.__init__(name,password,requester)
        self.developer=developer
        self.developer_site=developer_site

    def __init__(self):
        #self.__db = MySQLdb.connect("localhost", "amt", "amtlalala","crowd_assign");
        self.name=ChinaCrowd_Property.name#"hatims"
        self.password=ChinaCrowd_Property.password #"769513564"

    # set user's token
    def setToken(self):
        url=self.Api_Crowd+self.Login
        params = urllib.urlencode({'username': self.name, 'password': self.password, 'type': self.requester})
        f = urllib.urlopen(url, params)
        s=json.loads(f.read())
        self.token=s['result']['token']

    def CreateTasks(self,input_number=0,Inputs=False):
        self.input_number=input_number
        self.Inputs=Inputs
        return self.CreateTaskss()


    def CreateTaskss(self):
        params=dict()
        params['requester_id']=1
        params["description"]="this is a new question";
        params["title"]="questions";

        url=self.Api_Crowd+self.Workflow_Create+self.token
        params=urllib.urlencode(params)
        f=urllib.urlopen(url,params)
        s=json.loads(f.read())
        return s['result']['id']  #returen task's id



    def UploadTemplateType(self,task_id):
        params = dict()
        params["template_type"] = (3 if self.need_picture else 1)
        request = urllib2.Request(self.Api_Crowd+"/task/"+str(task_id)+"/?token="+self.token);
        # print self.Api_Crowd+"/task/"+str(task_id)+"/?token="+self.token
        request.get_method = lambda: 'PATCH'
        params = urllib.urlencode(params)
        request.add_data(params)
        # print request.get_method()
        try:
            urllib2.urlopen(request)

            return True
        except  Exception:
            return  False

    def UploadOtherTemplate(self,task_id,params):
        # params = dict()
        # params["template_type"] = (3 if self.need_picture else 1)
        request = urllib2.Request(self.Api_Crowd+"/task/"+str(task_id)+"/?token="+self.token);
        # print self.Api_Crowd+"/task/"+str(task_id)+"/?token="+self.token
        request.get_method = lambda: 'PATCH'
        params = urllib.urlencode(params)
        request.add_data(params)
        # print request.get_method()
        try:
            urllib2.urlopen(request)

            return True
        except  Exception:
            return  False



    def UploadOptions(self,task_id,questions,inputs,type):

        need_url="no"
        unit_num=0;
        if questions[0].get("url")  and (type=='M_TO_O' or type=='M_TO_M'):
            self.need_picture= True;
            need_url="yes"
        #initial question_index
        question_task=[]

        url=self.Api_Crowd+self.Workflow_UploadOption+self.token
        params=dict()
        params["task_id"]=task_id
        attr_id = ''
        params["is_geo"] = 0
        options=dict()
        if inputs:
            if self.Inputs:
                options["template"] ='please fill in the flank, there are '+str(self.input_number)+' blanks about {thing}: ';
            else:
                options["template"] = 'please collect these informations, there are ' + str(self.input_number) + ' parts about'+questions[0]["content"];
        elif self.need_picture:
            options["template"]="@{url_of_pic}@"


        else:
            options["template"]='please answer below questions.'
        # params['option']['isgeo']= '0'
        options["questions"]=[]
        i=0
        if inputs:
            if self.Inputs:
                for i in range(0,self.input_number):
                    option = dict()
                    option["type-input"] = 1
                    option["name"]=i+1;
                    option["description"]=questions[0]["columns"][i];
                    attr_id+=questions[0]["columns"][i]+"&&&&&"
                    options["questions"].append(option)
                for question in questions:
                    question_task.append(question["id"])
                    self.datas=self.datas+question["content"]
                    # for i in range(0,self.input_number):
                    #     self.datas = self.datas +"***"+ question["columns"][i];
                    self.datas=self.datas+'GEOGEO 120.0 -40.0 5.0'+'\n';
                unit_num=len(questions)
            else:
                for i in range(0,self.input_number):
                    option = dict()
                    option["type-input"] = 1
                    option["name"]=i+1;
                    option["description"]=questions[0]["columns"][i];
                    attr_id+=questions[0]["columns"][i]+"&&&&&"
                    options["questions"].append(option)
                question_task.append(questions[0]["id"])
                for i in range(0,int(questions[0]["limit"])*int(questions[0]["repeats"])):
                    self.datas = self.datas + 'GEOGEO 120.0 -40.0 5.0' + '\n';
                unit_num=int(questions[0]["limit"])*int(questions[0]["repeats"]);
        else:
            for question in questions:
                option=dict()
                if question.get("options"):

                    if question.get("type") and question.get("type")=="type-radio":
                        option["type-radio"] = 1
                    elif question.get("type") and question.get("type")=="type-checkbox":
                        option["type-checkbox"]=1
                    # if question.get
                    if type=='Y_N':
                        option["options"] = ["yes", "no"]
                    else:
                        option["options"] = question["options"]

                else:
                    option["type-radio"]= 1
                    option["options"] = ["yes", "no"]
                #option['is_geo'] = 0
                #
                option["description"] = question["content"]
                if self.need_picture:
                    options["questions"].append(option)
                    unit_num=len(questions)
                    break;
                else:
                    unit_num=1
                option["name"] = question["id"]
                question_task.append(question["id"])
                i=i+1




                # question_gene.append(json.dumps(option))
                options["questions"].append(option)
            if self.need_picture:
                for que in questions:
                    question_task.append(que["id"])
                    self.datas = self.datas +que["url"]
                    self.datas = self.datas + 'GEOGEO 120.0 -40.0 5.0' + '\n';


        self.question_index[task_id]=question_task
        # cursor=self.__db.cursor()
        que_id=''
        for que in question_task:
            que_id+=str(que)+'|'


        # sql="insert into main_quetask(task_id,que_id,attr_id,unit_num,need_url) VALUES ('%d','%s','%s','%d','%s')" %(task_id,que_id,attr_id,unit_num,need_url)
        # cursor.execute(sql)
        # self.__db.commit()
        sql_quetask = QueTask(task_id=task_id,
                              que_id=que_id,
                              attr_id=attr_id,
                              unit_num=unit_num,
                              need_url=need_url)
        sql_quetask.save()
        params["option"]=json.dumps(options)

        # print params
        params = urllib.urlencode(params)
        # print params
        # f = urllib.urlopen(url, params)
        res = urllib2.Request(url, params)
        f = urllib2.urlopen(res)
        # f=urllib.urlopen(url,params)
        # print f.read()
        s=json.loads(f.read())
        # print  s
        if s['code']==0:
            return True
        else:
            return False

    #upload data: 1.genetate template file; 2.upload file; 3.return result
    def UploadData(self,filename, task_id, numer_line,inputs):
        url=self.Api_Crowd+self.Workflow_UploadData+self.token
        self.task_unitnumber[task_id] = numer_line#len(questions)

        dataGene=open(filename,'w')
        if inputs:
            # print self.datas
            dataGene.write(self.datas)
        elif self.need_picture:
            dataGene.write(self.datas)
        else:
            # for i in range(0,numer_line,1):
            dataGene.write('GEOGEO 120.0 -40.0 5.0'+'\n')
        dataGene.close()
        handlers = [StreamingHTTPHandler, StreamingHTTPRedirectHandler, StreamingHTTPSHandler]
        opener = urllib2.build_opener(*handlers)
        urllib2.install_opener(opener)
        datagen, headers = multipart_encode({"data": open(filename, "rb"),"task_id":task_id})
        request = urllib2.Request(url, datagen, headers)
        result=urllib2.urlopen(request).read()
        s = json.loads(result)
        # print s
        if s['code'] == 0:
            return True
        else:
            return False

    def Publish(self,task_id):
        url = self.Api_Crowd + self.Workflow_Publish + self.token;
        params = dict()
        params['task_id'] = task_id
        params = urllib.urlencode(params)
        f = urllib.urlopen(url, params)
        s = json.loads(f.read())
        # print s
        if s['code']==0:
            return True
        else:
            return False

    def getResults(self,task_id):
        url = self.Api_Crowd + self.Workflow_DownloadAnswer + self.token;
        params = dict()
        params['task_id'] = task_id
        params = urllib.urlencode(params)
        s = urllib.urlopen(url, params).read()
        # print s.strip()[0:10]
        if s[0:10]=="\"inner_id:":
            return s
        try:
            sj = json.loads(s)
            # print  sj

            return "";
        except Exception:
            return s

        # if sj['code'] == 3:
        #     self.setToken();
        return s

    def GetResults(self,task_id,inputs):
        s=self.getResults(task_id)
        units = s.strip().split('\n')
        if units==None:
            return []
        results = []
        for unit in units:
            unit_id = self.GetUnitId(unit)
            result = self.getUnitResult(unit, unit_id,task_id,inputs)
            if self.Inputs or inputs or self.need_picture:
                results.append(result)
            else:
                results.extend(result)
        return results

    #detect whether task was finished
    def detectResult(self,task_id):
        s=self.getResults(task_id)
        res_number=len(s.split('\n'))
        # print s
        if cmp('',s)==0 or s==None:
            return False

        # sql = "select unit_num from main_quetask where task_id=%d" % task_id
        # cursor = self.__db.cursor()
        # cursor.execute(sql)
        # unit_num = cursor.fetchone()[0]
        # print "**********result_num:"+str(res_number)+"*******unit_num:"+str(unit_num);

        sql_quetask = QueTask.objects.filter(task_id=task_id)
        if sql_quetask!=None and len(sql_quetask)>0:
            unit_num = sql_quetask[0].unit_num
        else:
            unit_num = 0
        if(res_number<unit_num):
            return False
        return True


    def GetUnitId(self, str):
        start=str.find('inner_id:')+9
        end=str.find(",unit_data:")
        # print str[start:end]
        # print str
        return int(str[start:end])

    def GetWorkerId(self,str):
        start = str.find('worker_id:') + 10
        end = str.find(",unit_id:")
        # print str[start:end]
        return str[start:end].strip()

    def GetAnswer(self,str):
        start = str.find('result:') + 7
        end = str.find(",correct:")
        answers=str[start:end].strip().split("|||");
        return answers;


#question_id,worker_id,answer
    def getUnitResult(self,str,unit_id,task_id,inputs):

        if self.need_picture:
            questions = str.split('|||question_description:')
            results = []
            # sql = "select que_id from main_quetask where task_id=%d" % task_id
            # cursor = self.__db.cursor()
            # cursor.execute(sql)
            # res_ids = cursor.fetchone()[0].strip().split('|')
            sql_quetask = QueTask.objects.filter(task_id=task_id)
            res_ids = sql_quetask[0].que_id.strip().split('|')
            result = []
            result.append(res_ids[unit_id])
            result.append(self.GetWorkerId(questions[1]).strip())
            for i in range(1, len(questions)):
                answers = self.GetAnswer(questions[i]);
                if self.isMTO:
                    result.append(answers[0])
                else:
                    result.append(answers)
                results.append(result)
                return result


        if self.Inputs or inputs:
            questions = str.split('|||question_description:')
            results = []
            # sql = "select que_id from main_quetask where task_id=%d" % task_id
            # cursor = self.__db.cursor()
            # cursor.execute(sql)
            # res_ids = cursor.fetchone()[0].strip().split('|')
            sql_quetask = QueTask.objects.filter(task_id=task_id)
            res_ids = sql_quetask[0].que_id.strip().split('|')

            result = []
            if self.Inputs:
                result.append(res_ids[unit_id]) # id
            else:
                result.append(res_ids[0])
            result.append(self.GetWorkerId(questions[1]).strip())
            answers=dict()

            # sql = "select attr_id from main_quetask where task_id=%d" % task_id
            # cursor = self.__db.cursor()
            # cursor.execute(sql)
            # attr_res = cursor.fetchone()[0].strip().split('&&&&&')
            sql_quetask = QueTask.objects.filter(task_id=task_id)
            attr_res = sql_quetask[0].attr_id.strip().split('&&&&&')


            for i in range(1, len(questions)):
                answers[attr_res[i-1]]=self.GetAnswer(questions[i])[0]
            result.append(answers)
            return result

        questions=str.split('|||question_description:')
        results=[]
        # sql="select que_id from main_quetask where task_id=%d" %task_id
        # cursor=self.__db.cursor()
        # cursor.execute(sql)
        # res_ids=cursor.fetchone()[0].strip().split('|')
        sql_quetask = QueTask.objects.filter(task_id=task_id)
        res_ids = sql_quetask[0].que_id.strip().split('|')

        for i in range(1,len(questions)):
            result=[]
            result.append(res_ids[i-1]) #id
            result.append(self.GetWorkerId(questions[i]))
            answers=self.GetAnswer(questions[i]);
            if len(answers)==1 and (cmp(answers[0],'yes')==0 or cmp(answers[0],'no')==0):
                result.append((1 if cmp(answers[0],'yes')==0 else 0))
            elif self.isMTO:
                result.append(answers[0])
            else:
                result.append(answers)
            results.append(result)
        return results





#packing
class ChinaBaoApi:
    Qnum_Unit = 5
    Input_Number=3;
    def __init__(self):
        self.__task_projection= dict() #dictionary about the task id projection between ChinaCrowd and CrowdDB
        #database patameters, replace with your own database
        # self.__db=MySQLdb.connect("localhost", "amt", "amtlalala","crowd_assign");

        self.__crowdSorucing = TaskCrowdSourcing()


    # return: True or False
    #questions: [{'id':~,'content':~},...]
    ##version-2:[{'id',:~,'content':~,options:[]},..]
    def TaskCreated(self,questions,task_id,type,title="please answer some questions."):
        self.__crowdSorucing = TaskCrowdSourcing()
        inputs=False
        Inputts=0;
        created_inputs=False
        for que in questions:
            if type=='M_TO_O' or type=='Y_N':
                que["type"]="type-radio" ;
            elif type=='M_TO_M':
                que["type"] ='type-checkbox';
            elif type=='FREE':
                self.Input_Number=len(que["columns"])
                Inputts=len(que["columns"])
                inputs=True
                created_inputs=True
                break;
            elif type=='COLLECT':
                self.Input_Number=len(que["columns"])
                Inputts=len(que["columns"])
                inputs=True
                break;
        self.__crowdSorucing.setToken();
        baotask_id=self.__crowdSorucing.CreateTasks(Inputts,created_inputs) #task_id of ChinaCrowd
        # sql="insert into main_taskproject(task_db,task_bao) VALUES ('%s','%s')" %(task_id,str(baotask_id))
        # self.InsertDb(sql)
        sql_taskprojection = TaskProject(task_db=task_id,task_bao=str(baotask_id))
        sql_taskprojection.save()

        if  self.__crowdSorucing.UploadOptions(baotask_id,questions,inputs,type)==False:
            return False

        # print "change type"
        if self.__crowdSorucing.UploadTemplateType(baotask_id)==False:
            return False
        params=dict()
        params['title']=title
        if self.__crowdSorucing.UploadOtherTemplate(baotask_id,params)==False:
            return False


        # print "upload"
        if self.__crowdSorucing.UploadData("data.txt",baotask_id,len(questions)/self.Qnum_Unit,inputs)==False:
            return False
        # print "upidata"
        if self.__crowdSorucing.Publish(baotask_id)==False:
            return False
        # print "publish"
        return True

    #return: True or False
    def TaskDetected(self,task_id):
        self.__crowdSorucing.setToken();
        # baotask_id=self.__task_projection[task_id]
        #获取结果
        # sql="select task_bao from main_taskproject where task_db='%s'" %(task_id)
        bao_ids=self.Get_Baoid(task_id)
        if bao_ids:
            bao_ids=bao_ids.strip().split('|')
        else:
            return False
            # .strip().split('|')
        baotask_id=int(bao_ids[0])
        # print baotask_id
        return self.__crowdSorucing.detectResult(baotask_id)

   #return: [(question_id,worker_id,anser),(...)]
    def TaskResult(self,task_id,type):
        self.__crowdSorucing.setToken();
        inputs=False
        if type=="FREE":
            self.__crowdSorucing.Inputs=True
        if type=="FREE" or type=="COLLECT":
            inputs=True
        if type=="M_TO_O":
            self.__crowdSorucing.isMTO=True
        # sql = "select task_bao from main_taskproject where task_db='%s'" % (task_id)
        bao_ids = self.Get_Baoid(task_id)
        if bao_ids:
            bao_ids=bao_ids.strip().split('|')
        else:
            return None
        baotask_id = int(bao_ids[0])

        # sql = "select need_url from main_quetask where task_id=%d" % baotask_id
        # cursor = self.__db.cursor()

        # cursor.execute(sql)
        # need_url = cursor.fetchone()[0].strip()
        sql = QueTask.objects.filter(task_id=baotask_id)
        need_url = sql[0].need_url.strip()
        if need_url=="yes":
            self.__crowdSorucing.need_picture=True

        return self.__crowdSorucing.GetResults(baotask_id,inputs)



    # def InsertDb(self,sql):
    #     cursor = self.__db.cursor()
    #     cursor.execute(sql)
    #     self.__db.commit()

    def Get_Baoid(self,task_id):
        # cursor = self.__db.cursor()
        # result=cursor.execute(sql)
        # ids = cursor.fetchone()
        # # print sql
        # if ids:
        #     return ids[0]
        # else:
        #     return None
        sql = TaskProject.objects.filter(task_db=task_id)
        if sql:
            return sql[0].task_bao
        else:
            return None

if __name__=="__main__":


    api=ChinaBaoApi()
    #some questions for test
    #questions=[{"id":2,"content":"我是中国人"},{"id":3,"content":"yes ok?"},{"id":4,"content":"yes ok?","options":["I'm fine", "I'm hungry","I'm tired","I'm excited"]}]
    # questions=[{"id":"erer","content":"袁海涛","columns":[
    #                 "name",
    #                 "school",
    #                 "birthday"
    #             ]},{"id":"sdsd","content":"李元霸","columns":[
    #                 "name",
    #                 "school",
    #                 "birthday"
    #             ]}]
    # questions1=[{"id":"ererw3434","content":"一些人物","columns":[
    #                 "name",
    #                 "school",
    #                 "birthday"
    #             ],"limit":1,"repeats":2}]
    questions=[
        {
            "id":"fds","content":"这张图表达什么情感","options":["I'm fine", "I'm hungry","I'm tired","I'm excited"]#,"url":"http://imgbbs.heiguang.net/forum/201504/13/152729itt7suztnm933xdm.jpg"
        },
        {
            "id": "fds45", "content": "这张图表达什么情感", "options": ["I'm fine", "I'm hungry", "I'm tired", "I'm excited"]#,"url":"http://imgbbs.heiguang.net/forum/201504/13/152729itt7suztnm933xdm.jpg"
        }
    ]
    print api.TaskCreated(questions,"wrM2M","M_TO_M","please answer the questions")
    #detect results
    while True:
        time.sleep(5)
        if api.TaskDetected("wrM2M"):
            print api.TaskResult("wrM2M","M_TO_M")