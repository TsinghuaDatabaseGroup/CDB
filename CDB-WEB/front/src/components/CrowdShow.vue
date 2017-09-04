<template>
  <div class="container">
  <div class="page-header">
    <h1>Welcome to CrowdDB! <small>query data with crowd</small></h1>
  </div>
    <div class="row">
      <div class="big-page clearfix">
        <div class="col-xs-2">
          <div class="list-group">
            <a class="list-group-item active">
              Tables
            </a>
            <div class="table-list">
              <a v-for="t in tables" class="list-group-item clickable" @click="show_table_data(t)">{{t}}</a>
            </div>
          </div>
        </div>
        <!--
        <div class="col-xs-2">
          <table class="table">
            <thead>
              <tr>
                <th>CrowdSQLs</th>
              </tr>
            </thead>
          </table>
          <div class="scroll-table">
          {{boot_table.header[0]}}
          <table class="table table-hover">
            <tbody>
              <tr v-for="t in crowd_sqls.rows">
                <th class="clickable" @click="show_sql_data(t)">{{t.qsql | len_limit20}}...</th>
              </tr>
            </tbody>
          </table>
          </div>
        </div>
        -->
        <div class="col-xs-10">
          <div class="row">
            <div class="row">
              <textarea v-model="sql" class="sql-input"></textarea>
            </div>
            <div class="row">
              <div class="form-inline">
                <span v-if="is_crowd_sql"><label style="margin: 0px 10px;" class="control-label">Title : </label><input class="form-control" v-model="task_title"/></span>
                <span v-if="is_crowd_sql"><label style="margin: 0px 10px;" class="control-label">Result Table : </label><input class="form-control" v-model="result_table"/></span>
              </div>
              <p class="form-inline">
                <select v-model="sqltype" class="form-control">
                  <option>CrowdSQL</option>
                  <option>NormalSQL</option>
                </select>
                <select v-if="is_crowd_sql" v-model="platform" class="form-control">
                  <option value="CC">ChinaCrowds</option>
                  <option value="CF">CrowdFlower</option>
                  <option value="AMT">Amazon Mechanical Turk</option>
                </select>
                <select v-if="is_crowd_sql" v-model="gmodel" class="form-control">
                  <option :value="false">Normal</option>
                  <option :value="true">GModel</option>
                </select>
                <button @click="runSql" type="button" class="btn btn-success"> Run </button>
              </p>
            </div>
          </div>
          <div class="row form-group" v-if="table_name">
            <label>[ {{table_name}} ]</label>
            <input type="file" style="display: inline;" @change="fileSelected" id="fileToUpload" />
            <button @click="upload" class="btn btn-primary">Upload CSV</button>
            <a class="btn btn-primary" :href="downloadUrl" download>Download CSV</a>
          </div>
          <div class="row">
            <div v-if="show_msg" class="alert alert-info" role="alert">{{show_msg}}</div>
            <div class="scroll-table-header table-responsive sub-category-container">
              <table class="table table-striped">
                <thead>
                  <tr>
                    <th v-for="h in show_data.header" class="text-center">{{h}}</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="r in show_data.rows">
                    <td v-for="h in show_data.header">{{r[h]}}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="scroll-table table-responsive sub-category-container">
            <table class="table table-hover header-fixed">
              <thead style="">
                <tr>
                  <th v-for="h in show_data.header">{{h}}</th>
                </tr>
              </thead>
              <!--
            </table>
            <div class="scroll-table-small">
              <table class="table table-striped">
              -->
                <tbody style="width: 100%">
                <template v-for="r in show_data.rows">
                    <tr>
                      <td v-for="h in show_data.header">{{r[h]}}</td>
                    </tr>
                    <tr class="show-on-hover" v-if="show_crowd_sqls">
                      <td>
                        <button @click="del_crowd_sql(r['id'])" type="button" class="btn btn-success">Del</button>
                      </td>
                      <td :colspan="show_data.header.length-1" style="border-top: none;">
                        <div class="progress">
                          <div class="progress-bar progress-bar-success progress-bar-striped" role="progressbar" aria-valuenow="process_to_percent(r['process'])" aria-valuemin="0" aria-valuemax="100" :style="'width: '+process_to_percent(r['process'])+'%'">
                            {{process_to_percent(r['process'])}}% Complete (success)
                          </div>
                        </div>
                      </td>
                    </tr>
                </template>
                </tbody>
              </table>
            </div>
            <!--</div> -->
          </div>
        </div>
      </div>
    </div>
    <div style="text-align: center; margin-top: 10px" class="row">
      <button @click="logout" class="btn btn-primary">logout</button>
      <button @click="refresh_crowd_sqls()" type="button" class="btn btn-info">Show/Refresh CrowdSQLs</button>
    </div>
  </div>
