import web
import json

urls = (
    '/upload/', 'Upload',
    '/check/', 'Check',
    '/results/', 'Result'
)

hits = {}
columns = []
choices = []
options = None
limit = 5

class Upload:
    def POST(self):

        global hits, columns, choices, options, limit
        data_str = web.input().data
        # print data_str
        data = json.loads(data_str)
        options = data["options"]
        questions = data["questions"]


        hits.clear()
        for question in questions:
            hits[question["id"]] = 1
            if "columns" in question:
                columns = question["columns"]
            if "options" in question:
                choices = question["options"]


        results = {
            "code": 0,
            "status": 1,
        }
        return json.dumps(results)


class Check:
    def GET(self):
        results = {
            "code": 0,
            "status": True,
        }
        return json.dumps(results)

class Result:
    def GET(self):
        res = {}
        global options, limit
        for hit, label in hits.iteritems():
            ans = {}
            ans["id"] = hit
            if options["task_type"] == "fill":
                qans = {}
                for col in columns:
                    qans[col] = "col"
                ans["answer"] = qans
            elif options["task_type"] == "collect":
                pairs = []
                for i in xrange(0, limit):
                    entity = {}
                    for attr in columns:
                        entity[attr] = "col"
                    pairs.append(entity)
                ans["answer"] = pairs
            elif options["task_type"] == "single_label" or options["task_type"] == "multi_label":
                qans = choices
                ans["answer"] = ["snow", "sunny"]
            else:
                ans["answer"] = 1
            res[hit] = ans

        results = {
            "code": 0,
            "data": res,
        }

        print json.dumps(results)
        return json.dumps(results)

if __name__ == "__main__":
    app = web.application(urls, globals())
    app.run()