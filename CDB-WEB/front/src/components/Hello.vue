<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <div class="container" style="margin: auto; width: 300px;">
    <div class="row">
      <div class="form-horizontal" role="form">
        <div class="form-group">
          <label for="inputEmail3" class="col-sm-4 control-label" style="padding-right: 10px">Username</label>
          <div class="col-sm-8" style="padding-left: 10px">
            <input type="email" v-model="username" class="form-control" id="inputEmail3" placeholder="Username">
          </div>
        </div>
        <div class="form-group">
          <label for="inputPassword3" class="col-sm-4 control-label" style="padding-right: 10px">Password</label>
          <div class="col-sm-8" style="padding-left: 10px">
            <input type="password" v-model="password" class="form-control" id="inputPassword3" placeholder="Password">
          </div>
        </div>
      </div>
      <div class="btn-group">
        <button @click="regist" class="btn btn-primary">register</button>
        <button @click="login" class="btn btn-success">login</button>
      </div>
    </div>
    </div>
  </div>
</template>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  h1, h2 {
    font-weight: normal;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
    margin: 0 10px;
  }

  a {
    color: #42b983;
  }
</style>

<script>
  import * as actions from '../store/actions'
  import store from '../store/index'

  export default {
    name: 'hello',
    data () {
      return {
        msg: 'Welcome to CrowdDB',
        username: "",
        password: ""
      }
    },
    methods: {
      regist(){
        actions.register(store, {
          username:this.username,
          password:this.password
        }, function(data) {
          alert("注册成功！");
        }, function(res) {
          alert("注册失败！");
        })
      },
      login(){
        actions.login(store, {
          username:this.username,
          password:this.password
        }, function(data) {
          alert("登录成功！");
        }, function(res) {
          alert("登录失败！");
        })
      }
    }
  }
</script>