</template>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .show-on-hover {
    display: none;
  }
  tr + tr.show-on-hover:hover {
    display: table-row;
  }
  tr:hover + tr.show-on-hover {
    display: table-row;
  }
  .scroll-table-header {
    overflow-y: hidden;
    overflow-x: hidden;
    background:rgba(255,255,255,1);
    position:relative;
    padding-right: 19px;
    height: 30px;
    z-index: 1000;
  }

  .sub-category-container{
  }

  .scroll-table {
    z-index: 500;
    overflow-y: scroll;
    overflow-x: scroll;
    max-height: 500px;
    margin-top: -30px;
  }
  .scroll-table-small {
    overflow-y: auto;
    overflow-x: hidden;
    max-height: 340px;
    margin-top: -22px;
  }
  .show-table {
    margin:auto
  }
  .show-table tr {
    padding:5px;
    border: solid 1px;
  }
  .show-table td {
    padding: 4px 5px;
    border: solid 1px;
    min-width: 30px;
  }
  .sql-input {
    margin-top: 40px;
    width: 95%;
    height: 100%;
    font-family: Arial;
    font-size: medium;
  }
  .show {
    margin:auto;
    display: block;
    width: 1000px;
    height: 500px;
  }
  .big-page {
    width: 100%;
    height: 100%;
  }
  .table-list {
    max-height: 600px;
    overflow-y: auto;
    overflow-x: hidden;
  }
  .sql-list {
    margin-left: -1px;
    border: 2px;
    border: solid;
    border-color: brown;
    float: left;
    height: 99%;
    width: 15%;
    overflow: auto;
  }
  .main-block {
    float: left;
    height: 100%;
    width: 65%
  }
  .sql-block {
    margin: 10px;
    height: 20%;
    width: 80%;
  }
  .meta-info {
    height: 25px;
    margin: 5px 5px;
    width: 95%;
  }
  .table-block {
    margin: 10px;
  }
  h1, h2 {
    font-weight: normal;
  }
</style>

<script>
  import * as actions from '../store/actions'
  import store from '../store/index'
  import * as types from '../store/mutation_types'

  export default {
    name: 'crowd-show',
    components: {
    },
    data () {
      return {
        msg: 'Welcome to CrowdDB',
        tables: [],
        table_name: "",
        crowd_sqls: {
          header:[],
          rows: []
        },
        show_data:{
          header:[],
          rows: []
        },
        bootstrap_table_options:{},
        show_msg: "",
        sql: "",
        sqltype: "CrowdSQL",
        result_table: "result_table",
        task_title: "task_title",
        gmodel:false,
        platform:"CC",
        old_sql: "",
        selectedFile:null,
        show_all_crowd_sqls: false,
        show_crowd_sqls:false,
      }
    },
    mounted() {
      var self = this;
      this.$nextTick(function () {
        self.refresh_table_list();
        self.refresh_crowd_sqls();
        // code that assumes this.$el is in-document
      });
      var subCatContainer = $(".sub-category-container");
      subCatContainer.scroll(function() {
        subCatContainer.scrollLeft($(this).scrollLeft());
      });
      //this.refresh_table_list();
    },
    computed: {
      boot_table() {
        var h = [];
        for(var i in this.show_data.header){
          h.push({
            field: this.show_data.header[i],
            title: this.show_data.header[i]
          });
        }
        return {
          columns: h,
          data: this.show_data.rows
        }
      },
      downloadUrl() {
        return store.state.apiUrl+"/download/"+this.table_name+".csv?token="+store.state.token;
      },
      is_crowd_sql() {
        return this.sqltype == "CrowdSQL";
      }
    },
    methods: {
      logout() {
        store.commit(types.SET_TOKEN, "");
      },
      process_to_percent(p) {
        return Math.floor(p * 100);
      },
      upload(e) {
        var fd = new FormData();
        if(!this.selectedFile) {
          alert("must choose csv file first.");
        } else {
          if (confirm('The csv file must contains table header at the first row. Are you sure you to submit the file?')) {
            var self = this;
            fd.append("file", this.selectedFile);
            console.log(fd);
            this.$http.post(store.state.apiUrl + "/upload/"+this.table_name, fd, {
              emulateJSON:true,
            }).then(function(res){
              self.show_msg = "Upload finished.";
              self.show_table_data(self.table_name);
            }, function(res){
              console.log(res);
              alert("Upload error!");
            });
          }
        }
      },
      download(e) {
      },
      fileSelected(e) {
        this.selectedFile = document.getElementById('fileToUpload').files[0];
        console.log(this.selectedFile)
      },
      del_crowd_sql(sql_id) {
        if (!confirm('Are your sure to remove the crowd SQL with id '+ sql_id +' ?')) {
          return;
        }
        var self = this;
        actions.removeCrowdSql(store, {
          sql_ids:[sql_id.toString()]
        }, function(data) {
          self.refresh_crowd_sqls();
        }, function(res) {
          if(res.data && res.data.remark) {
            alert("remove crowd sql error: " + res.data.remark);
          } else {
            alert("remove crowd sql error!");
          }
        })
      },
      refresh_crowd_sqls() {
        var self = this;
        actions.get_crowd_sqls(store, {}, function(data) {
          if(data.header) {
            self.crowd_sqls = data;
            self.show_msg = "";
            self.table_name = "";
            self.old_sql = "";
            self.sql = "";
            self.sqltype = "CrowdSQL";
            var h = [];
            for(var i in self.crowd_sqls.header) {
              h.push(self.crowd_sqls.header[i]);
            }
            h.splice(h.indexOf("current_sqlnode_id"), 1);
            h.splice(h.indexOf("db_name"), 1);
            self.show_data = {
              header: h,
              rows: self.crowd_sqls.rows
            }
            self.show_crowd_sqls = true;
          }
        }, function(res) {
          if(res.data && res.data.remark) {
            alert("get crowd sql list error: " + res.data.remark);
          } else {
            alert("get crowd sql list error!");
          }
        })
      },
      show_sql_data(t) {
        this.show_msg = "";
        this.table_name = "";
        this.old_sql = "";
        var h = [];
        for(var i in this.crowd_sqls.header) {
          h.push(this.crowd_sqls.header[i]);
        }
        h.splice(h.indexOf("current_sqlnode_id"), 1);
        h.splice(h.indexOf("db_name"), 1);
        this.show_data = {
          header: h,
          rows: [t]
        };
      },
      refresh_table_list() {
        var self = this;
        var list_table_sql = "show tables";
        actions.runNormalSql(store, {
          sql: list_table_sql,
          return_dict: false
        }, function(data) {
          var l = []
          if(data.header) {
            for(var i in data.rows) {
              l.push(data.rows[i][0]);
            }
            self.tables = l;
          }
        }, function(res) {
          if(res.data && res.data.remark) {
            alert("get table list error: " + res.data.remark);
          } else {
            alert("get table list error!");
          }
        })
      },
      show_table_data(t) {
        this.sql = "SELECT * FROM " + t + " LIMIT 10";
        this.sqltype = "NormalSQL";
        this.runSql();
        this.table_name = t;
      },
      runSql(e) {
        if(this.is_crowd_sql) {
          for(var i in this.crowd_sqls.rows) {
            if(this.crowd_sqls.rows[i]["result_table"] == this.result_table) {
              alert("Result Table Name CANNOT Be the same with existing tables!");
              return;
            }
          }
        }
        if(this.sql == this.old_sql) {
          if(!confirm('This SQL has just been submitted, are you sure to submit again?')) {
            return;
          }
        }
        this.old_sql = this.sql;
        var self = this;
        self.table_name = "";
        self.show_data = {
          header:[],
          rows: []
        };
        if(!this.is_crowd_sql) {
          self.show_msg = "SQL is running ... ";
          actions.runNormalSql(store, {
            sql: this.sql
          }, function(data) {
            if(data.header) {
              self.show_data = data;
              self.show_crowd_sqls = false;
            }
            self.show_msg = "SQL done. ";
            self.refresh_table_list();
          }, function(res) {
            if(res.data && res.data.remark) {
              self.show_msg = "SQL error: " + res.data.remark;
            } else {
              self.show_msg = "SQL error!";
            }
          })
        } else {
          actions.runCrowdSql(store, {
            sql: this.sql,
            result_table: this.result_table,
            task_title: this.task_title,
            platform: this.platform,
            gmodel: this.gmodel,
          }, function(data) {
            if(data.header) {
              self.show_data = data;
            }
            self.refresh_table_list();
            self.refresh_crowd_sqls();
            self.show_msg = "SQL submitted. ";
          }, function(res) {
            if(res.data && res.data.remark) {
              self.show_msg = "SQL error: " + res.data.remark;
            } else {
              self.show_msg = "SQL error!";
            }
          })
        }
      }
    }
  }
</script>
